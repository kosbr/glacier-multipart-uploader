package com.github.kosbr.aws.service.holder;

import com.amazonaws.services.glacier.AmazonGlacier;
import com.github.kosbr.aws.exception.config.NoActiveConfiguration;
import com.github.kosbr.aws.model.UploaderConfiguration;
import com.github.kosbr.aws.service.UploaderConfigurationService;
import com.github.kosbr.aws.service.impl.AWSGlacierHolderImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class AWSGlacierHolderTest {

    @InjectMocks
    private AWSGlacierHolderImpl awsGlacierHolder;

    @Mock
    private UploaderConfigurationService configurationService;

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testReturnTheSameClient() throws NoActiveConfiguration {
        final UploaderConfiguration activeConfiguration = new UploaderConfiguration();
        activeConfiguration.setName("myconf");
        activeConfiguration.setSigningRegion("us-east-2");
        activeConfiguration.setServiceEndpoint("us-east-2.com");

        when(configurationService.findActiveConfiguration())
                .thenReturn(activeConfiguration);

        final AmazonGlacier client = awsGlacierHolder.getClient();
        final AmazonGlacier client2 = awsGlacierHolder.getClient();

        verify(configurationService, times(2)).findActiveConfiguration();
        verifyNoMoreInteractions(configurationService);
        //return the same object
        Assert.assertTrue(client == client2);

    }

    @Test
    public void testReturnOtherClient() throws NoActiveConfiguration {
        final UploaderConfiguration activeConfiguration = new UploaderConfiguration();
        activeConfiguration.setName("myconf");
        activeConfiguration.setSigningRegion("us-east-2");
        activeConfiguration.setServiceEndpoint("us-east-2.com");

        when(configurationService.findActiveConfiguration())
                .thenReturn(activeConfiguration);

        final AmazonGlacier client = awsGlacierHolder.getClient();

        final UploaderConfiguration activeConfiguration2 = new UploaderConfiguration();
        activeConfiguration2.setName("anotherConf");
        activeConfiguration2.setSigningRegion("us-west-2");
        activeConfiguration2.setServiceEndpoint("us-west-2.com");

        when(configurationService.findActiveConfiguration())
                .thenReturn(activeConfiguration2);

        final AmazonGlacier client2 = awsGlacierHolder.getClient();


        verify(configurationService, times(2)).findActiveConfiguration();
        verifyNoMoreInteractions(configurationService);

        //return another object
        Assert.assertTrue(client != client2);

    }

}
