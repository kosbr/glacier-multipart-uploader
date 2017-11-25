package com.github.kosbr.aws.commands.config.configure;

import com.github.kosbr.aws.exception.InterruptedCommandException;
import com.github.kosbr.aws.exception.config.UploaderConfigurationException;
import com.github.kosbr.aws.model.UploaderConfiguration;
import com.github.kosbr.aws.service.UploaderConfigurationService;
import com.github.kosbr.cli.DialogCommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

public class ConfigureHandler implements DialogCommandHandler<ConfigureOptions> {

    private static final String EXIT = "exit";

    @Autowired
    private UploaderConfigurationService uploaderConfigurationService;

    @Override
    public boolean handle(final ConfigureOptions options,
                          final PrintStream printStream,
                          final BufferedReader bufferedReader) {
        printStream.println("This command will ask you questions about new configuration");
        printStream.println("Print exit if you want to interrupt configuration creating");
        try {
            final String name = getName(bufferedReader, printStream);
            final String serviceEndpoint = getNotEmptyString(bufferedReader, printStream, "service endpoint");
            final String signingRegion = getNotEmptyString(bufferedReader, printStream, "signing region");
            printStream.println("The configuration with following parameters is going to be created");
            printStream.println("Name: " + name);
            printStream.println("Service endpoint: " + serviceEndpoint);
            printStream.println("Signing region: " + signingRegion);
            printStream.println("Print exit if there is a mistake and launch the command one more time.");
            printStream.println("If everything is ok, press Enter");
            if (EXIT.equalsIgnoreCase(safeReadLine(bufferedReader))) {
                throw new InterruptedCommandException();
            }
            final UploaderConfiguration configuration = new UploaderConfiguration();
            configuration.setName(name);
            configuration.setServiceEndpoint(serviceEndpoint);
            configuration.setSigningRegion(signingRegion);
            uploaderConfigurationService.createConfiguration(configuration);
            printStream.println("The configuration has been saved.");
            printStream.println("If you would like to use it, make it active by 'use' command");
        } catch (InterruptedCommandException e) {
            printStream.println("The command has been interrupted");
        } catch (UploaderConfigurationException e) {
            // actually validation shouldn't allow get this point
            printStream.println("Error while creating configuration: " + e.getMessage());
        }
        return true;
    }

    @Override
    public Class<ConfigureOptions> getOptionsClass() {
        return ConfigureOptions.class;
    }

    private String getName(final BufferedReader bufferedReader,
                           final PrintStream printStream) throws InterruptedCommandException {
        printStream.println("Enter the name of the new configuration");
        final String wishedName = safeReadLine(bufferedReader);
        if (EXIT.equalsIgnoreCase(wishedName)) {
            throw new InterruptedCommandException();
        }
        if (uploaderConfigurationService.findByName(wishedName).isPresent()) {
            printStream.println("The name " + wishedName + " is already used");
            return getName(bufferedReader, printStream);
        } else {
            return wishedName;
        }
    }

    private String getNotEmptyString(final BufferedReader bufferedReader,
                                     final PrintStream printStream,
                                     final String nameStr) throws InterruptedCommandException {
        printStream.println("Enter the " + nameStr + " of the new configuration");
        final String wishedValue = safeReadLine(bufferedReader);
        if (EXIT.equals(wishedValue.toLowerCase())) {
            throw new InterruptedCommandException();
        }
        if (StringUtils.isEmpty(wishedValue)) {
            printStream.println("The value shouldn't be empty");
            return getNotEmptyString(bufferedReader, printStream, nameStr);
        } else {
            return wishedValue;
        }
    }

    private String safeReadLine(final BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
