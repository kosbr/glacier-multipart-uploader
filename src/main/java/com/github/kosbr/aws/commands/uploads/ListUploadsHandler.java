package com.github.kosbr.aws.commands.uploads;

import com.github.kosbr.aws.exception.registration.UploadNotFoundException;
import com.github.kosbr.aws.model.MultipartUploadInfo;
import com.github.kosbr.aws.service.UploadRegistrationService;
import com.github.kosbr.cli.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

public class ListUploadsHandler implements CommandHandler<ListUploadsOptions> {

    private static final int HUNDRED = 100;

    @Autowired
    private UploadRegistrationService registrationService;

    @Override
    public boolean handle(final ListUploadsOptions options, final PrintStream printStream) {
        final List<MultipartUploadInfo> uploads = registrationService.getAllNotFinishedUploads();
        uploads.forEach(upload -> {
            printStream.println("Upload Id    : " + upload.getId());
            printStream.println("File         : " + upload.getLocalPath());
            printStream.println("Vault        : " + upload.getVaultName());
            printStream.println("Configuration: " + upload.getUploaderConfiguration().getName());
            printStream.println("Progress     : " + calculateProgress(upload));
        });
        return true;
    }

    private int calculateProgress(MultipartUploadInfo upload) {
        try {
            long currentPosition = registrationService.getCurrentUploadPosition(upload.getId());
            if (currentPosition == 0) {
                return 0;
            }
            final File file = new File(upload.getLocalPath());
            return (int) (HUNDRED * ((double) (currentPosition) / file.length()));
        } catch (UploadNotFoundException e) {
            throw new RuntimeException("Impossible to find " + upload.getId() + " upload");
        }
    }

    @Override
    public Class<ListUploadsOptions> getOptionsClass() {
        return ListUploadsOptions.class;
    }
}
