package com.github.kosbr.aws.commands.config.deleteall;

import com.github.kosbr.aws.model.MultipartUploadInfo;
import com.github.kosbr.aws.model.UploaderConfiguration;
import com.github.kosbr.aws.service.UploadRegistrationService;
import com.github.kosbr.aws.service.UploaderConfigurationService;
import com.github.kosbr.cli.DialogCommandHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.List;

public class DeleteAllCommandHandler implements DialogCommandHandler<DeleteAllOptions> {

    private static final String Y = "y";

    @Autowired
    private UploadRegistrationService registrationService;

    @Autowired
    private UploaderConfigurationService configurationService;

    @Override
    public boolean handle(final DeleteAllOptions options, final PrintStream printStream,
                          final BufferedReader bufferedReader) {
        try {
            final List<MultipartUploadInfo> uploads = registrationService.getAllNotFinishedUploads();
            final List<UploaderConfiguration> configs = configurationService.findAll();
            printStream.println(uploads.size() + " unfinished uploads are going to be interrupted");
            printStream.println(configs.size() + " configurations are going to be deleted");
            printStream.println("Are you sure?");
            printStream.println("Print 'y' to confirm "
                    + "Otherwise the operation will not be performed");
            final String ans = bufferedReader.readLine();
            if (Y.equalsIgnoreCase(ans)) {
                registrationService.removeUploadInfos(uploads);
                configurationService.deleteAllConfigurations();
            } else {
                printStream.println("The operation was canceled");
            }
        } catch (Throwable e) {
            printStream.println("Error: " + e.getMessage());
        }

        return true;
    }

    @Override
    public Class<DeleteAllOptions> getOptionsClass() {
        return DeleteAllOptions.class;
    }
}
