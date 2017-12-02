package com.github.kosbr.aws.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class PrintStreamWrapper {

    private final ByteArrayOutputStream outContent;

    private final PrintStream printStream;

    public PrintStreamWrapper() {
        outContent = new ByteArrayOutputStream();
        printStream = new PrintStream(outContent);
    }

    public PrintStream getPrintStream() {
        return printStream;
    }

    public String getOutContent() {
        return outContent.toString();
    }

}
