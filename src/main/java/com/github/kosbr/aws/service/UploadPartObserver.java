package com.github.kosbr.aws.service;

/**
 * Observer for uploading parts.
 */
public interface UploadPartObserver {

    /**
     * After a part has been uploaded, this method is called.
     * @param beginByte The start position of uploaded part in the file.
     * @param endByte The end position of uploaded part in the file.
     * @param checkSum The checksum of the uploaded part.
     */
    void registerPartUpload(long beginByte, long endByte, String checkSum);
}
