using Newtonsoft.Json.Linq;

namespace Pleisto.Flappy.Interfaces
{
  /// <summary>
  /// Flappy Function
  /// </summary>
  public interface FlappyFunction
  {
    /// <summary>
    /// Name of cuntion
    /// </summary>
    string name { get; }

    /// <summary>
    /// System call method by CSharp Type
    /// </summary>
    /// <param name="agent"></param>
    /// <param name="args"></param>
    /// <returns></returns>
    Task<JObject> sharp_syscall(FlappyAgent agent, JObject args);
  }
}
