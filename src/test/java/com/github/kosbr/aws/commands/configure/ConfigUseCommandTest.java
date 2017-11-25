package com.github.kosbr.aws.commands.configure;

import com.beust.jcommander.internal.Lists;
import com.github.kosbr.aws.HandlersConfiguration;
import com.github.kosbr.aws.commands.config.list.ConfigListOptions;
import com.github.kosbr.aws.commands.config.use.ConfigUseHandler;
import com.github.kosbr.aws.commands.config.use.ConfigUseOptions;
import com.github.kosbr.aws.exception.config.ConfigurationNotFoundException;
import com.github.kosbr.aws.service.UploaderConfigurationService;
import com.github.kosbr.aws.util.PrintStreamWrapper;
import com.github.kosbr.aws.util.SequenceBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HandlersConfiguration.class, MockConfigurationServiceConfiguration.class})
public class ConfigUseCommandTest {

    @Autowired
    private ConfigUseHandler configUseHandler;

    @Autowired
    private UploaderConfigurationService configurationServiceMock;

    @Before
    public void prepare() {
        reset(configurationServiceMock);
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
