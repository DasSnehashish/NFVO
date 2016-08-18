package org.openbaton.tosca.templates.TopologyTemplate.Nodes.VDU;

import org.openbaton.tosca.templates.TopologyTemplate.Nodes.NodeTemplate;

import java.util.Map;

/**
 * Created by rvl on 17.08.16.
 */
public class VDUNodeTemplate {

    private String type;
    private String name;
    private VDUCapabilities capabilities = null;
    private VDUArtifact vduArtifact = null;
    private Object interfaces = null;

    public VDUNodeTemplate(NodeTemplate nodeTemplate, String name){

        this.name = name;
        this.type = nodeTemplate.getType();

        if(nodeTemplate.getArtifacts() != null){

            Map<String, Object> artifactMap = (Map<String, Object>) nodeTemplate.getArtifacts();

            vduArtifact = new VDUArtifact(artifactMap.values().toArray()[0]);
        }

        if(nodeTemplate.getCapabilities() != null){

            capabilities = new VDUCapabilities(nodeTemplate.getCapabilities());
        }


    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public VDUCapabilities getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(VDUCapabilities capabilities) {
        this.capabilities = capabilities;
    }

    public VDUArtifact getVduArtifact() {
        return vduArtifact;
    }

    public void setVduArtifact(VDUArtifact vduArtifact) {
        this.vduArtifact = vduArtifact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return "Node: \n" +
                "type: " + type + "\n" +
                "Capabilities: " + capabilities + "\n" +
                "Interfaces: " + interfaces + "\n" +
                "Artifacts: " + vduArtifact + "\n";
    }


}
