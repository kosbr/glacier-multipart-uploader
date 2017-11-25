package com.github.kosbr.aws.commands.config.use;

import com.github.kosbr.aws.exception.config.ConfigurationNotFoundException;
import com.github.kosbr.aws.service.UploaderConfigurationService;
import com.github.kosbr.cli.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.PrintStream;

public class ConfigUseHandler implements CommandHandler<ConfigUseOptions> {

    @Autowired
    private UploaderConfigurationService uploaderConfigurationService;

    @Override
    public boolean handle(final ConfigUseOptions options,
                          final PrintStream printStream) {
        final String configurationName = options.getConfigurationName();
        try {
            uploaderConfigurationService.makeConfigurationActive(configurationName);
            printStream.println("The active configuration now is " + configurationName);
        } catch (ConfigurationNotFoundException e) {
            printStream.println("The configuration is not found");
        }
        return true;
    }

    @Override
    public Class<ConfigUseOptions> getOptionsClass() {
        return ConfigUseOptions.class;
    }
}
