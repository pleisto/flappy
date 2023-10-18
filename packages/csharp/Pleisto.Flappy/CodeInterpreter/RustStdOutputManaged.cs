using Newtonsoft.Json;
using System.Diagnostics.CodeAnalysis;

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
    [AllowNull]
    [JsonProperty("stdErr")]
    public string StdErr { get; private set; }

    /// <summary>
    /// Std Out
    /// </summary>
    [AllowNull]
    [JsonProperty("stdOut")]
    public string StdOut { get; private set; }

    /// <summary>
    /// Exception Message
    /// </summary>
    [AllowNull]
    [JsonProperty("exceptionString")]
    public string ExceptionString { get; private set; }
  }
}
