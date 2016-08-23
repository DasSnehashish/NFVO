package org.openbaton.tosca.parser;

import org.openbaton.catalogue.mano.descriptor.*;
import org.openbaton.catalogue.nfvo.Configuration;
import org.openbaton.catalogue.nfvo.ConfigurationParameter;
import org.openbaton.tosca.templates.NSDTemplate;
import org.openbaton.tosca.templates.RelationshipsTemplate;
import org.openbaton.tosca.templates.TopologyTemplate.Nodes.CP.CPNodeTemplate;
import org.openbaton.tosca.templates.TopologyTemplate.Nodes.VDU.VDUNodeTemplate;
import org.openbaton.tosca.templates.TopologyTemplate.Nodes.VL.VLNodeTemplate;
import org.openbaton.tosca.templates.TopologyTemplate.Nodes.VNF.VNFConfigurations;
import org.openbaton.tosca.templates.TopologyTemplate.Nodes.VNF.VNFNodeTemplate;
import org.openbaton.tosca.templates.TopologyTemplate.TopologyTemplate;
import org.openbaton.tosca.templates.VNFDTemplate;

import java.util.*;

/**
 * Created by rvl on 17.08.16.
 */
public class TOSCAParser {

    public TOSCAParser(){}


    private InternalVirtualLink parseVL(VLNodeTemplate vlNodeTemplate){

        InternalVirtualLink vl = new InternalVirtualLink();

        vl.setName(vlNodeTemplate.getName());

        return vl;
    }

    private VNFDConnectionPoint parseCPTemplate(CPNodeTemplate cpTemplate){

        VNFDConnectionPoint cp = new VNFDConnectionPoint();

        cp.setVirtual_link_reference(cpTemplate.getRequirements().getVirtualLink());

        if(cpTemplate.getProperties() != null){
            if(cpTemplate.getProperties().getFloatingIP() != null){
                cp.setFloatingIp(cpTemplate.getProperties().getFloatingIP());
            }
        }

        return cp;
    }

    private VirtualDeploymentUnit parseVDUTemplate(VDUNodeTemplate vduTemplate, List<CPNodeTemplate> cps){

        VirtualDeploymentUnit vdu = new VirtualDeploymentUnit();
        vdu.setName(vduTemplate.getName());

        // ADD Settings
        vdu.setScale_in_out(vduTemplate.getProperties().getScale_in_out());
        vdu.setVimInstanceName(vduTemplate.getProperties().getVim_instance_name());
        vdu.setVm_image(new HashSet<String>(vduTemplate.getProperties().getVm_image()));
        vdu.setVm_image(new HashSet<String>(vduTemplate.getProperties().getVm_image()));

        // ADD VNF Connection Points
        Set<VNFComponent> vnfComponents = new HashSet<>();
        VNFComponent vnfc = new VNFComponent();
        Set<VNFDConnectionPoint> connectionPoints = new HashSet<>();

        for( CPNodeTemplate cp : cps){
            if(cp.getRequirements().getVirtualBinding().equals(vduTemplate.getName())){

                connectionPoints.add(parseCPTemplate(cp));
            }
        }
        vnfc.setConnection_point(connectionPoints);
        vnfComponents.add(vnfc);
        vdu.setVnfc(vnfComponents);

        return vdu;
    }

    private void parseRelationships(NetworkServiceDescriptor nsd,
                                             Map<String, RelationshipsTemplate> relationshipsTemplates){

        for(String key : relationshipsTemplates.keySet()) {
            VNFDependency vnfDependency = new VNFDependency();

            RelationshipsTemplate relationshipsTemplate = relationshipsTemplates.get(key);
            VirtualNetworkFunctionDescriptor vnfdSouce = new VirtualNetworkFunctionDescriptor();
            VirtualNetworkFunctionDescriptor vnfdTarget = new VirtualNetworkFunctionDescriptor();

            vnfdSouce.setName(relationshipsTemplate.getSource());
            vnfdTarget.setName(relationshipsTemplate.getTarget());

            vnfDependency.setSource(vnfdSouce);
            vnfDependency.setTarget(vnfdTarget);
            vnfDependency.setParameters(new HashSet<String>(relationshipsTemplate.getParameters()));

            nsd.getVnf_dependency().add(vnfDependency);
        }
    }

    private VirtualNetworkFunctionDescriptor parseVNFNode(VNFNodeTemplate vnf, TopologyTemplate topologyTemplate){

        VirtualNetworkFunctionDescriptor vnfd = new VirtualNetworkFunctionDescriptor();

        vnfd.setName(vnf.getName());
        vnfd.setVendor(vnf.getProperties().getVendor());
        vnfd.setVersion((Double.toString(vnf.getProperties().getVersion())));

        vnfd.setDeployment_flavour(vnf.getProperties().getDeploymentFlavourConverted());
        vnfd.setVnfPackageLocation(vnf.getProperties().getVnfPackageLocation());
        vnfd.setEndpoint(vnf.getProperties().getEndpoint());

        ArrayList<String> vduList = vnf.getRequirements().getVDUS();

        // ADD VDUs
        Set<VirtualDeploymentUnit> vdus = new HashSet<>();

        for(VDUNodeTemplate vdu : topologyTemplate.getVDUNodes()){

            if( vduList.contains(vdu.getName()) ){

                vdus.add(parseVDUTemplate(vdu, topologyTemplate.getCPNodes()));
            }
        }
        vnfd.setVdu(vdus);

        // ADD VLs
        ArrayList<String> vl_list = vnf.getRequirements().getVirtualLinks();
        Set<InternalVirtualLink> vls = new HashSet<>();

        for(VLNodeTemplate vl : topologyTemplate.getVLNodes()){

            if(vl_list.contains(vl.getName())){

                vls.add(parseVL(vl));
            }
        }
        vnfd.setVirtual_link(vls);

        vnfd.setLifecycle_event(vnf.getInterfaces().getOpLifecycle());

        //ADD CONFIGURATIONS
        if( vnf.getProperties().getConfigurations() != null ){
            Configuration configuration = new Configuration();
            configuration.setName(vnf.getProperties().getConfigurations().getName());

            Set<ConfigurationParameter> configurationParameters = new HashSet<>();

            for(HashMap<String, String> pair : vnf.getProperties().getConfigurations().getConfigurationParameters()){

                ConfigurationParameter configurationParameter = new ConfigurationParameter();
                configurationParameter.setConfKey((String) pair.keySet().toArray()[0]);
                configurationParameter.setValue((String) pair.values().toArray()[0]);
                configurationParameters.add(configurationParameter);
            }

            configuration.setConfigurationParameters(configurationParameters);
            vnfd.setConfigurations(configuration);
        }

        return vnfd;
    }

    public VirtualNetworkFunctionDescriptor parseVNFDTemplate(VNFDTemplate vnfdTemplate){

        VirtualNetworkFunctionDescriptor vnfd = new VirtualNetworkFunctionDescriptor();

        // ADD SETTINGS

        vnfd.setName(vnfdTemplate.getMetadata().getID());
        vnfd.setVendor(vnfdTemplate.getMetadata().getVendor());
        vnfd.setVersion(vnfdTemplate.getMetadata().getVersion());

        vnfd.setDeployment_flavour(vnfdTemplate.getInputs().getDeploymentFlavourConverted());
        vnfd.setVnfPackageLocation(vnfdTemplate.getInputs().getVnfPackageLocation());
        vnfd.setEndpoint(vnfdTemplate.getInputs().getEndpoint());

        // ADD VDUs
        Set<VirtualDeploymentUnit> vdus = new HashSet<>();

        for(VDUNodeTemplate vdu : vnfdTemplate.getTopology_template().getVDUNodes()){

            vdus.add(parseVDUTemplate(vdu, vnfdTemplate.getTopology_template().getCPNodes()));
        }
        vnfd.setVdu(vdus);

        // ADD VLs
        Set<InternalVirtualLink> vls = new HashSet<>();

        for(VLNodeTemplate vl : vnfdTemplate.getTopology_template().getVLNodes()){

            vls.add(parseVL(vl));
        }

        vnfd.setVirtual_link(vls);

        vnfd.setLifecycle_event(vnfdTemplate.getInputs().getInterfaces().getOpLifecycle());

        //ADD CONFIGURATIONS
        if( vnfdTemplate.getInputs().getConfigurations() != null ){

            VNFConfigurations configurations = new VNFConfigurations(vnfdTemplate.getInputs().getConfigurations());

            Configuration configuration = new Configuration();
            configuration.setName(configurations.getName());

            Set<ConfigurationParameter> configurationParameters = new HashSet<>();

            for(HashMap<String, String> pair : configurations.getConfigurationParameters()){

                ConfigurationParameter configurationParameter = new ConfigurationParameter();
                configurationParameter.setConfKey((String) pair.keySet().toArray()[0]);
                configurationParameter.setValue((String) pair.values().toArray()[0]);
                configurationParameters.add(configurationParameter);
            }

            configuration.setConfigurationParameters(configurationParameters);
            vnfd.setConfigurations(configuration);
        }

        return vnfd;
    }


    public NetworkServiceDescriptor parseNSDTemplate(NSDTemplate nsdTemplate){

        NetworkServiceDescriptor nsd = new NetworkServiceDescriptor();

        nsd.setVersion(nsdTemplate.getMetadata().getVersion());
        nsd.setVendor(nsdTemplate.getMetadata().getVendor());
        nsd.setName(nsdTemplate.getMetadata().getID());

        // ADD VNFDS

        for(VNFNodeTemplate vnfNodeTemplate : nsdTemplate.getTopology_template().getVNFNodes()){

            VirtualNetworkFunctionDescriptor vnf = parseVNFNode(vnfNodeTemplate, nsdTemplate.getTopology_template());

            nsd.getVnfd().add(vnf);
        }

        // ADD VLS
        nsd.setVld(new HashSet<VirtualLinkDescriptor>());

        for( VLNodeTemplate vlNode :  nsdTemplate.getTopology_template().getVLNodes()){

            VirtualLinkDescriptor vld = new VirtualLinkDescriptor();
            vld.setName(vlNode.getName());
            nsd.getVld().add(vld);
        }

        // ADD DEPENDENCIES
        parseRelationships(nsd, nsdTemplate.getRelationships_template());

        return nsd;
    }
}
