package flappy

enum class FlappyRole {
  SYSTEM, USER, ASSISTANT
}

abstract class FlappyChatMessage(open val content: String) {
  abstract val role: FlappyRole

  override fun toString() = "[$role] $content"
}

internal class SystemMessage(override val content: String) : FlappyChatMessage(content) {
  override val role = FlappyRole.SYSTEM
}

internal class UserMessage(override val content: String) : FlappyChatMessage(content) {
  override val role = FlappyRole.USER
}

internal class AssistantMessage(override val content: String) : FlappyChatMessage(content) {
  override val role = FlappyRole.ASSISTANT
}
