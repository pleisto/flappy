namespace Pleisto.Flappy.Exceptions
{
    /// <summary>
    /// CodeInterpreter native call exception
    /// </summary>
    public class CodeInterpreterNativeException : Exception
    {
        internal CodeInterpreterNativeException(string content) : base(content)
        {
        }
    }
}
