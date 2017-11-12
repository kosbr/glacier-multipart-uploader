package com.github.kosbr.aws.commands.configure;

import com.beust.jcommander.Parameter;
import com.github.kosbr.cli.CommandOptions;

public class ConfigureOptions implements CommandOptions {

    @Parameter(names = "--path", description = "Config file path", required = true)
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }
}
