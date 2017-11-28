package com.github.kosbr.aws.service;

import com.amazonaws.services.glacier.AmazonGlacier;
import com.amazonaws.services.glacier.TreeHashGenerator;
import com.amazonaws.services.glacier.model.*;
import com.amazonaws.util.BinaryUtils;
import com.github.kosbr.aws.exception.config.NoActiveConfiguration;
import com.github.kosbr.aws.model.AWSArchiveDescription;
import com.github.kosbr.aws.model.MultipartUploadInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class AWSClientServiceImpl implements AWSClientService {

    // This example works for part sizes up to 1 GB.
    private static final Integer PART_SIZE = 1048576;

    @Autowired
    private AWSGlacierHolder glacierHolder;

    @Override
    public MultipartUploadInfo initiateMultipartUpload(final AWSArchiveDescription description)
            throws NoActiveConfiguration {
        final AmazonGlacier client = glacierHolder.getClient();
        final InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest()
                .withVaultName(description.getVaultName())
                // todo use more specific name
                .withArchiveDescription("my archive " + (new Date()))
                .withPartSize(PART_SIZE.toString());

        final InitiateMultipartUploadResult result = client.initiateMultipartUpload(request);
        return new MultipartUploadInfo(result.getUploadId(), PART_SIZE, description);
    }

    @Override
    public CompleteMultipartUploadResult completeMultiPartUpload(final String uploadId, final String checksum,
                                                                 final AWSArchiveDescription description)
            throws NoActiveConfiguration {
        final AmazonGlacier client = glacierHolder.getClient();
        final File file = new File(description.getLocalPath());
        final CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest()
                .withVaultName(description.getVaultName())
                .withUploadId(uploadId)
                .withChecksum(checksum)
                .withArchiveSize(String.valueOf(file.length()));

        return client.completeMultipartUpload(compRequest);
    }


    // todo actually it won't work with not zero startPosition.
    // It is needed to add storing of checksum for every uploaded part and this
    // method must return the rest checksums that have been calculated while launching.
    // Then, consumer of this service should calculate checksum for all parts and use it in completion stage.
    @Override
    public String uploadParts(final MultipartUploadInfo uploadInfo, final long startPosition)
            throws IOException, NoActiveConfiguration {
        //todo use try with resources
        final AmazonGlacier client = glacierHolder.getClient();
        final int filePosition = 0;
        long currentPosition = startPosition;
        final byte[] buffer = new byte[uploadInfo.getBufferSize()];
        final List<byte[]> binaryChecksums = new LinkedList<byte[]>();

        final File file = new File(uploadInfo.getDescription().getLocalPath());
        final FileInputStream fileToUpload = new FileInputStream(file);
        fileToUpload.skip(startPosition);
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
                    .withVaultName(uploadInfo.getDescription().getVaultName())
                    .withBody(new ByteArrayInputStream(bytesRead))
                    .withChecksum(checksum)
                    .withRange(contentRange)
                    .withUploadId(uploadInfo.getUploadId());

            boolean success = false;

            while (!success) {
                try {
                    final UploadMultipartPartResult partResult = client.uploadMultipartPart(partRequest);
                    //printStream.println("Part uploaded, checksum: " + partResult.getChecksum());
                    success = true;
                } catch (Throwable e) {
                    //printStream.println("error" + e.getMessage());
                }
            }
            currentPosition = currentPosition + read;
        }
        fileToUpload.close();
        return TreeHashGenerator.calculateTreeHash(binaryChecksums);
    }

}
