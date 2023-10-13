# Flappy Java Bindings

[![License](https://img.shields.io/github/license/pleisto/flappy.svg)](https://raw.githubusercontent.com/pleisto/flappy/main/LICENSE)
[![CI](https://img.shields.io/github/actions/workflow/status/pleisto/flappy/java-bindings-test.yml.svg)](https://github.com/pleisto/flappy/actions/workflows/java-bindings-test.yml)
[![Maven metadata URL](https://img.shields.io/maven-metadata/v.svg?metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Fcom%2Fpleisto%2Fflappy-java-bindings%2Fmaven-metadata.xml&color=blue)](https://central.sonatype.com/artifact/com.pleisto/flappy-java-bindings)
[![Documentation](https://javadoc.io/badge/com.pleisto/flappy-java-bindings.svg)](https://javadoc.io/doc/com.pleisto/flappy-java-bindings)

## Getting Started

This project is released for multiple platforms that you can use a classifier to specify the platform you are building the application on.

### Maven

Generally, you can first add the `os-maven-plugin` for automatically detect the classifier based on your platform:

```xml
<build>
<extensions>
  <extension>
    <groupId>kr.motd.maven</groupId>
    <artifactId>os-maven-plugin</artifactId>
    <version>1.7.0</version>
  </extension>
</extensions>
</build>
```

Then add the dependency to `flappy-java-bindings` as following:

```xml
<dependencies>
<dependency>
  <groupId>com.pleisto</groupId>
  <artifactId>flappy-java-bindings</artifactId>
  <version>${flappy-java-bindings.version}</version>
</dependency>
<dependency>
  <groupId>com.pleisto</groupId>
  <artifactId>flappy-java-bindings</artifactId>
  <version>${flappy-java-bindings.version}</version>
  <classifier>${os.detected.classifier}</classifier>
</dependency>
</dependencies>
```

### Gradle

For Gradle, you can first add the `com.google.osdetector` for automatically detect the classifier based on your platform:

```groovy
plugins {
    id "com.google.osdetector" version "1.7.3"
}
```

Then add the dependency to `flappy-java-bindings` as following:

```groovy
dependencies {
    implementation "com.pleisto:flappy-java-bindings:0.0.3"
    implementation "com.pleisto:flappy-java-bindings:0.0.3:$osdetector.classifier"
}
```

### Classified library

Note that the dependency without classifier ships all classes and resources except the "flappy_java_bindings" shared library. And those with classifier bundle only the shared library.

For downstream usage, it's recommended:

* Depend on the one without classifier to write code; 
* Depend on the classified ones with "test" for testing.

To load the shared library correctly, you can choose one of the following approaches:

* Append the classified JARs to the classpath at the runtime;
* Depend on the classified JARs and build a fat JAR (You may need to depend on all the provided classified JARs for running on multiple platforms);
* Build your own "flappy_java_bindings" shared library and specify "-Djava.library.path" to the folder containing that shared library.

## Build

You can use Maven to build both Rust dynamic lib and JAR files with one command now:

```shell
./mvnw clean package -DskipTests=true
```

## Run tests

Currently, all tests are written in Java.

You can run the base tests with the following command:

```shell
./mvnw clean verify -Dgpg.skip
```

## Code style

This project uses [spotless](https://github.com/diffplug/spotless) for code formatting so that all developers share a consistent code style without bikeshedding on it.

You can apply the code style with the following command::

```shell
./mvnw spotless:apply
```

## Acknowledgments

This project is heavily inspired by the following awesome projects.

- [OpenDAL Java Bindings](https://github.com/apache/incubator-opendal/blob/main/bindings/java/README.md)
