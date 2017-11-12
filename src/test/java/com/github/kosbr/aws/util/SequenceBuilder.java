package com.github.kosbr.aws.util;

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
