package com.github.kosbr.aws.client;

public class AWSArchiveDescription {

    private String localPath;

    private String description;

    private String vaultName;

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(final String localPath) {
        this.localPath = localPath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getVaultName() {
        return vaultName;
    }

    public void setVaultName(final String vaultName) {
        this.vaultName = vaultName;
    }
}
