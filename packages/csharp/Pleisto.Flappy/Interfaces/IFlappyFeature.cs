using Newtonsoft.Json.Linq;

namespace Pleisto.Flappy.Interfaces
{
  /// <summary>
  /// Flappy Feature
  /// </summary>
  public interface IFlappyFeature
  {
    /// <summary>
    /// Name of Feature
    /// </summary>
    string Name { get; }

    /// <summary>
    /// System call method by CSharp Type
    /// </summary>
    /// <param name="agent"></param>
    /// <param name="args"></param>
    /// <returns></returns>
    internal Task<JObject> SharpSystemCall(FlappyAgent agent, JObject args);
  }
}
