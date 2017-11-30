package com.github.kosbr.aws.service;

import com.amazonaws.services.glacier.AmazonGlacier;
import com.amazonaws.services.glacier.TreeHashGenerator;
import com.amazonaws.services.glacier.model.*;
import com.amazonaws.util.BinaryUtils;
import com.github.kosbr.aws.exception.config.NoActiveConfiguration;
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

    @Autowired
    private AWSGlacierHolder glacierHolder;

    @Override
    public String initiateMultipartUpload(final String vaultName, final int partSize)
            throws NoActiveConfiguration {
        final AmazonGlacier client = glacierHolder.getClient();
        final InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest()
                .withVaultName(vaultName)
                // todo use more specific name
                .withArchiveDescription("my archive " + (new Date()))
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


    // todo actually it won't work with not zero startPosition.
    // It is needed to add storing of checksum for every uploaded part and this
    // method must return the rest checksums that have been calculated while launching.
    // Then, consumer of this service should calculate checksum for all parts and use it in completion stage.
    @Override
    public String uploadParts(final String localPath,
                              final String uploadId,
                              final String vaultName,
                              final int bufferSize,
                              final long startPosition,
                              final UploadPartObserver partObserver)
            throws IOException, NoActiveConfiguration {
        //todo use try with resources
        final AmazonGlacier client = glacierHolder.getClient();
        final int filePosition = 0;
        long currentPosition = startPosition;
        final byte[] buffer = new byte[bufferSize];
        final List<byte[]> binaryChecksums = new LinkedList<byte[]>();

        final File file = new File(localPath);
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
                    .withVaultName(vaultName)
                    .withBody(new ByteArrayInputStream(bytesRead))
                    .withChecksum(checksum)
                    .withRange(contentRange)
                    .withUploadId(uploadId);

            boolean success = false;

            while (!success) {
                try {
                    final UploadMultipartPartResult partResult = client.uploadMultipartPart(partRequest);
                    final int progressInPercents =
                            (int) (HUNDRED * ((double) (currentPosition + read - 1) / file.length()));
                    partObserver.registerPartUpload(currentPosition, currentPosition + read - 1,
                            partResult.getChecksum(), progressInPercents);
                    success = true;
                } catch (Throwable e) {
                    // todo max attempts number
                    //printStream.println("error" + e.getMessage());
                }
            }
            currentPosition = currentPosition + read;
        }
        fileToUpload.close();
        return TreeHashGenerator.calculateTreeHash(binaryChecksums);
    }

}
