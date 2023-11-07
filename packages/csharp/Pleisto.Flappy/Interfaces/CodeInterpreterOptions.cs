namespace Pleisto.Flappy.Interfaces
{
  /// <summary>
  /// Core Interpreter Options
  /// </summary>
  public class CodeInterpreterOptions : FlappyFeatureOption
  {
    /// <summary>
    /// Enable network access in sandbox. Default is false.
    /// </summary>
    public bool? EnableNetwork { get; set; }

    /// <summary>
    /// Specify the cache directory for code interpreter.
    /// </summary>
    public string CacheDir { get; set; }

    /// <summary>
    /// Environment variables for code interpreter.
    /// </summary>
    public Dictionary<string, string> Env { get; set; }
  }
}
