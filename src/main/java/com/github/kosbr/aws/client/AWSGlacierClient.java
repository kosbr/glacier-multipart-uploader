package com.github.kosbr.aws.client;

import java.io.IOException;
import java.io.PrintStream;
import java.security.NoSuchAlgorithmException;

public interface AWSGlacierClient {

    void uploadArchive(AWSArchiveDescription archiveDescription, PrintStream printStream)
            throws IOException, NoSuchAlgorithmException;
}
