package com.github.kosbr.aws.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class SequenceBuilder {

    private final StringBuilder stringBuilder;

    public static SequenceBuilder create() {
        return new SequenceBuilder();
    }


    private SequenceBuilder() {
        stringBuilder = new StringBuilder();
    }

    public SequenceBuilder addLine(String line) {
        stringBuilder.append(line);
        stringBuilder.append("\n");
        return this;
    }

    public BufferedReader getAsBufferedReader() {
        final String inputCommands = stringBuilder.toString();
        try {
            final InputStream inputStream = new ByteArrayInputStream(
                    inputCommands.getBytes(StandardCharsets.UTF_8.name()));
            return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String getAsString() {
        return stringBuilder.toString();
    }

    public static String createSequence(String... strings) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            stringBuilder.append(strings[i]);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

}
