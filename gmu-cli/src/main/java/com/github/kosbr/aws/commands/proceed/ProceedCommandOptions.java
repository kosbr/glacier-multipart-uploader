package com.github.kosbr.aws.commands.proceed;

import com.beust.jcommander.Parameter;
import com.github.kosbr.cli.CommandOptions;

public class ProceedCommandOptions implements CommandOptions {

    @Parameter(names = "--upload-id", description = "Upload name", required = true)
    private Long uploadId;

    public Long getUploadId() {
        return uploadId;
    }

    public void setUploadId(final Long uploadId) {
        this.uploadId = uploadId;
    }
}
