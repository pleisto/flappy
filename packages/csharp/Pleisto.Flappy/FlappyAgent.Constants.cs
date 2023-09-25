using Newtonsoft.Json.Schema.Generation;
using System.Text.RegularExpressions;

namespace Pleisto.Flappy
{
  public partial class FlappyAgent
  {
    public const string STEP_PREFIX = "%@_";

    public static readonly Regex STEP_PREFIX_REGEX = new Regex("^%(.*)_");

    public delegate Task<TReturn> ResolveFunction<TArgs, TReturn>(TArgs args)
      where TArgs : class
      where TReturn : class;

    public static JSchemaGenerator GetSchemaGenerator()
    {
      var ret = new JSchemaGenerator();
      ret.GenerationProviders.Add(new StringEnumGenerationProvider());
      return ret;
    }
  }
}
