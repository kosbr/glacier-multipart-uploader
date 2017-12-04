package com.github.kosbr.aws.service.client;

import com.amazonaws.services.glacier.AmazonGlacier;
import com.amazonaws.services.glacier.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.glacier.model.InitiateMultipartUploadResult;
import com.github.kosbr.aws.exception.config.NoActiveConfiguration;
import com.github.kosbr.aws.service.AWSGlacierHolder;
import com.github.kosbr.aws.service.impl.AWSClientServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class AWSClientServiceTest {

    private static final int PART_SIZE = 1024;
    private static final String VAULT_NAME = "myvault";
    private static final String DESCRIPTION = "my archive";
    private static final String UPLOAD_ID = "abc123";
    private static final String LOCATION = "location";

    @InjectMocks
    private AWSClientServiceImpl awsClientService;

    @Mock
    private AWSGlacierHolder glacierHolder;

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInitUpload() throws NoActiveConfiguration {
        final AmazonGlacier client = mock(AmazonGlacier.class);
        when(glacierHolder.getClient())
                .thenReturn(client);

        final InitiateMultipartUploadRequest expectedRequest = new InitiateMultipartUploadRequest()
                .withVaultName(VAULT_NAME)
                .withArchiveDescription(DESCRIPTION)
                .withPartSize(Integer.toString(PART_SIZE));

        final InitiateMultipartUploadResult mockResult = new InitiateMultipartUploadResult();
        mockResult.setLocation(LOCATION);
        mockResult.setUploadId(UPLOAD_ID);

        when(client.initiateMultipartUpload(expectedRequest))
                .thenReturn(mockResult);

        final String uploadId = awsClientService.initiateMultipartUpload(VAULT_NAME, DESCRIPTION, PART_SIZE);

        Assert.assertEquals(UPLOAD_ID, uploadId);

        verify(client).initiateMultipartUpload(expectedRequest);
        verifyNoMoreInteractions(client);
    }
}
