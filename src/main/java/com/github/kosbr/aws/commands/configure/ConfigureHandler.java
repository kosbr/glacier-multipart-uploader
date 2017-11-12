package com.github.kosbr.aws.commands.configure;

import com.github.kosbr.aws.config.GlacierUploaderConfigurationHolder;
import com.github.kosbr.aws.config.GlacierUploaderConfigurationImpl;
import com.github.kosbr.cli.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.Properties;

public class ConfigureHandler implements CommandHandler<ConfigureOptions> {

    private static final String PROP_AWS_SERVICE_ENDPOINT = "aws.service.endpoint";
    private static final String PROP_AWS_SIGNING_REGION = "aws.signing.region";

    @Autowired
    private GlacierUploaderConfigurationHolder configurationHolder;

    @Override
    public boolean handle(final ConfigureOptions options, final PrintStream printStream) {
        final String filepath = options.getPath();
        final Properties prop = new Properties();

        try (InputStream input = new FileInputStream(filepath)) {
            prop.load(input);
            final GlacierUploaderConfigurationImpl config = readConfig(prop, printStream);
            if (config == null) {
                printStream.println("Configuration hasn't been updated due to errors");
                return true;
            }
            configurationHolder.setConfiguration(config);
            printStream.println("Configuration has been updated:");
            config.display(printStream);
            return true;
        } catch (FileNotFoundException e) {
            printStream.println("File " + filepath + " is not found");
        } catch (IOException e) {
            printStream.println("Error reading file " + filepath);
        }
        return true;
    }

    @Override
    public Class<ConfigureOptions> getOptionsClass() {
        return ConfigureOptions.class;
    }

    private boolean checkNotNull(final String property,
                              final String propertyName,
                              final PrintStream printStream) {
        if (property != null) {
            return true;
        }
        printStream.println("Error: property " + propertyName + " is null");
        return false;
    }

    private GlacierUploaderConfigurationImpl readConfig(
            final Properties properties, final PrintStream printStream) {
        final String serviceEndpoint = properties.getProperty(PROP_AWS_SERVICE_ENDPOINT);
        if (!checkNotNull(serviceEndpoint, PROP_AWS_SERVICE_ENDPOINT, printStream)) {
            return null;
        }
        final String signingRegion = properties.getProperty(PROP_AWS_SIGNING_REGION);
        if (!checkNotNull(signingRegion, PROP_AWS_SIGNING_REGION, printStream)) {
            return null;
        }

        final GlacierUploaderConfigurationImpl config = new GlacierUploaderConfigurationImpl();
        config.setServiceEndpoint(serviceEndpoint);
        config.setSigningRegion(signingRegion);
        return config;
    }
}
