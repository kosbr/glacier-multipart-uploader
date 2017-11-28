package com.github.kosbr.aws.model;

/**
 * The description of wished upload.
 */
public class AWSArchiveDescription {

    private final String localPath;

    private final String description;

    private final String vaultName;


    /**
     *
     * @param localPath Absolute local path of the file to be uploaded.
     * @param vaultName The name of existing vault for uploading.
     * @param description Archive's description.
     */
    public AWSArchiveDescription(final String localPath,
                                 final String vaultName,
                                 final String description) {
        this.localPath = localPath;
        this.description = description;
        this.vaultName = vaultName;
    }

    /**
     * Absolute local path of the file to be uploaded.
     * @return
     */
    public String getLocalPath() {
        return localPath;
    }

    /**
     * Archive's description.
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * The name of existing vault for uploading.
     * @return
     */
    public String getVaultName() {
        return vaultName;
    }
}
