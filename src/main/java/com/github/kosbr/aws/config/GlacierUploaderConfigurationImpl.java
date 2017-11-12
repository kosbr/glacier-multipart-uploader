package com.github.kosbr.aws.config;

import java.io.PrintStream;

public class GlacierUploaderConfigurationImpl implements GlacierUploaderConfiguration {

    private String serviceEndpoint;

    private String signingRegion;

    public void setServiceEndpoint(final String serviceEndpoint) {
        this.serviceEndpoint = serviceEndpoint;
    }

    public void setSigningRegion(final String signingRegion) {
        this.signingRegion = signingRegion;
    }

    @Override
    public String getServiceEndpoint() {
        return serviceEndpoint;
    }

    @Override
    public String getSigningRegion() {
        return signingRegion;
    }

    public void display(final PrintStream printStream) {
        printStream.println("Service endpoint: " + serviceEndpoint);
        printStream.println("Signing region: " + signingRegion);
    }
}
