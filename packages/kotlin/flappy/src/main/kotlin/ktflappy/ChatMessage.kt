package flappy

enum class FlappyRole {
    SYSTEM, USER, ASSISTANT
}

abstract class FlappyChatMessage(open val content: String) {
    abstract val role: FlappyRole

    override fun toString() = "[$role] $content"
}

class SystemMessage(override val content: String) : FlappyChatMessage(content) {
    override val role = FlappyRole.SYSTEM

}

class UserMessage(override val content: String) : FlappyChatMessage(content) {
    override val role = FlappyRole.USER
}

class AssistantMessage(override val content: String) : FlappyChatMessage(content) {
    override val role = FlappyRole.ASSISTANT
}