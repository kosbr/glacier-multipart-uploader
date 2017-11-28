package com.github.kosbr.aws.commands.configure;

import com.github.kosbr.aws.commands.config.use.ConfigUseHandler;
import com.github.kosbr.aws.commands.config.use.ConfigUseOptions;
import com.github.kosbr.aws.exception.config.ConfigurationNotFoundException;
import com.github.kosbr.aws.service.UploaderConfigurationService;
import com.github.kosbr.aws.util.PrintStreamWrapper;
import com.github.kosbr.aws.util.SequenceBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class ConfigUseCommandTest {

    @InjectMocks
    private ConfigUseHandler configUseHandler;

    @Mock
    private UploaderConfigurationService configurationServiceMock;

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testConfigurationNotFound() throws ConfigurationNotFoundException {

        final String configurationName = "test";

        doThrow(new ConfigurationNotFoundException(""))
                .when(configurationServiceMock).makeConfigurationActive(configurationName);

        final PrintStreamWrapper printStreamWrapper = new PrintStreamWrapper();

        final ConfigUseOptions options = new ConfigUseOptions();
        options.setConfigurationName(configurationName);
        configUseHandler.handle(options, printStreamWrapper.getPrintStream());

        verify(configurationServiceMock).makeConfigurationActive(configurationName);
        verifyNoMoreInteractions(configurationServiceMock);

        final String expectedOutput = SequenceBuilder.createSequence(
                "The configuration is not found"
        );

        Assert.assertEquals(expectedOutput, printStreamWrapper.getOutContent());

    }

    @Test
    public void testSuccessActivation() throws ConfigurationNotFoundException {

        final String configurationName = "test";

        final PrintStreamWrapper printStreamWrapper = new PrintStreamWrapper();

        final ConfigUseOptions options = new ConfigUseOptions();
        options.setConfigurationName(configurationName);
        configUseHandler.handle(options, printStreamWrapper.getPrintStream());

        verify(configurationServiceMock).makeConfigurationActive(configurationName);
        verifyNoMoreInteractions(configurationServiceMock);

        final String expectedOutput = SequenceBuilder.createSequence(
                "The active configuration now is " + configurationName
        );

        Assert.assertEquals(expectedOutput, printStreamWrapper.getOutContent());

    }
}
