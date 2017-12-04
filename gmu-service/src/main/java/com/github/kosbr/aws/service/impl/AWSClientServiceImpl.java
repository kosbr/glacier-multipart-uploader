package com.github.kosbr.aws.service.impl;

import com.amazonaws.services.glacier.AmazonGlacier;
import com.amazonaws.services.glacier.TreeHashGenerator;
import com.amazonaws.services.glacier.model.*;
import com.amazonaws.util.BinaryUtils;
import com.github.kosbr.aws.exception.config.NoActiveConfiguration;
import com.github.kosbr.aws.service.AWSClientService;
import com.github.kosbr.aws.service.AWSGlacierHolder;
import com.github.kosbr.aws.service.util.UploadPartObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class AWSClientServiceImpl implements AWSClientService {

    private static final int HUNDRED = 100;

    private static final int MAX_UPLOAD_PART_ATTEMPTS_NUMBER = 20;

    @Autowired
    private AWSGlacierHolder glacierHolder;

    @Override
    public String initiateMultipartUpload(final String vaultName, final String description, final int partSize)
            throws NoActiveConfiguration {
        final AmazonGlacier client = glacierHolder.getClient();
        final InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest()
                .withVaultName(vaultName)
                .withArchiveDescription(description)
                .withPartSize(Integer.toString(partSize));

        final InitiateMultipartUploadResult result = client.initiateMultipartUpload(request);
        return result.getUploadId();
    }

    @Override
    public CompleteMultipartUploadResult completeMultiPartUpload(final String uploadId, final String checksum,
                                                                 final String localPath, final String vaultName)
            throws NoActiveConfiguration {
        final AmazonGlacier client = glacierHolder.getClient();
        final File file = new File(localPath);
        final CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest()
                .withVaultName(vaultName)
                .withUploadId(uploadId)
                .withChecksum(checksum)
                .withArchiveSize(String.valueOf(file.length()));

        return client.completeMultipartUpload(compRequest);
    }

    @Override
    public String uploadParts(final String localPath,
                              final String uploadId,
                              final String vaultName,
                              final int bufferSize,
                              final long startPosition,
                              final UploadPartObserver partObserver)
            throws NoActiveConfiguration, IOException {
        final AmazonGlacier client = glacierHolder.getClient();
        final int filePosition = 0;
        long currentPosition = startPosition;
        final byte[] buffer = new byte[bufferSize];
        final List<byte[]> binaryChecksums = new LinkedList<byte[]>();

        final File file = new File(localPath);
        try (final FileInputStream fileToUpload = new FileInputStream(file)) {
            final long skipped = fileToUpload.skip(startPosition);
            if (skipped != startPosition) {
                throw new UnsupportedOperationException("Impossible to skip " + startPosition + " bytes");
            }
            String contentRange;
            int read;
            while (currentPosition < file.length()) {
                read = fileToUpload.read(buffer, filePosition, buffer.length);
                if (read == -1) {
                    break;
                }
                final byte[] bytesRead = Arrays.copyOf(buffer, read);

                contentRange = String.format("bytes %s-%s/*", currentPosition, currentPosition + read - 1);
                final String checksum = TreeHashGenerator.calculateTreeHash(new ByteArrayInputStream(bytesRead));
                final byte[] binaryChecksum = BinaryUtils.fromHex(checksum);
                binaryChecksums.add(binaryChecksum);

                //Upload part.
                final UploadMultipartPartRequest partRequest = new UploadMultipartPartRequest()
                        .withVaultName(vaultName)
                        .withBody(new ByteArrayInputStream(bytesRead))
                        .withChecksum(checksum)
                        .withRange(contentRange)
                        .withUploadId(uploadId);

                boolean success = false;

                int attemptsNumber = MAX_UPLOAD_PART_ATTEMPTS_NUMBER;

                while (!success && attemptsNumber > 0) {
                    try {
                        final UploadMultipartPartResult partResult = client.uploadMultipartPart(partRequest);
                        final int progressInPercents =
                                (int) (HUNDRED * ((double) (currentPosition + read) / file.length()));
                        partObserver.registerPartUpload(currentPosition, currentPosition + read - 1,
                                partResult.getChecksum(), progressInPercents);
                        success = true;
                    } catch (Throwable e) {
                        attemptsNumber--;
                    }
                }
                currentPosition = currentPosition + read;
            }
        }
        return TreeHashGenerator.calculateTreeHash(binaryChecksums);
    }

}
