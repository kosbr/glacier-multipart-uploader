package com.github.kosbr.aws.commands.exit;

import com.github.kosbr.cli.CommandHandler;
import org.springframework.stereotype.Component;

import java.io.PrintStream;

@Component
public class ExitHandler implements CommandHandler<ExitOptions> {

    @Override
    public boolean handle(ExitOptions options, PrintStream printStream) {
        printStream.println("Good bye");
        return false;
    }

    @Override
    public Class<ExitOptions> getOptionsClass() {
        return ExitOptions.class;
    }
}
