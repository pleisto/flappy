using Newtonsoft.Json;

namespace Pleisto.Flappy.CodeInterpreter
{
  /// <summary>
  /// CodeInterpreter Input Argument
  /// </summary>
  internal sealed class RustStdInputManaged
  {
    /// <summary>
    /// Python Code
    /// </summary>
    [JsonProperty("code")]
    public string Code { get; set; }

    /// <summary>
    /// Allow network
    /// </summary>
    [JsonProperty("network")]
    public bool Network { get; set; }

    /// <summary>
    /// Environments
    /// </summary>
    [JsonProperty("envs")]
    public Dictionary<string, string> Envs { get; set; } = new Dictionary<string, string>();

    /// <summary>
    /// Cache of WASI
    /// </summary>
    [JsonProperty("cachePath")]
    public string CachePath { get; set; }
  }
}
