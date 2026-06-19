#!/bin/java --source 21

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class PropertiesConverter {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: java PropertiesConverter <inputDir> <outputDir>");
            System.exit(1);
        }

        Path inputDir = Paths.get(args[0]);
        Path outputDir = Paths.get(args[1]);

        if (!Files.isDirectory(inputDir)) {
            throw new IllegalArgumentException("Input must be a directory");
        }

        Files.walk(inputDir)
                .filter(p -> p.toString().endsWith(".properties"))
                .forEach(p -> {
                    try {
                        Path relative = inputDir.relativize(p);
                        Path outFile = outputDir.resolve(relative);
                        Files.createDirectories(outFile.getParent());
                        convertFile(p, outFile);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }

    private static void convertFile(Path in, Path out) throws IOException {
        try (
                BufferedReader reader = Files.newBufferedReader(in, StandardCharsets.UTF_8);
                BufferedWriter writer = Files.newBufferedWriter(out, StandardCharsets.ISO_8859_1)
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(escapePropertyLine(line));
                writer.newLine();
            }
        }
    }

    private static String escapePropertyLine(String line) {
        // Preserve comments and empty lines verbatim (except Unicode escaping)
        if (line.isEmpty() || line.startsWith("#") || line.startsWith("!")) {
            return escapeUnicode(line, false);
        }

        StringBuilder sb = new StringBuilder();
        boolean isKey = true;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (isKey && (c == '=' || c == ':' || Character.isWhitespace(c))) {
                sb.append(c);
                isKey = false;
                continue;
            }

            sb.append(escapeChar(c));
        }

        return sb.toString();
    }

    private static String escapeChar(char c) {
        switch (c) {
            case '\\': return "\\";
            case '\t': return "\t";
            case '\n': return "\n";
            case '\r': return "\r";
            case '\f': return "\f";
            default:
                if (c < 0x20 || c > 0x7E) {
                    return String.format("\\u%04x", (int) c);
                }
                return String.valueOf(c);
        }
    }

    private static String escapeUnicode(String s, boolean escapeSpaces) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c > 0x7E) {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
