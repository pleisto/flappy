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
    [JsonProperty("std_err", Required = Required.AllowNull)]
    public string StdErr { get; private set; }

    /// <summary>
    /// Std Out
    /// </summary>

    [JsonProperty("std_out", Required = Required.AllowNull)]
    public string StdOut { get; private set; }

    /// <summary>
    /// Exception Message
    /// </summary>
    [JsonProperty("exception_string", Required = Required.AllowNull)]
    public string ExceptionString { get; private set; }
  }
}
