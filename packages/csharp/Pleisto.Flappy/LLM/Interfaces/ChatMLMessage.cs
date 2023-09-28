namespace Pleisto.Flappy.LLM.Interfaces
{
  /// <summary>
  /// LLM Message
  /// </summary>
  public class ChatMLMessage
  {
    /// <summary>
    /// Message Role
    /// </summary>
    public ChatMLMessageRole Role { get; set; }

    /// <summary>
    /// Message Content
    /// </summary>
    public string Content { get; set; }
  }
}
