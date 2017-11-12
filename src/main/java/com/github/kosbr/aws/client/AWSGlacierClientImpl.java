package com.github.kosbr.aws.client;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.glacier.AmazonGlacier;
import com.amazonaws.services.glacier.AmazonGlacierClientBuilder;
import com.amazonaws.services.glacier.TreeHashGenerator;
import com.amazonaws.services.glacier.model.*;
import com.amazonaws.util.BinaryUtils;
import com.github.kosbr.aws.config.GlacierUploaderConfiguration;
import com.github.kosbr.aws.config.GlacierUploaderConfigurationHolder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class AWSGlacierClientImpl implements AWSGlacierClient {

    // This example works for part sizes up to 1 GB.
    private static final String PART_SIZE = "1048576";

    private AmazonGlacier client;

    @Autowired
    private GlacierUploaderConfigurationHolder configurationHolder;

    @Override
    public void uploadArchive(final AWSArchiveDescription description,
                              final PrintStream printStream) throws IOException, NoSuchAlgorithmException {
        provideClientReady();

        printStream.println("Uploading an archive.");
        final String uploadId = initiateMultipartUpload(description, printStream);
        final String checksum = uploadParts(uploadId, description, printStream);
        final String archiveId = completeMultiPartUpload(
                uploadId, checksum, description, printStream
        );
        printStream.println("Completed an archive. ArchiveId: " + archiveId);
    }



    private String initiateMultipartUpload(
            final AWSArchiveDescription description, final PrintStream printStream
    ) {
        // Initiate
        final InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest()
                .withVaultName(description.getVaultName())
                .withArchiveDescription("my archive " + (new Date()))
                .withPartSize(PART_SIZE);

        final InitiateMultipartUploadResult result = client.initiateMultipartUpload(request);
        printStream.println("ArchiveID: " + result.getUploadId());
        return result.getUploadId();
    }

    private String uploadParts(final String uploadId,
                               final AWSArchiveDescription description,
                               final PrintStream printStream)
            throws NoSuchAlgorithmException, IOException {

        final int filePosition = 0;
        long currentPosition = 0;
        final byte[] buffer = new byte[Integer.valueOf(PART_SIZE)];
        final List<byte[]> binaryChecksums = new LinkedList<byte[]>();

        final File file = new File(description.getLocalPath());
        final FileInputStream fileToUpload = new FileInputStream(file);
        String contentRange;
        int read = 0;
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
            printStream.println(contentRange);

            //Upload part.
            final UploadMultipartPartRequest partRequest = new UploadMultipartPartRequest()
                    .withVaultName(description.getVaultName())
                    .withBody(new ByteArrayInputStream(bytesRead))
                    .withChecksum(checksum)
                    .withRange(contentRange)
                    .withUploadId(uploadId);

            boolean success = false;

            while (!success) {
                try {
                    final UploadMultipartPartResult partResult = client.uploadMultipartPart(partRequest);
                    printStream.println("Part uploaded, checksum: " + partResult.getChecksum());
                    success = true;
                } catch (Throwable e) {
                    printStream.println("error" + e.getMessage());
                }
            }
            currentPosition = currentPosition + read;
        }
        fileToUpload.close();
        return TreeHashGenerator.calculateTreeHash(binaryChecksums);
    }

    private String completeMultiPartUpload(final String uploadId, final String checksum,
                                                  final AWSArchiveDescription description,
                                                  final PrintStream printStream)
            throws NoSuchAlgorithmException, IOException {

        final File file = new File(description.getLocalPath());

        final CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest()
                .withVaultName(description.getVaultName())
                .withUploadId(uploadId)
                .withChecksum(checksum)
                .withArchiveSize(String.valueOf(file.length()));

        final CompleteMultipartUploadResult compResult = client.completeMultipartUpload(compRequest);
        return compResult.getLocation();
    }

    private void provideClientReady() {
        if (client == null) {
            final ProfileCredentialsProvider credentials = new ProfileCredentialsProvider();
            final GlacierUploaderConfiguration configuration = configurationHolder.getConfiguration();
            client = AmazonGlacierClientBuilder.standard()
                    .withCredentials(credentials)
                    .withEndpointConfiguration(
                            new AwsClientBuilder.EndpointConfiguration(
                                    configuration.getServiceEndpoint(),
                                    configuration.getSigningRegion())
                    ).build();
        }
    }
}
