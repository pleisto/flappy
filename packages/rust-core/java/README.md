# Flappy Java Bindings

[![License](https://img.shields.io/github/license/pleisto/flappy.svg)](https://raw.githubusercontent.com/pleisto/flappy/main/LICENSE)
[![CI](https://img.shields.io/github/actions/workflow/status/pleisto/flappy/java-bindings-test.yml.svg)](https://github.com/pleisto/flappy/actions/workflows/java-bindings-test.yml)
[![Maven metadata URL](https://img.shields.io/maven-metadata/v.svg?metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Fcom%2Fpleisto%2Fflappy%2Fmaven-metadata.xml&color=blue)](https://central.sonatype.com/artifact/com.pleisto/flappy)



## Build

You can use Maven to build both Rust dynamic lib and JAR files with one command now:

```shell
./mvnw clean package -DskipTests=true
```

## Run tests

Currently, all tests are written in Java.

You can run the base tests with the following command:

```shell
./mvnw clean verify
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
