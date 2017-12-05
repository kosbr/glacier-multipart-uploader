package com.github.kosbr.aws.service.client;

import com.amazonaws.services.glacier.AmazonGlacier;
import com.amazonaws.services.glacier.model.UploadMultipartPartRequest;
import com.amazonaws.services.glacier.model.UploadMultipartPartResult;
import com.github.kosbr.aws.exception.config.NoActiveConfiguration;
import com.github.kosbr.aws.service.AWSGlacierHolder;
import com.github.kosbr.aws.service.impl.AWSClientServiceImpl;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AWSClientServiceUploadPartsTest {

    private static final String UPLOAD_ID = "upload12";
    private static final String VAULT_NAME = "myvault";
    private static final int BUFFER_SIZE = 12;

    private static final String PART_1_CHECKSUM = "e4b369df2f1b34de5e351eeef3effd68f5df96a4b93e0a98ec90479975e4b256";
    private static final String PART_1_RANGE = "bytes 0-11/*";
    private static final String PART_1_CONTENT = "qqqqqqqqqqqq";

    private static final String PART_2_CHECKSUM = "0ae13e6b652097ee5eaf572acd516196d02e6b2f447dd5bc1d998614c736ce9f";
    private static final String PART_2_RANGE = "bytes 12-23/*";
    private static final String PART_2_CONTENT = "tttttttttttt";

    private static final String PART_3_CHECKSUM = "27a84712e4b22c415fc544d55cdee82327a829f96d03329457f76ebf9af4dcaa";
    private static final String PART_3_RANGE = "bytes 24-25/*";
    private static final String PART_3_CONTENT = "ee";

    @InjectMocks
    private AWSClientServiceImpl awsClientService;

    @Mock
    private AWSGlacierHolder glacierHolder;

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUploadPartsFromTheBeginning() throws NoActiveConfiguration, IOException {
        final AmazonGlacier client = mock(AmazonGlacier.class);
        when(glacierHolder.getClient())
                .thenReturn(client);

        final String localPath = getClass().getClassLoader().getResource("big-file").getPath();

        when(client.uploadMultipartPart(any()))
                .thenReturn(createResult(PART_1_CHECKSUM))
                .thenReturn(createResult(PART_2_CHECKSUM))
                .thenReturn(createResult(PART_3_CHECKSUM));

        awsClientService.uploadParts(localPath, UPLOAD_ID, VAULT_NAME, BUFFER_SIZE, 0,
                (beginByte, endByte, checkSum, progressInPercents) -> {

        });

        final ArgumentCaptor<UploadMultipartPartRequest> requestCaptor =
                ArgumentCaptor.forClass(UploadMultipartPartRequest.class);
        verify(client, times(3)).uploadMultipartPart(requestCaptor.capture());

        verifyNoMoreInteractions(client);

        final List<UploadMultipartPartRequest> capturedRequests = requestCaptor.getAllValues();
        checkEqual(createExpectedRequest(PART_1_CHECKSUM, PART_1_RANGE, PART_1_CONTENT),
                capturedRequests.get(0));
        checkEqual(createExpectedRequest(PART_2_CHECKSUM, PART_2_RANGE, PART_2_CONTENT),
                capturedRequests.get(1));
        checkEqual(createExpectedRequest(PART_3_CHECKSUM, PART_3_RANGE, PART_3_CONTENT),
                capturedRequests.get(2));
    }

    private UploadMultipartPartRequest createExpectedRequest(final String checksum, final String range,
                                                             final String content) {
        return new UploadMultipartPartRequest()
                .withVaultName(VAULT_NAME)
                .withBody(new ByteArrayInputStream(content.getBytes()))
                .withChecksum(checksum)
                .withRange(range)
                .withUploadId(UPLOAD_ID);
    }

    private UploadMultipartPartResult createResult(final String checkSum) {
        return new UploadMultipartPartResult()
                .withChecksum(checkSum);
    }

    private void checkEqual(final UploadMultipartPartRequest expected, final UploadMultipartPartRequest actual)
            throws IOException {
        final InputStream expectedIS = expected.getBody();
        final InputStream actualIS = actual.getBody();

        Assert.assertTrue(IOUtils.contentEquals(expectedIS, actualIS));

        expected.setBody(null);
        actual.setBody(null);
        Assert.assertEquals(expected, actual);
    }
}
