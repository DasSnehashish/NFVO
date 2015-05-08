package org.project.neutrino.nfvo.catalogue.mano.common;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Version;

import org.project.neutrino.nfvo.catalogue.util.IdGenerator;

/**
 * Created by lto on 06/02/15.
 *
 * Based on ETSI GS NFV-MAN 001 V1.1.1 (2014-12)
 */
@Entity
public class CostituentVNF {

	@Id
	private String id = IdGenerator.createUUID();
	@Version
	private int version = 0; 
	
    /**
     * Reference to a VNFD declared as vnfd in the Network Service via vnf:id.
     * TODO this must be an id. So a String. For doing that we must do manually
     * */
    private String vnf_reference;
    /**
     * References a VNF flavour (vnfd:deployment_flavour:id) to be used for this service flavour, see clause 6.2.1.3.1
     * TODO this must be an id. So a String. For doing that we must do manually
     * */
    private String vnf_flavour_id_reference;
    /**
     * Represents the redundancy of instances, for example,"active" or "standby".
     * */
    @Enumerated(EnumType.STRING)
    private RedundancyModel redundancy_model;
    /**
     * Specifies the placemen policy between this instance and other instances, if any.
     * TODO: think to a more appropriate type
     * */
    private String affinity;
    /**
     * Represents the capabilities of the VNF instances. An example of capability is instance capacity (e.g. capability = 50 %* NS capacity).
     * */
    private String capability;

    /**
     * Number of VNF instances satisfying this service assurance. For a Gold flavour of the vEPC Network Service that
     * needs to satisfy an assurance of 96K cps, 2 instances of the vMME VNFs will be required.
     * */
    private int number_of_instances;

    public CostituentVNF() {
    }

    public String getVnf_reference() {
        return vnf_reference;
    }

    public void setVnf_reference(String vnf_reference) {
        this.vnf_reference = vnf_reference;
    }

    public String getVnf_flavour_id_reference() {
        return vnf_flavour_id_reference;
    }

    public void setVnf_flavour_id_reference(String vnf_flavour_id_reference) {
        this.vnf_flavour_id_reference = vnf_flavour_id_reference;
    }

    public RedundancyModel getRedundancy_model() {
        return redundancy_model;
    }

    public void setRedundancy_model(RedundancyModel redundancy_model) {
        this.redundancy_model = redundancy_model;
    }

    public String getAffinity() {
        return affinity;
    }

    public void setAffinity(String affinity) {
        this.affinity = affinity;
    }

    public String getCapability() {
        return capability;
    }

    public void setCapability(String capability) {
        this.capability = capability;
    }

    public int getNumber_of_instances() {
        return number_of_instances;
    }

    public void setNumber_of_instances(int number_of_instances) {
        this.number_of_instances = number_of_instances;
    }
}