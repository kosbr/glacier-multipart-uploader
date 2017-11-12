package com.github.kosbr.aws;

import com.github.kosbr.aws.commands.configure.ConfigureHandler;
import com.github.kosbr.aws.commands.exit.ExitHandler;
import com.github.kosbr.aws.config.GlacierUploaderConfigurationHolder;
import com.github.kosbr.cli.ConsoleManager;
import com.github.kosbr.cli.registry.CommandRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfiguration {

    @Bean
    public CommandRegistry commandRegistry() {
        final CommandRegistry commandRegistry = new CommandRegistry();
        commandRegistry.registerCommand("exit", exitHandler());
        commandRegistry.registerCommand("configure", configureHandler());
        return commandRegistry;
    }

    @Bean
    public ConsoleManager consoleManager() {
        return new ConsoleManager(System.out, System.in, commandRegistry());
    }

    @Bean
    public GlacierUploaderConfigurationHolder glacierUploaderConfigurationHolder() {
        return new GlacierUploaderConfigurationHolder();
    }

    @Bean
    public ExitHandler exitHandler() {
        return new ExitHandler();
    }

    @Bean
    public ConfigureHandler configureHandler() {
        return new ConfigureHandler();
    }
}