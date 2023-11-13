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
            RetryCount = count;
        }

        /// <summary>
        /// Retried count
        /// </summary>
        public int RetryCount { get; private set; }
    }
}
