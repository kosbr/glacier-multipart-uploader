package com.github.kosbr.aws.commands.upload;

import com.amazonaws.services.glacier.model.CompleteMultipartUploadResult;
import com.github.kosbr.aws.exception.config.NoActiveConfiguration;
import com.github.kosbr.aws.service.AWSClientService;
import com.github.kosbr.cli.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.PrintStream;

public class UploadArchiveHandler implements CommandHandler<UploadArchiveOptions> {

    // This example works for part sizes up to 1 GB.
    private static final Integer PART_SIZE = 1048576;

    @Autowired
    private AWSClientService client;

    @Override
    public boolean handle(final UploadArchiveOptions options, final PrintStream printStream) {
        try {
            final String uploadId = client.initiateMultipartUpload(options.getVault(), PART_SIZE);
            final String checksum = client.uploadParts(options.getArchiveLocalPath(), uploadId,
                    options.getVault(), PART_SIZE, 0, (beginByte, endByte, checkSum) -> {
                        printStream.println("Part uploaded");
                    });

            final CompleteMultipartUploadResult result = client.completeMultiPartUpload(
                    uploadId, checksum, options.getArchiveLocalPath(), options.getVault()
            );
            printStream.println("Uploaded has been finished. Checksum: " + result.getChecksum());
        } catch (NoActiveConfiguration e) {
            printStream.println("There is no active configuration");
        } catch (Throwable e) {
            printStream.println("Error: " + e.getMessage());
        }
        return true;
    }

    @Override
    public Class<UploadArchiveOptions> getOptionsClass() {
        return UploadArchiveOptions.class;
    }
}
