package com.github.kosbr.aws.model;

import javax.persistence.*;
import java.util.List;

/**
 * Keeps information about the upload process that has been already started.
 */
@Entity
public class MultipartUploadInfo {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String uploadId;

    @Column
    private Integer bufferSize;

    @Column
    private String localPath;

    @Column
    private String description;

    @Column
    private String vaultName;

    @ManyToOne
    @JoinColumn(name = "CONFIG_NAME")
    private UploaderConfiguration uploaderConfiguration;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FinishedUpload> finishedUploads;

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

    public UploaderConfiguration getUploaderConfiguration() {
        return uploaderConfiguration;
    }

    /**
     * The name of existing vault for uploading.
     * @return
     */
    public String getVaultName() {
        return vaultName;
    }

    public Long getId() {
        return id;
    }

    /**
     * @return List of the part uploads that already have been performed.
     */
    public List<FinishedUpload> getFinishedUploads() {
        return finishedUploads;
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

    public void setFinishedUploads(final List<FinishedUpload> finishedUploads) {
        this.finishedUploads = finishedUploads;
    }

    public void setUploaderConfiguration(final UploaderConfiguration uploaderConfiguration) {
        this.uploaderConfiguration = uploaderConfiguration;
    }
}
