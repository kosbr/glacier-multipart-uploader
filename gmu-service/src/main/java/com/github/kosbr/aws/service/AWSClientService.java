package com.github.kosbr.aws.service;

import com.amazonaws.services.glacier.model.CompleteMultipartUploadResult;
import com.github.kosbr.aws.exception.config.NoActiveConfiguration;
import com.github.kosbr.aws.service.util.UploadPartObserver;

import java.io.IOException;

/**
 * This service is a wrapper for a AWS Glacier multipart API.
 * The upload process consists of 3 stages:
 * 1. Initiation
 * 2. Upload parts
 * 3. Completion
 * The API deals with all 3 parts. See the description of the methods.
 */
public interface AWSClientService {

    /**
     * Initiate an upload.
     * @param vaultName The name of existing vault for uploading.
     * @param description Archive description.
     * @param partSize The size of a part in bytes.
     * @return AWS upload id.
     * @throws NoActiveConfiguration
     */
    String initiateMultipartUpload(String vaultName, String description, int partSize) throws NoActiveConfiguration;

    /**
     * Complete multipart upload. It is considered, that all parts have been uploaded.
     * @param uploadId The AWS upload id, that should be given from the MultipartUploadInfo
     * @param checksum The checksum of all parts that have been uploaded.
     * @param localPath The absolute local path to the file.
     * @param vaultName The name of existing vault for uploading.
     * @return The final result of the upload.
     * @throws NoActiveConfiguration If there is no active configuration. See {@link UploaderConfigurationService}.
     */
    CompleteMultipartUploadResult completeMultiPartUpload(String uploadId, String checksum,
                                                          String localPath, String vaultName)
            throws NoActiveConfiguration;

    /**
     * Uploads the parts of the file one by one in natural order. In case of interruption,
     * it is possible to start upload not from the beginning of the file.
     * @param localPath The absolute local path to the file.
     * @param uploadId AWS upload Id.
     * @param vaultName The name of existing vault for uploading.
     * @param partSize The size of a part in bytes.
     * @param startPosition The number of byte the upload will start from.
     * @param partObserver The observer for registering part uploads.
     * @return The checksum of all parts that have been uploaded during this launching.
     * @throws IOException If there are some problems with file reading.
     * @throws NoActiveConfiguration If there is no active configuration. See {@link UploaderConfigurationService}.
     */
    String uploadParts(String localPath,
                       String uploadId,
                       String vaultName,
                       int partSize,
                       long startPosition,
                       UploadPartObserver partObserver) throws IOException, NoActiveConfiguration;

}
