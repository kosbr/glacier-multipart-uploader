package com.github.kosbr.aws.commands.upload;

import com.amazonaws.services.glacier.model.CompleteMultipartUploadResult;
import com.github.kosbr.aws.exception.config.NoActiveConfiguration;
import com.github.kosbr.aws.exception.registration.UploadNotFoundException;
import com.github.kosbr.aws.model.MultipartUploadInfo;
import com.github.kosbr.aws.service.AWSClientService;
import com.github.kosbr.aws.service.UploadRegistrationService;
import com.github.kosbr.aws.service.UploaderConfigurationService;
import com.github.kosbr.cli.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.PrintStream;

public class UploadArchiveHandler implements CommandHandler<UploadArchiveOptions> {

    // This example works for part sizes up to 1 GB.
    private static final Integer PART_SIZE = 1048576;

    @Autowired
    private AWSClientService client;

    @Autowired
    private UploadRegistrationService registrationService;

    @Autowired
    private UploaderConfigurationService configurationService;

    @Override
    public boolean handle(final UploadArchiveOptions options, final PrintStream printStream) {
        try {
            final String uploadId = client.initiateMultipartUpload(options.getVault(), PART_SIZE);

            final MultipartUploadInfo uploadInfo = new MultipartUploadInfo();
            uploadInfo.setBufferSize(PART_SIZE);
            uploadInfo.setDescription(options.getDescription());
            uploadInfo.setLocalPath(options.getArchiveLocalPath());
            uploadInfo.setVaultName(options.getVault());
            uploadInfo.setUploadId(uploadId);
            uploadInfo.setUploaderConfiguration(configurationService.findActiveConfiguration());
            registrationService.registerUpload(uploadInfo);


            client.uploadParts(options.getArchiveLocalPath(), uploadId, options.getVault(), PART_SIZE, 0,
                (beginByte, endByte, checkSum, progressInPercents) -> {
                    try {
                        registrationService.registerPartUpload(uploadInfo.getId(), beginByte, endByte, checkSum);
                        printStream.println("Part uploaded: " + beginByte + "-" + endByte);
                        printStream.println("Checksum: " + checkSum);
                        printStream.println("Progress: " + progressInPercents + " %");
                    } catch (UploadNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });

            final String checksum = registrationService.registerAllPartsUploaded(uploadInfo.getId());
            final CompleteMultipartUploadResult result = client.completeMultiPartUpload(
                    uploadId, checksum, uploadInfo.getLocalPath(), uploadInfo.getVaultName()
            );
            printStream.println("Uploaded has been finished. Checksum: " + result.getChecksum());
            registrationService.removeUploadInfo(uploadInfo.getId());
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
