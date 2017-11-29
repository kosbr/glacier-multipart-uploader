package com.github.kosbr.aws.model;

/**
 * Keeps information about the upload process that has been already started.
 */
public class MultipartUploadInfo {

    private String uploadId;

    private Integer bufferSize;

    private String localPath;

    private String description;

    private String vaultName;

    /**
     * AWS upload id.
     * @return
     */
    public String getUploadId() {
        return uploadId;
    }

    /**
     * The size of the one part in multipart upload.
     * @return
     */
    public int getBufferSize() {
        return bufferSize;
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

    public void setUploadId(final String uploadId) {
        this.uploadId = uploadId;
    }

    public void setBufferSize(final Integer bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void setLocalPath(final String localPath) {
        this.localPath = localPath;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setVaultName(final String vaultName) {
        this.vaultName = vaultName;
    }
}
