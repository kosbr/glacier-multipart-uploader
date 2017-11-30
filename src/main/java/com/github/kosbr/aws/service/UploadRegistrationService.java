package com.github.kosbr.aws.service;

import com.github.kosbr.aws.exception.registration.UploadNotFoundException;
import com.github.kosbr.aws.model.MultipartUploadInfo;

public interface UploadRegistrationService {

    /**
     * Registration of the upload process. It should be called after successful AWS API request for the
     * upload initiation.
     * @param multipartUploadInfo Detached filled object with uploadInfo.
     * @return The same persisted object.
     */
    MultipartUploadInfo registerUpload(MultipartUploadInfo multipartUploadInfo);

    /**
     * Registration of the part uploading. It should be called after every part is uploaded.
     * @param uploadInfoId The id of corresponding {@link MultipartUploadInfo} that was given in registerUpload method.
     * @param begin The position on the first byte uploaded in the part.
     * @param end The position on the last byte uploaded in the part.
     * @param checkSum Checksum of the uploaded part.
     */
    void registerPartUpload(long uploadInfoId, long begin, long end, String checkSum) throws UploadNotFoundException;

    /**
     * Registration of the fact, that all parts have been uploaded.
     * @param uploadInfoId The id of corresponding {@link MultipartUploadInfo} that was given in registerUpload method.
     * @return The final checksum of the all parts. It should be passed to the last API request to AWS for completion.
     */
    String registerAllPartsUploaded(long uploadInfoId) throws UploadNotFoundException;

    /**
     * Removes all stored data about upload. It doesn't call any AWS API services.
     * @param uploadInfoId The id of corresponding {@link MultipartUploadInfo} that was given in registerUpload method.
     */
    void removeUploadInfo(long uploadInfoId) throws UploadNotFoundException;

    /**
     * Returns the byte position, the following upload should start with.
     * @param uploadInfoId The id of corresponding {@link MultipartUploadInfo} that was given in registerUpload method.
     * @return The byte position, the following upload should start with.
     */
    long getCurrentUploadPosition(long uploadInfoId) throws UploadNotFoundException;

}
