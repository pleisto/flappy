# Flappy Kotlin version

This package is the Kotlin version of the flappy implementation.

## Usage

### Installation

Add the following dependency to your `build.gradle.kts` file:

```kotlin
implementation("com.pleisto:flappy:0.0.1")
```

#### Create a Synthesized Function

A synthesized function allows developers to format natural language using configuration fields for the LLM.

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

    @FlappyField(subType = FieldType.STRING)
    val judgeOptions: List<String>
)

val lawGetMeta = FlappySynthesizedFunction(
    name = "getMeta",
    description = "Extract meta data from a lawsuit full text.",
    args = LawMetaArguments::class.java,
    returnType = LawMetaReturn::class.java,
)
```

#### Create an Invoke Function

In addition to synthesized functions, developers can also add custom methods for the agent to invoke by including `invokeFunction`.

```kotlin
val MOCK_LAWSUIT_DATA =
    """As Alex Jones continues telling his Infowars audience about his money problems and pleads for them to buy his products, his own documents show life is not all that bad — his net worth is around $14 million and his personal spending topped $93,000 in July alone, including thousands of dollars on meals and entertainment. The conspiracy theorist and his lawyers file monthly financial reports in his personal bankruptcy case, and the latest one has struck a nerve with the families of victims of Sandy Hook Elementary School shooting. They're still seeking the $1.5 billion they won last year in lawsuits against Jones and his media company for repeatedly calling the 2012 massacre a hoax on his shows. “It is disturbing that Alex Jones continues to spend money on excessive household expenditures and his extravagant lifestyle when that money rightfully belongs to the families he spent years tormenting,” said Christopher Mattei, a Connecticut lawyer for the families. “The families are increasingly concerned and will continue to contest these matters in court.” In an Aug. 29 court filing, lawyers for the families said that if Jones doesn’t reduce his personal expenses to a “reasonable” level, they will ask the bankruptcy judge to bar him from “further waste of estate assets,” appoint a trustee to oversee his spending, or dismiss the bankruptcy case. On his Infowars show Tuesday, Jones said he’s not doing anything wrong."""

class GetLatestLawsuitsArguments(
    @FlappyField
    val plaintiff: String,

    @FlappyField(description = "For demo purpose. set to False")
    val arg1: Boolean,

    @FlappyField(description = "ignore it", subType = FieldType.STRING, optional = true)
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

#### Create an agent

To create an agent, you need to provide an LLM (Large Language Model) along with the methods you want the agent to use.

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
