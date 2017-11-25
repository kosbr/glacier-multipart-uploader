package com.github.kosbr.aws;

import com.github.kosbr.aws.client.AWSGlacierClient;
import com.github.kosbr.aws.client.AWSGlacierClientImpl;
import com.github.kosbr.aws.commands.config.configure.ConfigureHandler;
import com.github.kosbr.aws.commands.config.list.ConfigListHandler;
import com.github.kosbr.aws.commands.exit.ExitHandler;
import com.github.kosbr.aws.commands.upload.UploadArchiveHandler;
import com.github.kosbr.cli.ConsoleManager;
import com.github.kosbr.cli.registry.CommandRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(DAOConfiguration.class)
@ComponentScan(basePackages = "com.github.kosbr.aws.service")
public class SpringConfiguration {

    @Bean
    public CommandRegistry commandRegistry() {
        final CommandRegistry commandRegistry = new CommandRegistry();
        commandRegistry.registerCommand("exit", exitHandler());
        commandRegistry.registerCommand("configure", configureHandler());
        commandRegistry.registerCommand("configlist", configListHandler());
        commandRegistry.registerCommand("upload", uploadArchiveHandler());
        return commandRegistry;
    }

    @Bean
    public ConsoleManager consoleManager() {
        return new ConsoleManager(System.out, System.in, commandRegistry());
    }

    @Bean
    public AWSGlacierClient awsGlacierClient() {
        return new AWSGlacierClientImpl();
    }

    @Bean
    public ExitHandler exitHandler() {
        return new ExitHandler();
    }

    @Bean
    public ConfigureHandler configureHandler() {
        return new ConfigureHandler();
    }

    @Bean
    public ConfigListHandler configListHandler() {
        return new ConfigListHandler();
    }

    @Bean
    public UploadArchiveHandler uploadArchiveHandler() {
        return new UploadArchiveHandler();
    }
}
