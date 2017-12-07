package com.github.kosbr.aws.commands.upload;

import com.amazonaws.services.glacier.model.CompleteMultipartUploadResult;
import com.github.kosbr.aws.exception.config.NoActiveConfiguration;
import com.github.kosbr.aws.exception.registration.UploadNotFoundException;
import com.github.kosbr.aws.model.MultipartUploadInfo;
import com.github.kosbr.aws.model.UploaderConfiguration;
import com.github.kosbr.aws.service.AWSClientService;
import com.github.kosbr.aws.service.FileDigestService;
import com.github.kosbr.aws.service.UploadRegistrationService;
import com.github.kosbr.aws.service.UploaderConfigurationService;
import com.github.kosbr.aws.service.util.UploadPartObserver;
import com.github.kosbr.aws.util.PrintStreamWrapper;
import com.github.kosbr.aws.util.SequenceBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class UploadCommandTest {

    private static final String LOCAL_PATH = "localPath";
    private static final String FILE_DESCRIPTION = "File description";
    private static final String VAULT_NAME = "my vault";
    private static final String SHA_256_HEX_STR = "sha256hexStr";
    private static final String UPLOAD_ID = "uploadId";
    private static final Long UPLOAD_INFO_ID = 777L;
    private static final String CONF_NAME = "myconf";
    private static final String CONF_REGION = "region";
    private static final String CONF_ENDPOINT = "serviceEndPoint";
    private static final int BEGIN_BYTE_1 = 0;
    private static final int BEGIN_BYTE_2 = 1048576;
    private static final int BEGIN_BYTE_3 = 2097151;
    private static final int END_BYTE_1 = 1048575;
    private static final int END_BYTE_2 = 2097151;
    private static final int END_BYTE_3 = 2097161;
    private static final int PERCENT_1 = 49;
    private static final int PERCENT_2 = 99;
    private static final int PERCENT_3 = 100;
    private static final String CHECK_SUM_3 = "checkSum3";
    private static final String CHECK_SUM_2 = "checkSum2";
    private static final String CHECK_SUM_1 = "checkSum1";
    private static final String CHECKSUM = "checkum";
    private static final String ALL_FILE_CHECKSUM = "all-file-checksum";

    private UploaderConfiguration configuration;

    @InjectMocks
    private UploadArchiveHandler uploadHandler;

    @Mock
    private AWSClientService client;

    @Mock
    private UploadRegistrationService registrationService;

    @Mock
    private UploaderConfigurationService configurationService;

    @Mock
    private FileDigestService fileDigestService;

    @Before
    public void prepare() {
        createConfiguration();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSuccessUpload() throws IOException, NoActiveConfiguration, UploadNotFoundException {
        final UploadArchiveOptions options = new UploadArchiveOptions();
        options.setArchiveLocalPath(LOCAL_PATH);
        options.setDescription(FILE_DESCRIPTION);
        options.setVault(VAULT_NAME);

        final PrintStreamWrapper printStreamWrapper = new PrintStreamWrapper();

        when(fileDigestService.calculateSha256Hex(anyString()))
                .thenReturn(SHA_256_HEX_STR);

        when(configurationService.findActiveConfiguration())
                .thenReturn(configuration);

        when(registrationService.registerUpload(any(MultipartUploadInfo.class)))
                .then((Answer<MultipartUploadInfo>) invocationOnMock -> {
                    final MultipartUploadInfo info = invocationOnMock.getArgumentAt(0, MultipartUploadInfo.class);
                    info.setId(UPLOAD_INFO_ID);
                    return info;
                });
        when(registrationService.registerAllPartsUploaded(anyLong()))
                .thenReturn(ALL_FILE_CHECKSUM);

        when(client.initiateMultipartUpload(anyString(), anyString(), anyInt()))
                .thenReturn(UPLOAD_ID);

        when(client.uploadParts(anyString(), anyString(), anyString(), anyInt(), anyInt(),
                any(UploadPartObserver.class)))
                .then((Answer<String>) invocationOnMock -> {
                    final UploadPartObserver observer = invocationOnMock.getArgumentAt(5, UploadPartObserver.class);
                    observer.registerPartUpload(BEGIN_BYTE_1, END_BYTE_1, CHECK_SUM_1, PERCENT_1);
                    observer.registerPartUpload(BEGIN_BYTE_2, END_BYTE_2, CHECK_SUM_2, PERCENT_2);
                    observer.registerPartUpload(BEGIN_BYTE_3, END_BYTE_3, CHECK_SUM_3, PERCENT_3);
                    return CHECKSUM;
                });

        when(client.completeMultiPartUpload(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(new CompleteMultipartUploadResult().withChecksum(ALL_FILE_CHECKSUM));

        uploadHandler.handle(options, printStreamWrapper.getPrintStream());

        verify(fileDigestService).calculateSha256Hex(LOCAL_PATH);
        verifyNoMoreInteractions(fileDigestService);

        verify(configurationService).findActiveConfiguration();
        verifyNoMoreInteractions(configurationService);


        final ArgumentCaptor<MultipartUploadInfo> requestCaptor =
                ArgumentCaptor.forClass(MultipartUploadInfo.class);
        verify(registrationService).registerUpload(requestCaptor.capture());
        final MultipartUploadInfo actualUploadInfo = requestCaptor.getValue();
        assertEqual(createExpectedUploadInfo(), actualUploadInfo);
        verify(registrationService).registerPartUpload(UPLOAD_INFO_ID, BEGIN_BYTE_1, END_BYTE_1, CHECK_SUM_1);
        verify(registrationService).registerPartUpload(UPLOAD_INFO_ID, BEGIN_BYTE_2, END_BYTE_2, CHECK_SUM_2);
        verify(registrationService).registerPartUpload(UPLOAD_INFO_ID, BEGIN_BYTE_3, END_BYTE_3, CHECK_SUM_3);
        verify(registrationService).registerAllPartsUploaded(UPLOAD_INFO_ID);
        verify(registrationService).removeUploadInfo(UPLOAD_INFO_ID);
        verifyNoMoreInteractions(registrationService);

        verify(client).initiateMultipartUpload(VAULT_NAME, FILE_DESCRIPTION,
                UploadArchiveHandler.PART_SIZE);
        verify(client).uploadParts(eq(LOCAL_PATH), eq(UPLOAD_ID), eq(VAULT_NAME),
                eq(UploadArchiveHandler.PART_SIZE), eq(0L), any(UploadPartObserver.class));
        verify(client).completeMultiPartUpload(UPLOAD_ID, ALL_FILE_CHECKSUM, LOCAL_PATH, VAULT_NAME);
        verifyNoMoreInteractions(client);
    }

    @Test
    public void testSuccessUploadOutput() throws IOException, NoActiveConfiguration, UploadNotFoundException {
        final UploadArchiveOptions options = new UploadArchiveOptions();
        options.setArchiveLocalPath(LOCAL_PATH);
        options.setDescription(FILE_DESCRIPTION);
        options.setVault(VAULT_NAME);

        final PrintStreamWrapper printStreamWrapper = new PrintStreamWrapper();

        when(fileDigestService.calculateSha256Hex(anyString()))
                .thenReturn(SHA_256_HEX_STR);

        when(configurationService.findActiveConfiguration())
                .thenReturn(configuration);

        when(registrationService.registerUpload(any(MultipartUploadInfo.class)))
                .then((Answer<MultipartUploadInfo>) invocationOnMock -> {
                    final MultipartUploadInfo info = invocationOnMock.getArgumentAt(0, MultipartUploadInfo.class);
                    info.setId(UPLOAD_INFO_ID);
                    return info;
                });
        when(registrationService.registerAllPartsUploaded(anyLong()))
                .thenReturn(ALL_FILE_CHECKSUM);

        when(client.initiateMultipartUpload(anyString(), anyString(), anyInt()))
                .thenReturn(UPLOAD_ID);

        when(client.uploadParts(anyString(), anyString(), anyString(), anyInt(), anyInt(),
                any(UploadPartObserver.class)))
                .then((Answer<String>) invocationOnMock -> {
                    final UploadPartObserver observer = invocationOnMock.getArgumentAt(5, UploadPartObserver.class);
                    observer.registerPartUpload(BEGIN_BYTE_1, END_BYTE_1, CHECK_SUM_1, PERCENT_1);
                    observer.registerPartUpload(BEGIN_BYTE_2, END_BYTE_2, CHECK_SUM_2, PERCENT_2);
                    observer.registerPartUpload(BEGIN_BYTE_3, END_BYTE_3, CHECK_SUM_3, PERCENT_3);
                    return CHECKSUM;
                });

        when(client.completeMultiPartUpload(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(new CompleteMultipartUploadResult().withChecksum(ALL_FILE_CHECKSUM));

        uploadHandler.handle(options, printStreamWrapper.getPrintStream());

        final String expectedOutput = SequenceBuilder.createSequence(
                "Part uploaded: " + BEGIN_BYTE_1 + "-" + END_BYTE_1,
                "Checksum: " + CHECK_SUM_1,
                "Progress: " + PERCENT_1 + " %",
                "Part uploaded: " + BEGIN_BYTE_2 + "-" + END_BYTE_2,
                "Checksum: " + CHECK_SUM_2,
                "Progress: " + PERCENT_2 + " %",
                "Part uploaded: " + BEGIN_BYTE_3 + "-" + END_BYTE_3,
                "Checksum: " + CHECK_SUM_3,
                "Progress: " + PERCENT_3 + " %",
                "Uploaded has been finished. Checksum: " + ALL_FILE_CHECKSUM
        );

        Assert.assertEquals(expectedOutput, printStreamWrapper.getOutContent());

    }

    private void createConfiguration() {
        configuration = new UploaderConfiguration();
        configuration.setServiceEndpoint(CONF_ENDPOINT);
        configuration.setSigningRegion(CONF_REGION);
        configuration.setActive(true);
        configuration.setName(CONF_NAME);
    }

    private void assertEqual(final MultipartUploadInfo expected, final MultipartUploadInfo actual) {
        Assert.assertEquals(expected.getBufferSize(), actual.getBufferSize());
        Assert.assertEquals(expected.getDescription(), actual.getDescription());
        Assert.assertEquals(expected.getLocalPath(), actual.getLocalPath());
        Assert.assertEquals(expected.getVaultName(), actual.getVaultName());
        Assert.assertEquals(expected.getUploadId(), actual.getUploadId());
        Assert.assertEquals(expected.getDigest(), actual.getDigest());
        Assert.assertEquals(expected.getUploaderConfiguration(), actual.getUploaderConfiguration());
    }

    private MultipartUploadInfo createExpectedUploadInfo() {
        final MultipartUploadInfo uploadInfo = new MultipartUploadInfo();
        uploadInfo.setBufferSize(UploadArchiveHandler.PART_SIZE);
        uploadInfo.setDescription(FILE_DESCRIPTION);
        uploadInfo.setLocalPath(LOCAL_PATH);
        uploadInfo.setVaultName(VAULT_NAME);
        uploadInfo.setUploadId(UPLOAD_ID);
        uploadInfo.setDigest(SHA_256_HEX_STR);
        uploadInfo.setUploaderConfiguration(configuration);
        return uploadInfo;
    }

}
