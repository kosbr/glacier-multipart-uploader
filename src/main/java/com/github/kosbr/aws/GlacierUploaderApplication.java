package com.github.kosbr.aws;

import com.github.kosbr.aws.config.SpringConfiguration;
import com.github.kosbr.cli.ConsoleManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public final class GlacierUploaderApplication {

    private GlacierUploaderApplication() {

    }

    public static void main(final String[] args) {
        final ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
        final ConsoleManager consoleManager = context.getBean(ConsoleManager.class);
        consoleManager.start();
    }
}
