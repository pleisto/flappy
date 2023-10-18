package flappy.llms

import com.fasterxml.jackson.annotation.JsonInclude
import flappy.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.math.BigInteger
import java.security.MessageDigest
import java.time.Instant
import java.util.*

/**
 * https://platform.baichuan-ai.com/docs/api
 */

// https://stackoverflow.com/a/64171625/20030734
internal fun calculateMd5(input: String): String {
  val md = MessageDigest.getInstance("MD5")
  return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
}

class Baichuan(
  private val baichuanConfig: BaichuanConfig,
) :
  FlappyLLMBase() {
  private val client = HttpClient()

  override fun close() {
    client.close()
  }

  class BaichuanConfig(
    model: String? = null,
    val apiKey: String, val secretKey: String
  ) {
    val baichuanModel = model ?: "Baichuan2-53B"
  }

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  data class BaichuanParameters(
    val temperature: Double? = null,
    val top_k: Double? = null,
    val top_p: Double? = null,
    val with_search_enhance: Boolean? = null
  )

  data class BaichuanRequestChatMessage(
    val role: String,
    val content: String
  )

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  data class BaichuanRequestBody(
    val model: String,
    val messages: List<BaichuanRequestChatMessage>,
    val parameters: BaichuanParameters? = null
  )

  class BaichuanChatMessage(
    val role: String,
    val content: String,
    val finish_reason: String
  )

  class BaichuanResponseData(
    val messages: List<BaichuanChatMessage>
  )

  class BaichuanResponseUsage(
    val prompt_tokens: Int,
    val answer_tokens: Int,
    val total_tokens: Int
  )

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  class BaichuanResponseBody(
    val code: Int,
    val msg: String,
    val data: BaichuanResponseData?,
    val usage: BaichuanResponseUsage
  )

  override suspend fun chatComplete(
    messages: List<FlappyChatMessage>,
    source: String,
    config: LLMGenerateConfig?
  ): LLMResponse {
    val requestId = UUID.randomUUID().toString()
    val timestamp = Instant.now().epochSecond.toString()

    val body = BaichuanRequestBody(
      model = baichuanConfig.baichuanModel,
      messages = messages.map {
        BaichuanRequestChatMessage(
          role = (when (it.role) {
            FlappyRole.ASSISTANT -> "assistant"
            FlappyRole.SYSTEM -> "system"
            FlappyRole.USER -> "user"
          }),
          content = it.content
        )
      },
      parameters = BaichuanParameters(temperature = config?.temperature, top_p = config?.topP)
    )

    val jsonBody = body.asString()

    val signature = calculateMd5("${baichuanConfig.secretKey}$jsonBody$timestamp")

    logger.info("baichuan input $messages -> $jsonBody, $timestamp")

    try {
      val response = client.post(
        "https://api.baichuan-ai.com/v1/chat"
      ) {
        contentType(ContentType.Application.Json)
        headers {
          append(HttpHeaders.ContentType, "application/json")
          append(HttpHeaders.Authorization, "Bearer ${baichuanConfig.apiKey}")
          append(HttpHeaders.AcceptLanguage, "*")
          append(HttpHeaders.AcceptEncoding, "gzip, deflate")
          append("X-BC-Request-Id", requestId)
          append("X-BC-Timestamp", timestamp)
          append("X-BC-Signature", signature)
          append("X-BC-Sign-Algo", "MD5")
        }
        setBody(jsonBody)
      }

      if (response.status != HttpStatusCode.OK) {
        return LLMResponse.Fail("Invalid status: ${response.status}")
      }

      val respBody = response.bodyAsText()
      logger.info("baichuan output $respBody")

      val result = respBody.castBy(BaichuanResponseBody::class.java)
      if (result.code != 0) return LLMResponse.Fail(result.msg)
      val message = result.data?.messages?.get(0)
      if (message === null) return LLMResponse.Fail("Invalid response")

      return LLMResponse.Success(message.content)

    } catch (e: Exception) {
      return LLMResponse.Fail(e.message ?: "ERROR")
    }
  }
}
