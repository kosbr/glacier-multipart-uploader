package com.github.kosbr.aws.commands.config.use;

import com.beust.jcommander.Parameter;
import com.github.kosbr.cli.CommandOptions;

public class ConfigUseOptions implements CommandOptions {

    @Parameter(names = "--name", description = "Configuration name", required = true)
    private String configurationName;

    public String getConfigurationName() {
        return configurationName;
    }

    public void setConfigurationName(final String configurationName) {
        this.configurationName = configurationName;
    }
}
