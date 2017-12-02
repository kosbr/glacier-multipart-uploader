package com.github.kosbr.aws.commands.upload;

import com.beust.jcommander.Parameter;
import com.github.kosbr.cli.CommandOptions;

public class UploadArchiveOptions implements CommandOptions {

    @Parameter(names = "--vault", description = "Vault name", required = true)
    private String vault;

    @Parameter(names = "--archive", description = "Archive local path", required = true)
    private String archiveLocalPath;

    @Parameter(names = "--description", description = "Description", required = false)
    private String description;

    public String getVault() {
        return vault;
    }

    public void setVault(final String vault) {
        this.vault = vault;
    }

    public String getArchiveLocalPath() {
        return archiveLocalPath;
    }

    public void setArchiveLocalPath(final String archiveLocalPath) {
        this.archiveLocalPath = archiveLocalPath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
