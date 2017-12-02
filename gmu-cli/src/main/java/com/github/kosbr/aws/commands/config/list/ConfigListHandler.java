package com.github.kosbr.aws.commands.config.list;

import com.github.kosbr.aws.model.UploaderConfiguration;
import com.github.kosbr.aws.service.UploaderConfigurationService;
import com.github.kosbr.cli.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.PrintStream;
import java.util.List;

public class ConfigListHandler implements CommandHandler<ConfigListOptions> {

    @Autowired
    private UploaderConfigurationService configurationService;

    @Override
    public boolean handle(final ConfigListOptions options, final PrintStream printStream) {
        final List<UploaderConfiguration> configs = configurationService.findAll();
        if (configs.isEmpty()) {
            printStream.println("The are no configurations");
        } else {
            configs.forEach(config -> {
                displayConfig(printStream, config);
            });
        }
        return true;
    }

    @Override
    public Class<ConfigListOptions> getOptionsClass() {
        return ConfigListOptions.class;
    }

    private void displayConfig(final PrintStream printStream, final UploaderConfiguration config) {
        printStream.println("Configuration name:   " + config.getName());
        printStream.println("--- service endpoint: " + config.getServiceEndpoint());
        printStream.println("--- signing region:   " + config.getSigningRegion());
        printStream.println("--- active:           " + Boolean.TRUE.equals(config.getActive()));
        printStream.println("----------------------");
    }
}
