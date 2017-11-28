package com.github.kosbr.aws.commands.upload;

import com.amazonaws.services.glacier.model.CompleteMultipartUploadResult;
import com.github.kosbr.aws.model.AWSArchiveDescription;
import com.github.kosbr.aws.model.MultipartUploadInfo;
import com.github.kosbr.aws.exception.config.NoActiveConfiguration;
import com.github.kosbr.aws.service.AWSClientService;
import com.github.kosbr.cli.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.PrintStream;

public class UploadArchiveHandler implements CommandHandler<UploadArchiveOptions> {

    @Autowired
    private AWSClientService client;

    @Override
    public boolean handle(final UploadArchiveOptions options, final PrintStream printStream) {
        final AWSArchiveDescription description = new AWSArchiveDescription(
                options.getArchiveLocalPath(),
                options.getVault(),
                options.getDescription()
        );

        try {
            final MultipartUploadInfo uploadInfo = client.initiateMultipartUpload(description);
            final String checksum = client.uploadParts(uploadInfo, 0);
            final CompleteMultipartUploadResult result = client.completeMultiPartUpload(
                    uploadInfo.getUploadId(), checksum, description
            );
            // todo show some info
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
