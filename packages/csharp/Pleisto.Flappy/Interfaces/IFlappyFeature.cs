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
        /// System convert json to argument
        /// </summary>
        /// <param name="json"></param>
        /// <returns></returns>
        internal object JsonToArgs(JObject json);

        /// <summary>
        /// System call
        /// </summary>
        /// <param name="agent"></param>
        /// <param name="args"></param>
        /// <returns></returns>
        internal Task<object> SystemCall(FlappyAgent agent, object args);
    }
}
