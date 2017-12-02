package com.github.kosbr.aws.service;

import java.io.IOException;

/**
 * The interface which provides methods to provide
 * safety from file changing.
 */
public interface FileDigestService {

    /**
     * Calculate sha256 hex for the file.
     * @param localPath File's path
     * @return
     */
    String calculateSha256Hex(String localPath) throws IOException;
}
