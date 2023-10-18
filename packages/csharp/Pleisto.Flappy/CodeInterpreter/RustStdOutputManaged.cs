using Newtonsoft.Json;

namespace Pleisto.Flappy.CodeInterpreter
{
  /// <summary>
  /// Managed of Rust Result
  /// </summary>
  public sealed class RustStdOutputManaged
  {
    /// <summary>
    /// Std Error
    /// </summary>
    [JsonProperty("stdErr", Required = Required.AllowNull)]
    public string StdErr { get; private set; }

    /// <summary>
    /// Std Out
    /// </summary>

    [JsonProperty("stdOut", Required = Required.AllowNull)]
    public string StdOut { get; private set; }

    /// <summary>
    /// Exception Message
    /// </summary>
    [JsonProperty("exceptionString", Required = Required.AllowNull)]
    public string ExceptionString { get; private set; }
  }
}
