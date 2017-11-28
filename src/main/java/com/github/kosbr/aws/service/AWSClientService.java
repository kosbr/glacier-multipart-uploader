package com.github.kosbr.aws.service;

import com.amazonaws.services.glacier.model.CompleteMultipartUploadResult;
import com.github.kosbr.aws.model.AWSArchiveDescription;
import com.github.kosbr.aws.model.MultipartUploadInfo;
import com.github.kosbr.aws.exception.config.NoActiveConfiguration;

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
     * @param description The description of wished upload.
     * @return The information about this upload, which must be used in next stages.
     * @throws NoActiveConfiguration
     */
    MultipartUploadInfo initiateMultipartUpload(AWSArchiveDescription description) throws NoActiveConfiguration;

    /**
     * Complete multipart upload. It is considered, that all parts have been uploaded.
     * @param uploadId The AWS upload id, that should be given from the {@link MultipartUploadInfo}
     * @param checksum The checksum of all parts that have been uploaded. (The result of uploadParts method)
     * @param description The description of wished upload.
     * @return The final result of the upload.
     * @throws NoActiveConfiguration If there is no active configuration. See {@link UploaderConfigurationService}.
     */
    CompleteMultipartUploadResult completeMultiPartUpload(String uploadId, String checksum,
                                                          AWSArchiveDescription description)
            throws NoActiveConfiguration;

    /**
     * Uploads the parts of the file one by one in natural order. In case of interruption,
     * it is possible to start upload not from the beginning of the file.
     * @param uploadInfo The upload info that must be given from the initiation stage.
     * @param startPosition The number of byte the upload will start from.
     * @return The checksum of all parts that have been uploaded during this launching.
     * @throws IOException If there are some problems with file reading.
     * @throws NoActiveConfiguration If there is no active configuration. See {@link UploaderConfigurationService}.
     */
    String uploadParts(MultipartUploadInfo uploadInfo, long startPosition) throws IOException, NoActiveConfiguration;

}
