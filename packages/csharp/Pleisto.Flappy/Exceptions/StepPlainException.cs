using Newtonsoft.Json.Linq;

namespace Pleisto.Flappy.Exceptions
{
  /// <summary>
  /// Exception of plain step executing
  /// </summary>
  public class StepPlainException : Exception
  {
    internal StepPlainException(JObject config, Exception inner)
      : base($"unable to process step {config}", inner)
    {
    }

    /// <summary>
    /// Json of step
    /// </summary>
    public JObject StepConfigure { get; private set; }
  }
}
