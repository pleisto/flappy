namespace Pleisto.Flappy.Exceptions
{
  /// <summary>
  /// Codeinterpreter too more retry exception
  /// </summary>
  public class CodeInterpreterRetryException : Exception
  {
    internal CodeInterpreterRetryException(int retryCount, Exception inner)
      : base($"retried count={retryCount}", inner)
    {
      RetryCount = retryCount;
    }

    /// <summary>
    /// Retried count
    /// </summary>
    public int RetryCount { get; private set; }
  }
}
