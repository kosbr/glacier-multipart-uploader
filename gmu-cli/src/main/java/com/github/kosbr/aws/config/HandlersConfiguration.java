package com.github.kosbr.aws.config;

import com.github.kosbr.aws.commands.config.configure.ConfigureHandler;
import com.github.kosbr.aws.commands.config.delete.DeleteConfigurationCommandHandler;
import com.github.kosbr.aws.commands.config.deleteall.DeleteAllCommandHandler;
import com.github.kosbr.aws.commands.config.list.ConfigListHandler;
import com.github.kosbr.aws.commands.config.use.ConfigUseHandler;
import com.github.kosbr.aws.commands.exit.ExitHandler;
import com.github.kosbr.aws.commands.proceed.ProceedCommandHandler;
import com.github.kosbr.aws.commands.upload.UploadArchiveHandler;
import com.github.kosbr.aws.commands.uploads.ListUploadsHandler;
import com.github.kosbr.cli.ConsoleManager;
import com.github.kosbr.cli.registry.CommandRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SuppressWarnings("checkstyle:classdataabstractioncoupling")
public class HandlersConfiguration {

    @Bean
    public CommandRegistry commandRegistry() {
        final CommandRegistry commandRegistry = new CommandRegistry();
        commandRegistry.registerCommand("exit", exitHandler());
        commandRegistry.registerCommand("configure", configureHandler());
        commandRegistry.registerCommand("config-list", configListHandler());
        commandRegistry.registerCommand("config-use", configUseHandler());
        commandRegistry.registerCommand("upload", uploadArchiveHandler());
        commandRegistry.registerCommand("uploads-list", listUploadsHandler());
        commandRegistry.registerCommand("upload-proceed", proceedCommandHandler());
        commandRegistry.registerCommand("config-delete", deleteConfigurationCommandHandler());
        commandRegistry.registerCommand("config-reset", deleteAllCommandHandler());
        return commandRegistry;
    }

    @Bean
    public ConsoleManager consoleManager() {
        return new ConsoleManager(System.out, System.in, commandRegistry());
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
    public ConfigUseHandler configUseHandler() {
        return new ConfigUseHandler();
    }

    @Bean
    public ConfigListHandler configListHandler() {
        return new ConfigListHandler();
    }


    @Bean
    public UploadArchiveHandler uploadArchiveHandler() {
        return new UploadArchiveHandler();
    }

    @Bean
    public ProceedCommandHandler proceedCommandHandler() {
        return new ProceedCommandHandler();
    }

    @Bean
    public ListUploadsHandler listUploadsHandler() {
        return new ListUploadsHandler();
    }

    @Bean
    public DeleteConfigurationCommandHandler deleteConfigurationCommandHandler() {
        return new DeleteConfigurationCommandHandler();
    }

    @Bean
    public DeleteAllCommandHandler deleteAllCommandHandler() {
        return new DeleteAllCommandHandler();
    }


}
