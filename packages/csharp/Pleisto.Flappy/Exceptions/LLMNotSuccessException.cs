namespace Pleisto.Flappy.Exceptions
{
  /// <summary>
  /// LLM not success
  /// </summary>
  public class LLMNotSuccessException : Exception
  {
    internal LLMNotSuccessException()
       : base("LLM operation return not success")
    {
    }
  }
}
