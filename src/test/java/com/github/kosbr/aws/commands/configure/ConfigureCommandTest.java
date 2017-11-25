package com.github.kosbr.aws.commands.configure;

import com.github.kosbr.aws.HandlersConfiguration;
import com.github.kosbr.aws.commands.config.configure.ConfigureHandler;
import com.github.kosbr.aws.commands.config.configure.ConfigureOptions;
import com.github.kosbr.aws.exception.config.ConfigurationExistsException;
import com.github.kosbr.aws.exception.config.InvalidConfigurationException;
import com.github.kosbr.aws.model.UploaderConfiguration;
import com.github.kosbr.aws.service.UploaderConfigurationService;
import com.github.kosbr.aws.util.PrintStreamWrapper;
import com.github.kosbr.aws.util.SequenceBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HandlersConfiguration.class, MockConfigurationServiceConfiguration.class})
public class ConfigureCommandTest {

    @Autowired
    private ConfigureHandler configureHandler;

    @Autowired
    private UploaderConfigurationService configurationServiceMock;

    @Before
    public void prepare() {
        reset(configurationServiceMock);
    }

    @Test
    public void testSuccessCreateConfiguration() throws InvalidConfigurationException, ConfigurationExistsException {

        final String configurationName = "test";
        final String serviceEndPoint = "endpoint";
        final String signingRegion = "region";

        when(configurationServiceMock.findByName(configurationName))
                .thenReturn(Optional.empty());

        final BufferedReader bufferedReader = SequenceBuilder.create()
                .addLine(configurationName)
                .addLine(serviceEndPoint)
                .addLine(signingRegion)
                .addLine("")
                .getAsBufferedReader();

        final PrintStreamWrapper printStreamWrapper = new PrintStreamWrapper();
        final ConfigureOptions options = new ConfigureOptions();
        configureHandler.handle(options, printStreamWrapper.getPrintStream(), bufferedReader);

        final ArgumentCaptor<UploaderConfiguration> configArg = ArgumentCaptor.forClass(UploaderConfiguration.class);
        verify(configurationServiceMock).createConfiguration(configArg.capture());

        Assert.assertEquals(configurationName, configArg.getValue().getName());
        Assert.assertEquals(serviceEndPoint, configArg.getValue().getServiceEndpoint());
        Assert.assertEquals(signingRegion, configArg.getValue().getSigningRegion());
        Assert.assertNull(configArg.getValue().getActive());

        final String expectedOutput = SequenceBuilder.createSequence(
                "This command will ask you questions about new configuration",
                "Print exit if you want to interrupt configuration creating",
                "Enter the name of the new configuration",
                "Enter the service endpoint of the new configuration",
                "Enter the signing region of the new configuration",
                "The configuration with following parameters is going to be created",
                "Name: " + configurationName,
                "Service endpoint: " + serviceEndPoint,
                "Signing region: " + signingRegion,
                "Print exit if there is a mistake and launch the command one more time.",
                "If everything is ok, press Enter",
                "The configuration has been saved.",
                "If you would like to use it, make it active by 'use' command");

        Assert.assertEquals(expectedOutput, printStreamWrapper.getOutContent());
    }

}
