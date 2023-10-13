package com.pleisto;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * Environment resolves environment-specific project metadata.
 *
 * forked from https://github.com/apache/incubator-opendal/blob/main/bindings/java/src/main/java/org/apache/opendal/Environment.java
 */
enum Environment {
    INSTANCE;

    public static final String UNKNOWN = "<unknown>";
    private String classifier = UNKNOWN;
    private String projectVersion = UNKNOWN;

    static {
        ClassLoader classLoader = Environment.class.getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("bindings.properties")) {
            final Properties properties = new Properties();
            properties.load(is);
            INSTANCE.projectVersion = properties.getProperty("project.version", UNKNOWN);
        } catch (IOException e) {
            throw new UncheckedIOException("cannot load environment properties file", e);
        }

        final StringBuilder classifier = new StringBuilder();
        final String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("windows")) {
            classifier.append("windows");
        } else if (os.startsWith("mac")) {
            classifier.append("osx");
        } else {
            classifier.append("linux");
        }
        classifier.append("-");
        final String arch = System.getProperty("os.arch").toLowerCase();
        if (arch.equals("aarch64")) {
            classifier.append("aarch_64");
        } else {
            classifier.append("x86_64");
        }
        INSTANCE.classifier = classifier.toString();
    }

    public static String getClassifier() {
        return INSTANCE.classifier;
    }

    public static String getVersion() {
        return INSTANCE.projectVersion;
    }
}
