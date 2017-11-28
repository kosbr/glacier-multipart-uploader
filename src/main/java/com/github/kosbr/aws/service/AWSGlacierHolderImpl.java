package com.github.kosbr.aws.service;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.glacier.AmazonGlacier;
import com.amazonaws.services.glacier.AmazonGlacierClientBuilder;
import com.github.kosbr.aws.exception.config.NoActiveConfiguration;
import com.github.kosbr.aws.model.UploaderConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Holds the client for AWS API access.
 */
@Service
@Transactional
public class AWSGlacierHolderImpl implements AWSGlacierHolder {

    @Autowired
    private UploaderConfigurationService uploaderConfigurationService;

    // client is stored here
    private AmazonGlacier client;

    // the configuration name the client is stored for
    private String clientConfigName;

    /**
     * Knowing the current active configuration, it returns the client.
     * @return AmazonGlacier client.
     * @throws NoActiveConfiguration If there is no active configuration.
     */
    @Override
    public AmazonGlacier getClient() throws NoActiveConfiguration {
        provideClientReady();
        return client;
    }

    private void provideClientReady() throws NoActiveConfiguration {

        final UploaderConfiguration activeConfiguration = uploaderConfigurationService.findActiveConfiguration();

        // if client is null (first launch) or active configuration has been changed
        if (client == null || !activeConfiguration.getName().equals(clientConfigName)) {
            final ProfileCredentialsProvider credentials = new ProfileCredentialsProvider();
            client = AmazonGlacierClientBuilder.standard()
                    .withCredentials(credentials)
                    .withEndpointConfiguration(
                            new AwsClientBuilder.EndpointConfiguration(
                                    activeConfiguration.getServiceEndpoint(),
                                    activeConfiguration.getSigningRegion())
                    ).build();
            clientConfigName = activeConfiguration.getName();
        }
    }
}
