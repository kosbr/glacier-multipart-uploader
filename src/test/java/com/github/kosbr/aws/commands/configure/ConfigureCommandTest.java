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
import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HandlersConfiguration.class, MockConfigurationServiceConfiguration.class})
public class ConfigureCommandTest {

    private static final String EXIT = "exit";

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

        verify(configurationServiceMock).findByName(configurationName);

        final ArgumentCaptor<UploaderConfiguration> configArg = ArgumentCaptor.forClass(UploaderConfiguration.class);
        verify(configurationServiceMock).createConfiguration(configArg.capture());

        Assert.assertEquals(configurationName, configArg.getValue().getName());
        Assert.assertEquals(serviceEndPoint, configArg.getValue().getServiceEndpoint());
        Assert.assertEquals(signingRegion, configArg.getValue().getSigningRegion());
        Assert.assertNull(configArg.getValue().getActive());

        verifyNoMoreInteractions(configurationServiceMock);

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

    @Test
    public void testCreateConfigurationInterruption() throws InvalidConfigurationException, ConfigurationExistsException {

        final String configurationName = "test";
        final String serviceEndPoint = "endpoint";

        when(configurationServiceMock.findByName(configurationName))
                .thenReturn(Optional.empty());

        final BufferedReader bufferedReader = SequenceBuilder.create()
                .addLine(configurationName)
                .addLine(serviceEndPoint)
                .addLine(EXIT)
                .getAsBufferedReader();

        final PrintStreamWrapper printStreamWrapper = new PrintStreamWrapper();
        final ConfigureOptions options = new ConfigureOptions();
        configureHandler.handle(options, printStreamWrapper.getPrintStream(), bufferedReader);

        verify(configurationServiceMock).findByName(configurationName);
        verifyNoMoreInteractions(configurationServiceMock);

        final String expectedOutput = SequenceBuilder.createSequence(
                "This command will ask you questions about new configuration",
                "Print exit if you want to interrupt configuration creating",
                "Enter the name of the new configuration",
                "Enter the service endpoint of the new configuration",
                "Enter the signing region of the new configuration",
                "The command has been interrupted"
        );

        Assert.assertEquals(expectedOutput, printStreamWrapper.getOutContent());
    }

    @Test
    public void testCreateConfigurationWithEmptyNameAttempt() throws InvalidConfigurationException, ConfigurationExistsException {

        final String invalidConfigurationName = "";
        final String configurationName = "test";
        final String serviceEndPoint = "endpoint";
        final String signingRegion = "region";

        when(configurationServiceMock.findByName(configurationName))
                .thenReturn(Optional.empty());

        final BufferedReader bufferedReader = SequenceBuilder.create()
                .addLine(invalidConfigurationName)
                .addLine(configurationName)
                .addLine(serviceEndPoint)
                .addLine(signingRegion)
                .addLine("")
                .getAsBufferedReader();

        final PrintStreamWrapper printStreamWrapper = new PrintStreamWrapper();
        final ConfigureOptions options = new ConfigureOptions();
        configureHandler.handle(options, printStreamWrapper.getPrintStream(), bufferedReader);

        verify(configurationServiceMock).findByName(configurationName);

        final ArgumentCaptor<UploaderConfiguration> configArg = ArgumentCaptor.forClass(UploaderConfiguration.class);
        verify(configurationServiceMock).createConfiguration(configArg.capture());

        Assert.assertEquals(configurationName, configArg.getValue().getName());
        Assert.assertEquals(serviceEndPoint, configArg.getValue().getServiceEndpoint());
        Assert.assertEquals(signingRegion, configArg.getValue().getSigningRegion());
        Assert.assertNull(configArg.getValue().getActive());

        verifyNoMoreInteractions(configurationServiceMock);

        final String expectedOutput = SequenceBuilder.createSequence(
                "This command will ask you questions about new configuration",
                "Print exit if you want to interrupt configuration creating",
                "Enter the name of the new configuration",
                "The value shouldn't be empty. Please, enter not empty value:",
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

    @Test
    public void testCreateConfigurationWithInvalidNameAttempt() throws InvalidConfigurationException, ConfigurationExistsException {

        final String invalidConfigurationName = "-df f";
        final String configurationName = "test";
        final String serviceEndPoint = "endpoint";
        final String signingRegion = "region";

        when(configurationServiceMock.findByName(configurationName))
                .thenReturn(Optional.empty());

        final BufferedReader bufferedReader = SequenceBuilder.create()
                .addLine(invalidConfigurationName)
                .addLine(configurationName)
                .addLine(serviceEndPoint)
                .addLine(signingRegion)
                .addLine("")
                .getAsBufferedReader();

        final PrintStreamWrapper printStreamWrapper = new PrintStreamWrapper();
        final ConfigureOptions options = new ConfigureOptions();
        configureHandler.handle(options, printStreamWrapper.getPrintStream(), bufferedReader);

        verify(configurationServiceMock).findByName(configurationName);

        final ArgumentCaptor<UploaderConfiguration> configArg = ArgumentCaptor.forClass(UploaderConfiguration.class);
        verify(configurationServiceMock).createConfiguration(configArg.capture());

        Assert.assertEquals(configurationName, configArg.getValue().getName());
        Assert.assertEquals(serviceEndPoint, configArg.getValue().getServiceEndpoint());
        Assert.assertEquals(signingRegion, configArg.getValue().getSigningRegion());
        Assert.assertNull(configArg.getValue().getActive());

        verifyNoMoreInteractions(configurationServiceMock);

        final String expectedOutput = SequenceBuilder.createSequence(
                "This command will ask you questions about new configuration",
                "Print exit if you want to interrupt configuration creating",
                "Enter the name of the new configuration",
                "The name must not contain space and dash symbols",
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

    @Test
    public void testSuccessCreateConfigurationWithEmptyServiceEndpointAttempt() throws InvalidConfigurationException, ConfigurationExistsException {

        final String configurationName = "test";
        final String invalidServiceEndpoint = "";
        final String serviceEndPoint = "endpoint";
        final String signingRegion = "region";

        when(configurationServiceMock.findByName(configurationName))
                .thenReturn(Optional.empty());

        final BufferedReader bufferedReader = SequenceBuilder.create()
                .addLine(configurationName)
                .addLine(invalidServiceEndpoint)
                .addLine(serviceEndPoint)
                .addLine(signingRegion)
                .addLine("")
                .getAsBufferedReader();

        final PrintStreamWrapper printStreamWrapper = new PrintStreamWrapper();
        final ConfigureOptions options = new ConfigureOptions();
        configureHandler.handle(options, printStreamWrapper.getPrintStream(), bufferedReader);

        verify(configurationServiceMock).findByName(configurationName);

        final ArgumentCaptor<UploaderConfiguration> configArg = ArgumentCaptor.forClass(UploaderConfiguration.class);
        verify(configurationServiceMock).createConfiguration(configArg.capture());

        Assert.assertEquals(configurationName, configArg.getValue().getName());
        Assert.assertEquals(serviceEndPoint, configArg.getValue().getServiceEndpoint());
        Assert.assertEquals(signingRegion, configArg.getValue().getSigningRegion());
        Assert.assertNull(configArg.getValue().getActive());

        verifyNoMoreInteractions(configurationServiceMock);

        final String expectedOutput = SequenceBuilder.createSequence(
                "This command will ask you questions about new configuration",
                "Print exit if you want to interrupt configuration creating",
                "Enter the name of the new configuration",
                "Enter the service endpoint of the new configuration",
                "The value shouldn't be empty. Please, enter not empty value:",
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

    @Test
    public void testCreateConfigurationWithUsedNameAttempt() throws InvalidConfigurationException, ConfigurationExistsException {

        final String usedConfigurationName = "used";
        final String configurationName = "test";
        final String serviceEndPoint = "endpoint";
        final String signingRegion = "region";

        when(configurationServiceMock.findByName(usedConfigurationName))
                .thenReturn(Optional.of(new UploaderConfiguration()));

        when(configurationServiceMock.findByName(configurationName))
                .thenReturn(Optional.empty());

        final BufferedReader bufferedReader = SequenceBuilder.create()
                .addLine(usedConfigurationName)
                .addLine(configurationName)
                .addLine(serviceEndPoint)
                .addLine(signingRegion)
                .addLine("")
                .getAsBufferedReader();

        final PrintStreamWrapper printStreamWrapper = new PrintStreamWrapper();
        final ConfigureOptions options = new ConfigureOptions();
        configureHandler.handle(options, printStreamWrapper.getPrintStream(), bufferedReader);

        verify(configurationServiceMock).findByName(usedConfigurationName);
        verify(configurationServiceMock).findByName(configurationName);

        final ArgumentCaptor<UploaderConfiguration> configArg = ArgumentCaptor.forClass(UploaderConfiguration.class);
        verify(configurationServiceMock).createConfiguration(configArg.capture());

        Assert.assertEquals(configurationName, configArg.getValue().getName());
        Assert.assertEquals(serviceEndPoint, configArg.getValue().getServiceEndpoint());
        Assert.assertEquals(signingRegion, configArg.getValue().getSigningRegion());
        Assert.assertNull(configArg.getValue().getActive());

        verifyNoMoreInteractions(configurationServiceMock);

        final String expectedOutput = SequenceBuilder.createSequence(
                "This command will ask you questions about new configuration",
                "Print exit if you want to interrupt configuration creating",
                "Enter the name of the new configuration",
                "The name used is already used",
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
