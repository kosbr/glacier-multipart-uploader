package com.github.kosbr.aws.model;

/**
 * Keeps information about the upload process that has been already started.
 */
public class MultipartUploadInfo {

    private final String uploadId;

    private final int bufferSize;

    private final AWSArchiveDescription description;

    /**
     *
     * @param uploadId AWS upload id.
     * @param bufferSize The size of the one part in multipart upload.
     * @param description The description of the current upload.
     */
    public MultipartUploadInfo(final String uploadId,
                               final int bufferSize,
                               final AWSArchiveDescription description) {
        this.uploadId = uploadId;
        this.bufferSize = bufferSize;
        this.description = description;
    }

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
     * The description of the current upload.
     * @return
     */
    public AWSArchiveDescription getDescription() {
        return description;
    }
}
