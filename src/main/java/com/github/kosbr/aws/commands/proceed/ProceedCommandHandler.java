package com.github.kosbr.aws.commands.proceed;

import com.amazonaws.services.glacier.model.CompleteMultipartUploadResult;
import com.github.kosbr.aws.exception.config.NoActiveConfiguration;
import com.github.kosbr.aws.exception.registration.UploadNotFoundException;
import com.github.kosbr.aws.model.MultipartUploadInfo;
import com.github.kosbr.aws.service.AWSClientService;
import com.github.kosbr.aws.service.UploadRegistrationService;
import com.github.kosbr.aws.service.UploaderConfigurationService;
import com.github.kosbr.cli.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.PrintStream;

public class ProceedCommandHandler implements CommandHandler<ProceedCommandOptions> {

    @Autowired
    private AWSClientService client;

    @Autowired
    private UploadRegistrationService registrationService;

    @Autowired
    private UploaderConfigurationService configurationService;

    @Override
    public boolean handle(final ProceedCommandOptions options, final PrintStream printStream) {

        try {
            final MultipartUploadInfo uploadInfo = registrationService.findUploadInfo(options.getUploadId());
            final long startPosition = registrationService.getCurrentUploadPosition(uploadInfo.getId());

            printStream.println("Make configuration " + uploadInfo.getUploaderConfiguration().getName() + " active");
            configurationService.makeConfigurationActive(uploadInfo.getUploaderConfiguration().getName());

            final File file = new File(uploadInfo.getLocalPath());

            if (startPosition < file.length()) {
                client.uploadParts(uploadInfo.getLocalPath(), uploadInfo.getUploadId(), uploadInfo.getVaultName(),
                        uploadInfo.getBufferSize(), startPosition,
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
            } else {
                printStream.println("All parts had been uploaded before. Completing...");
            }

            final String checksum = registrationService.registerAllPartsUploaded(uploadInfo.getId());
            final CompleteMultipartUploadResult result = client.completeMultiPartUpload(
                    uploadInfo.getUploadId(), checksum, uploadInfo.getLocalPath(), uploadInfo.getVaultName()
            );
            printStream.println("Uploaded has been finished. Checksum: " + result.getChecksum());
            registrationService.removeUploadInfo(uploadInfo.getId());
        } catch (NoActiveConfiguration e) {
            printStream.println("There is no active configuration");
        } catch (UploadNotFoundException e) {
            printStream.println("Upload with id" + options.getUploadId() + " is not found");
        } catch (Throwable e) {
            printStream.println("Error: " + e.getMessage());
        }
        return true;
    }

    @Override
    public Class<ProceedCommandOptions> getOptionsClass() {
        return ProceedCommandOptions.class;
    }
}
