package com.github.kosbr.aws.commands.upload;

import com.github.kosbr.aws.client.AWSArchiveDescription;
import com.github.kosbr.aws.client.AWSGlacierClient;
import com.github.kosbr.cli.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.PrintStream;

public class UploadArchiveHandler implements CommandHandler<UploadArchiveOptions> {

    @Autowired
    private AWSGlacierClient client;

    @Override
    public boolean handle(final UploadArchiveOptions options, final PrintStream printStream) {
        final AWSArchiveDescription description = new AWSArchiveDescription();
        description.setDescription(options.getDescription());
        description.setLocalPath(options.getArchiveLocalPath());
        description.setVaultName(options.getVault());

        try {
            client.uploadArchive(description, printStream);
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
