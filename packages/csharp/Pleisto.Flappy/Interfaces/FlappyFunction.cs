using Newtonsoft.Json.Linq;

namespace Pleisto.Flappy.Interfaces
{
  /// <summary>
  /// Flappy Function
  /// </summary>
  public interface IFlappyFunction
  {
    /// <summary>
    /// Name of cuntion
    /// </summary>
    string Name { get; }

    /// <summary>
    /// System call method by CSharp Type
    /// </summary>
    /// <param name="agent"></param>
    /// <param name="args"></param>
    /// <returns></returns>
    Task<JObject> SharpSystemCall(FlappyAgent agent, JObject args);
  }
}
