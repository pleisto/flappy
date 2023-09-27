package org.example.kotlin

import flappy.FieldType
import flappy.FlappyBaseAgent
import flappy.FlappyInvokeFunction
import flappy.FlappySynthesizedFunction
import flappy.annotations.FlappyField
import flappy.llms.ChatGPT
import flappy.llms.ChatGPTConfig
import io.github.cdimascio.dotenv.dotenv


enum class Verdict {
  Innocent, Guilty, Unknown
}

const val MOCK_LAWSUIT_DATA =
  """As Alex Jones continues telling his Infowars audience about his money problems and pleads for them to buy his products, his own documents show life is not all that bad — his net worth is around $14 million and his personal spending topped $93,000 in July alone, including thousands of dollars on meals and entertainment. The conspiracy theorist and his lawyers file monthly financial reports in his personal bankruptcy case, and the latest one has struck a nerve with the families of victims of Sandy Hook Elementary School shooting. They're still seeking the $1.5 billion they won last year in lawsuits against Jones and his media company for repeatedly calling the 2012 massacre a hoax on his shows. “It is disturbing that Alex Jones continues to spend money on excessive household expenditures and his extravagant lifestyle when that money rightfully belongs to the families he spent years tormenting,” said Christopher Mattei, a Connecticut lawyer for the families. “The families are increasingly concerned and will continue to contest these matters in court.” In an Aug. 29 court filing, lawyers for the families said that if Jones doesn’t reduce his personal expenses to a “reasonable” level, they will ask the bankruptcy judge to bar him from “further waste of estate assets,” appoint a trustee to oversee his spending, or dismiss the bankruptcy case. On his Infowars show Tuesday, Jones said he’s not doing anything wrong."""


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

class GetLatestLawsuitsArguments(
  @FlappyField
  val plaintiff: String,

  @FlappyField(description = "For demo purpose. set to False")
  val arg1: Boolean,

  @FlappyField(description = "ignore it", subType = FieldType.STRING, optional = true)
  val arg2: List<String>?
)


val lawGetLatestLawsuitsByPlaintiff = FlappyInvokeFunction(
  name = "getLatestLawsuitsByPlaintiff",
  description = "Get the latest lawsuits by plaintiff.",
  args = GetLatestLawsuitsArguments::class.java,
  returnType = String::class.java,
  invoker = { _, _ -> MOCK_LAWSUIT_DATA }
)

const val LAW_EXECUTE_PLAN_PROMPT =
  "Find the latest case with the plaintiff being families of victims and return its metadata."


suspend fun main(args: Array<String>) {
  val dotenv = dotenv()

  val llm = ChatGPT(
    model = "gpt-3.5-turbo",
    chatGPTConfig = ChatGPTConfig(token = dotenv["OPENAI_TOKEN"], host = dotenv["OPENAI_API_BASE"])
  )
  val lawAgent = FlappyBaseAgent(
    maxRetry = 2,
    inferenceLLM = llm,
    functions = listOf(lawGetMeta, lawGetLatestLawsuitsByPlaintiff)
  )

  lawAgent.executePlan<LawMetaReturn>(LAW_EXECUTE_PLAN_PROMPT)
//    lawAgent.callFunction("getMeta", MetaArguments(MOCK_LAWSUIT_DATA))
}
