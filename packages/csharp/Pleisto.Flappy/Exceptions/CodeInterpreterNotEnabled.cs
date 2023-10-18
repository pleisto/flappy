namespace Pleisto.Flappy.Exceptions
{
  /// <summary>
  /// Code Interpreter not enabled exception
  /// </summary>
  public class CodeInterpreterNotEnabledException : Exception
  {
    internal CodeInterpreterNotEnabledException() : base("Code interpreter is not enabled!")
    {

    }
  }
}
