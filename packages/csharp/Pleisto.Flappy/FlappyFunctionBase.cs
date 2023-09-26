using Newtonsoft.Json.Linq;
using Newtonsoft.Json.Schema.Generation;
using Pleisto.Flappy.Interfaces;
using Pleisto.Flappy.Utils;

namespace Pleisto.Flappy
{
  /// <summary>
  /// Basic of flappy function
  /// </summary>
  /// <typeparam name="TArgs"></typeparam>
  /// <typeparam name="TReturn"></typeparam>
  public abstract class FlappyFunctionBase<TArgs, TReturn> : IFlappyFunction
      where TArgs : class
      where TReturn : class
  {
    /// <summary>
    /// Function define
    /// </summary>
    public InvokeFunctionDefinition<TArgs, TReturn> Define;

    /// <summary>
    /// Function calling schema
    /// </summary>
    public JObject CallingSchema { get; private set; }

    /// <summary>
    /// Name of function
    /// </summary>
    public string Name => Define.Name;

    /// <summary>
    /// Create Flappy Function Base
    /// </summary>
    /// <param name="define">Function Definition</param>
    public FlappyFunctionBase(InvokeFunctionDefinition<TArgs, TReturn> define)
    {
      Define = define;
      CallingSchema = BuildJsonSchema(define);
    }

    private static JObject BuildJsonSchema(InvokeFunctionDefinition<TArgs, TReturn> define)
    {
      var schemaGenerator = new JSchemaGenerator();
      return new JObject
      {
        ["name"] = define.Name,
        ["description"] = define.Description,
        ["parameters"] = new JObject
        {
          ["type"] = "object",
          ["properties"] = new JObject
          {
            ["args"] = JObject.FromObject(schemaGenerator.Generate(define.Args.GetType())),
            ["returnType"] = JObject.FromObject(schemaGenerator.Generate(define.ReturnType.GetType()))
          }
        }
      };
    }

    /// <summary>
    /// Function call abstract
    /// </summary>
    /// <param name="agent">FlappyAgent caller</param>
    /// <param name="args">Calling argument</param>
    /// <returns></returns>
    public abstract Task<TReturn> Call(FlappyAgent agent, TArgs args);

    /// <summary>
    /// System call method
    /// </summary>
    /// <param name="agent">FlappyAgent</param>
    /// <param name="args">Calling argument (JsonObject)</param>
    /// <returns>Functions return (JsonObject)</returns>
    public async Task<JObject> SharpSystemCall(FlappyAgent agent, JObject args)
    {
      return JObject.FromObject(await Call(agent, args.ToObject<TArgs>(JsonExtensions.jsonSerializer)),JsonExtensions.jsonSerializer);
    }
  }
}
