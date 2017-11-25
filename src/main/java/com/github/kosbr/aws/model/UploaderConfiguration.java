package com.github.kosbr.aws.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UploaderConfiguration {

    @Column
    private String serviceEndpoint;

    @Column
    private String signingRegion;

    @Column
    @Id
    private String name;

    @Column
    private Boolean active;

    public String getServiceEndpoint() {
        return serviceEndpoint;
    }

    public void setServiceEndpoint(final String serviceEndpoint) {
        this.serviceEndpoint = serviceEndpoint;
    }

    public String getSigningRegion() {
        return signingRegion;
    }

    public void setSigningRegion(final String signingRegion) {
        this.signingRegion = signingRegion;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(final Boolean active) {
        this.active = active;
    }
}
