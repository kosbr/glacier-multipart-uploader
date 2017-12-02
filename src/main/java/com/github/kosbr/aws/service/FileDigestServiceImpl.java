package com.github.kosbr.aws.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class FileDigestServiceImpl implements FileDigestService {

    @Override
    public String calculateSha256Hex(final String localPath) throws IOException {
        try (final FileInputStream fis = new FileInputStream(new File(localPath))) {
            return DigestUtils.sha256Hex(fis);
        }
    }
}
