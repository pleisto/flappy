namespace Pleisto.Flappy.Exceptions
{
  /// <summary>
  /// Codeinterpreter too more retry exception
  /// </summary>
  public class TooMoreRetryException : Exception
  {
    internal TooMoreRetryException(int count, Exception inner)
      : base($"retried count={count}", inner)
    {
      this.RetryCount = count;
    }

    /// <summary>
    /// Retried count
    /// </summary>
    public int RetryCount { get; private set; }
  }
}
