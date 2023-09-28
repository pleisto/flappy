# Flappy Kotlin version

[![License](https://img.shields.io/github/license/pleisto/flappy)](../../LICENSE)
[![CI](https://img.shields.io/github/actions/workflow/status/pleisto/flappy/gradle.yml?label=ci)](https://github.com/pleisto/flappy/actions/workflows/gradle.yml)
[![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Fcom%2Fpleisto%2Fflappy%2Fmaven-metadata.xml&color=blue&label=Download)](https://central.sonatype.com/artifact/com.pleisto/flappy)
[![Documentation](https://img.shields.io/badge/docs-api-a97bff.svg?logo=kotlin)](https://pleisto.github.io/flappy/kotlin/)

This package is the Kotlin version of the flappy implementation.

## Usage

### Installation

Add the following dependency to your `build.gradle.kts` file:

```kotlin
implementation("com.pleisto:flappy:0.0.5")
```

#### Create a Synthesized Function

A synthesized function allows developers to format natural language using configuration fields for the LLM.

With **Kotlin**

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

With **Java**

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

#### Create an Invoke Function

In addition to synthesized functions, developers can also add custom methods for the agent to invoke by including `invokeFunction`.

With **Kotlin**

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

With **Java**

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

#### Create an Agent

To create an agent, you need to provide an LLM (Large Language Model) along with the methods you want the agent to use.

With **Kotlin**

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

With **Java**

```java
ChatGPT llm = new ChatGPT("gpt-3.5-turbo", new ChatGPTConfig(dotenv.get("OPENAI_TOKEN"), dotenv.get("OPENAI_API_BASE")));
FlappyBaseAgent lawAgent = new FlappyBaseAgent(
  llm, List.of(lawGetMeta, lawGetLatestLawsuitsByPlaintiff)
);
```

## TODO
