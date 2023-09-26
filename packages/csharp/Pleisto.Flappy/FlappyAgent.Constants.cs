using Newtonsoft.Json.Schema.Generation;
using System.Text.RegularExpressions;

namespace Pleisto.Flappy
{
  public partial class FlappyAgent
  {
    /// <summary>
    /// Step Prefix
    /// </summary>
    public const string STEP_PREFIX = "%@_";

    /// <summary>
    /// Resolve function delegate
    /// </summary>
    /// <typeparam name="TArgs"></typeparam>
    /// <typeparam name="TReturn"></typeparam>
    /// <param name="args"></param>
    /// <returns></returns>
    public delegate Task<TReturn> ResolveFunction<TArgs, TReturn>(TArgs args)
      where TArgs : class
      where TReturn : class;

    /// <summary>
    /// Get Schema Generator
    /// </summary>
    /// <returns></returns>
    internal static JSchemaGenerator GetSchemaGenerator()
    {
      var ret = new JSchemaGenerator();
      ret.GenerationProviders.Add(new StringEnumGenerationProvider());
      return ret;
    }
  }
}
