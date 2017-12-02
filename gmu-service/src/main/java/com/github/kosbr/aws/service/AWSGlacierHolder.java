package com.github.kosbr.aws.service;

import com.amazonaws.services.glacier.AmazonGlacier;
import com.github.kosbr.aws.exception.config.NoActiveConfiguration;

/**
 * Holds the client for AWS API access.
 */
public interface AWSGlacierHolder {

    /**
     * Knowing the current active configuration, it returns the client.
     * @return AmazonGlacier client.
     * @throws NoActiveConfiguration If there is no active configuration.
     */
    AmazonGlacier getClient() throws NoActiveConfiguration;
}
