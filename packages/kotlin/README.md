# Module flappy

This package is the Kotlin version of the flappy implementation.

<div align="center">

[![License](https://img.shields.io/github/license/pleisto/flappy.svg)](https://raw.githubusercontent.com/pleisto/flappy/main/LICENSE)
[![CI](https://img.shields.io/github/actions/workflow/status/pleisto/flappy/kotlin-ci.yml.svg)](https://github.com/pleisto/flappy/actions/workflows/kotlin-ci.yml)
[![Maven metadata URL](https://img.shields.io/maven-metadata/v.svg?metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Fcom%2Fpleisto%2Fflappy%2Fmaven-metadata.xml&color=blue)](https://central.sonatype.com/artifact/com.pleisto/flappy)
[![Documentation](https://javadoc.io/badge/com.pleisto/flappy.svg)](https://javadoc.io/doc/com.pleisto/flappy)
[![codecov](https://codecov.io/gh/pleisto/flappy/graph/badge.svg?token=8C94YY3KBD)](https://codecov.io/gh/pleisto/flappy)

[Install](#installation) •
[Usage](#usage) •
[Examples](#examples) •
[What's next](#whats-next)

</div>

## Features

- [x] Easily switch between different LLMs
- [x] Controlled, consistent, and machine-readable LLM outputs
- [x] Implement via pure Kotlin

## Installation

In Gradle(Kotlin), add the following dependency to your `build.gradle.kts` file:

```kotlin
// plugins
id("com.google.osdetector") version "1.7.3"

// dependencies
implementation("com.pleisto:flappy:0.0.8")
implementation("com.pleisto:flappy-java-bindings:0.0.8")
implementation("com.pleisto:flappy-java-bindings:0.0.8:${osdetector.classifier}")
```

In other build system, please refer to [here](https://central.sonatype.com/artifact/com.pleisto/flappy)

## Usage

### Create a Synthesized Function

A synthesized function allows developers to format natural language using configuration fields for the LLM.

<details open>
  <summary>Kotlin</summary>

```kotlin
class LawMetaArguments(
  @FlappyField(description = "Lawsuit full text.")
  val lawsuit: String
)

class LawMetaReturn(
  @FlappyField
  val verdict: Verdict,

  @FlappyField
  val plaintiff: String,

  @FlappyField
  val defendant: String,

  @FlappyField
  val judgeOptions: List<String>
)

val lawGetMeta = FlappySynthesizedFunction(
  name = "getMeta",
  description = "Extract meta data from a lawsuit full text.",
  args = LawMetaArguments::class.java,
  returnType = LawMetaReturn::class.java,
)
```

</details>

<details>
  <summary>Java</summary>

```java
class LawMetaArguments {
  @FlappyField(description = "Lawsuit full text.")
  String lawsuit;
}

class LawMetaReturn {
  @FlappyField
  Verdict verdict;

  @FlappyField
  String plaintiff;

  @FlappyField
  String defendant;

  @FlappyField
  List<String> judgeOptions;
}

  FlappyFunction<?, ?> lawGetMeta = new FlappySynthesizedFunction(
    "getMeta",
    "Extract meta data from a lawsuit full text.",
    LawMetaArguments.class,
    LawMetaReturn.class
  );
```

</details>

### Create an Invoke Function

In addition to synthesized functions, developers can also add custom methods for the agent to invoke by including `invokeFunction`.

<details open>
  <summary>Kotlin</summary>

```kotlin
class GetLatestLawsuitsArguments(
  @FlappyField
  val plaintiff: String,

  @FlappyField(description = "For demo purpose. set to False")
  val arg1: Boolean,

  @FlappyField(description = "ignore it", optional = true)
  val arg2: List<String>?
)

class GetLatestLawsuitsReturn(
  @FlappyField
  val output: String
)


val lawGetLatestLawsuitsByPlaintiff = FlappyInvokeFunction(
  name = "getLatestLawsuitsByPlaintiff",
  description = "Get the latest lawsuits by plaintiff.",
  args = GetLatestLawsuitsArguments::class.java,
  returnType = GetLatestLawsuitsReturn::class.java,
  invoker = { _, _ -> GetLatestLawsuitsReturn(MOCK_LAWSUIT_DATA) }
)
```

</details>

<details>
  <summary>Java</summary>

```java
class GetLatestLawsuitsArguments {
  @FlappyField
  String plaintiff;

  @FlappyField(description = "For demo purpose. set to False")
  Boolean arg1;

  @FlappyField(description = "ignore it", optional = true)
  List<String> arg2 = null;
}

static class GetLatestLawsuitsReturn {
  @FlappyField
  String output;

  public GetLatestLawsuitsReturn(String output) {
    this.output = output;
  }
}

  FlappyFunction<?, ?> lawGetLatestLawsuitsByPlaintiff = new FlappyInvokeFunction(
    "getLatestLawsuitsByPlaintiff",
    "Get the latest lawsuits by plaintiff.",
    GetLatestLawsuitsArguments.class,
    GetLatestLawsuitsReturn.class,
    (a, agent, $completion) -> new GetLatestLawsuitsReturn(MOCK_LAWSUIT_DATA)
  );
```

</details>

### Create an Agent

To create an agent, you need to provide an LLM (Large Language Model) along with the methods you want the agent to use.

<details open>
  <summary>Kotlin</summary>

```kotlin
val llm = ChatGPT(
  model = "gpt-3.5-turbo",
  chatGPTConfig = ChatGPTConfig(token = dotenv["OPENAI_TOKEN"], host = dotenv["OPENAI_API_BASE"])
)

val lawAgent = FlappyBaseAgent(
  maxRetry = 2,
  inferenceLLM = llm,
  functions = listOf(lawGetMeta, lawGetLatestLawsuitsByPlaintiff)
)
```

</details>

<details>
  <summary>Java</summary>

```java
ChatGPT llm=new ChatGPT("gpt-3.5-turbo",new ChatGPTConfig(dotenv.get("OPENAI_TOKEN"),dotenv.get("OPENAI_API_BASE")));
  FlappyBaseAgent lawAgent=new FlappyBaseAgent(
  llm,Arrays.asList(lawGetMeta,lawGetLatestLawsuitsByPlaintiff)
  );
```

</details>

## Examples

### Kotlin

- [Resume](https://github.com/pleisto/flappy/blob/main/examples/kotlin/src/main/kotlin/org/example/kotlin/Resume.kt)
- [Law](https://github.com/pleisto/flappy/blob/main/examples/kotlin/src/main/kotlin/org/example/kotlin/Law.kt)

### Java

- [Resume](https://github.com/pleisto/flappy/blob/main/examples/java/src/main/java/org/example/java/Resume.java)
- [Law](https://github.com/pleisto/flappy/blob/main/examples/java/src/main/java/org/example/java/Law.java)

## What's next

- Template Engine
- Code Interpreter
