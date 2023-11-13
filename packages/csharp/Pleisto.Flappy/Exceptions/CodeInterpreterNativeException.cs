using Pleisto.Flappy.CodeInterpreter;

namespace Pleisto.Flappy.Exceptions
{
    /// <summary>
    /// CodeInterpreter native call exception
    /// </summary>
    public class CodeInterpreterNativeException : Exception
    {
        internal CodeInterpreterNativeException(NativeResult result) :
            base($"Exception={result.ExceptionString}{Environment.NewLine}" +
                $"Output={result.StdOut}{Environment.NewLine}" +
                $"Error={result.StdErr}{Environment.NewLine}")
        {
            StdOut = result.StdOut;
            StdErr = result.StdErr;
        }

        /// <summary>
        /// Std Out
        /// </summary>
        public string StdOut { get; private set; }

        /// <summary>
        /// Std Error
        /// </summary>
        public string StdErr { get; private set; }
    }
}
