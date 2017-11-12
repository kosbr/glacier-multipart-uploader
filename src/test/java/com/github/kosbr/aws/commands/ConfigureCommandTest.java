package com.github.kosbr.aws.commands;

import com.github.kosbr.aws.SpringConfiguration;
import com.github.kosbr.aws.commands.configure.ConfigureHandler;
import com.github.kosbr.aws.commands.configure.ConfigureOptions;
import com.github.kosbr.aws.config.GlacierUploaderConfigurationHolder;
import com.github.kosbr.aws.util.PrintStreamWrapper;
import com.github.kosbr.aws.util.SequenceBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfiguration.class)
public class ConfigureCommandTest {

    @Autowired
    private ConfigureHandler configureHandler;

    @Autowired
    private GlacierUploaderConfigurationHolder holder;

    @Test
    public void testUpdateConfiguration() {
        final PrintStreamWrapper printStreamWrapper = new PrintStreamWrapper();
        final ConfigureOptions options = new ConfigureOptions();
        final String path = getClass().getClassLoader()
                .getResource("valid.config.properties").getPath();
        options.setPath(path);
        configureHandler.handle(options, printStreamWrapper.getPrintStream());

        Assert.assertEquals("https://glacier.us-east-2.amazonaws.com/", holder.getConfiguration().getServiceEndpoint());
        Assert.assertEquals("us-east-2", holder.getConfiguration().getSigningRegion());
    }

    @Test
    public void testFileNotFoundConfiguration() {
        final PrintStreamWrapper printStreamWrapper = new PrintStreamWrapper();
        final ConfigureOptions options = new ConfigureOptions();
        options.setPath("/file/doesnt/exist");
        configureHandler.handle(options, printStreamWrapper.getPrintStream());

        final String expectedOutput = SequenceBuilder.create()
                .addLine("File /file/doesnt/exist is not found")
                .getAsString();

        Assert.assertEquals(expectedOutput, printStreamWrapper.getOutContent());
    }

    @Test
    public void testInvalidConfiguration() {
        final PrintStreamWrapper printStreamWrapper = new PrintStreamWrapper();
        final ConfigureOptions options = new ConfigureOptions();
        final String path = getClass().getClassLoader()
                .getResource("invalid.config.properties").getPath();
        options.setPath(path);
        configureHandler.handle(options, printStreamWrapper.getPrintStream());

        final String expectedOutput = SequenceBuilder.create()
                .addLine("Error: property aws.signing.region is null")
                .addLine("Configuration hasn't been updated due to errors")
                .getAsString();

        Assert.assertEquals(expectedOutput, printStreamWrapper.getOutContent());
    }

}
