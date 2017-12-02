package com.github.kosbr.aws.commands.config.delete;

import com.github.kosbr.aws.exception.config.ConfigurationNotFoundException;
import com.github.kosbr.aws.model.MultipartUploadInfo;
import com.github.kosbr.aws.model.UploaderConfiguration;
import com.github.kosbr.aws.service.UploadRegistrationService;
import com.github.kosbr.aws.service.UploaderConfigurationService;
import com.github.kosbr.cli.DialogCommandHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;

public class DeleteConfigurationCommandHandler implements DialogCommandHandler<DeleteConfigurationOptions> {

    private static final String Y = "y";

    @Autowired
    private UploaderConfigurationService configurationService;

    @Autowired
    private UploadRegistrationService registrationService;

    @Override
    public boolean handle(final DeleteConfigurationOptions options,
                          final PrintStream printStream,
                          final BufferedReader bufferedReader) {
        final String configurationName = options.getConfigurationName();
        try {
            final Optional<UploaderConfiguration> maybeConfiguration =
                    configurationService.findByName(configurationName);
            if (!maybeConfiguration.isPresent()) {
                throw new ConfigurationNotFoundException("Configuration is not found");
            }
            final UploaderConfiguration configuration = maybeConfiguration.get();

            final List<MultipartUploadInfo> uploads = registrationService.findByConfiguration(configuration);

            if (!uploads.isEmpty()) {
                printStream.println("This configuration has" + uploads.size() +  " unfinished uploads");
                printStream.println("Print 'y' if you want to delete them. "
                        + "Otherwise configuration will not be removed");
                final String ans = bufferedReader.readLine();
                if (Y.equalsIgnoreCase(ans)) {
                    registrationService.removeUploadInfos(uploads);
                } else {
                    printStream.println("The configuration hasn't been deleted");
                }
            }

            configurationService.deleteConfiguration(configuration.getName());
            printStream.println("The configuration has been deleted");
        } catch (Throwable e) {
            printStream.println("Error: " + e.getMessage());
        }
        return true;
    }

    @Override
    public Class<DeleteConfigurationOptions> getOptionsClass() {
        return DeleteConfigurationOptions.class;
    }
}
