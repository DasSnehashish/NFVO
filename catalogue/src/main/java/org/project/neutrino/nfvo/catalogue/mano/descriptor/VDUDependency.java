/*#############################################################################
 # Copyright (c) 2015.                                                        #
 #                                                                            #
 # This file is part of the OpenSDNCore project.                              #
 #############################################################################*/

package org.project.neutrino.nfvo.catalogue.mano.descriptor;

import org.project.neutrino.nfvo.catalogue.util.IdGenerator;

import javax.persistence.*;

/**
 * Created by lto on 06/02/15.
 *
 * Based on ETSI GS NFV-MAN 001 V1.1.1 (2014-12)
 */
@Entity
public class VDUDependency {
	@Id
	private String id = IdGenerator.createUUID();
	@Version
	private int version = 0;
	
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	@OneToOne(cascade={CascadeType.MERGE, CascadeType.PERSIST})
	private VirtualDeploymentUnit source;
	@OneToOne(cascade={CascadeType.MERGE, CascadeType.PERSIST})
    private VirtualDeploymentUnit target;

    public VirtualDeploymentUnit getTarget() {
        return target;
    }

    public void setTarget(VirtualDeploymentUnit target) {
        this.target = target;
    }

    public VirtualDeploymentUnit getSource() {
        return source;
    }

    public void setSource(VirtualDeploymentUnit source) {
        this.source = source;
    }

    public VDUDependency() {

    }
}