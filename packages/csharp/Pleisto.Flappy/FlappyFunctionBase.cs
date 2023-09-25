using Newtonsoft.Json.Linq;
using Newtonsoft.Json.Schema.Generation;
using Pleisto.Flappy.Interfaces;
using System.Diagnostics.CodeAnalysis;

namespace Pleisto.Flappy
{
  public abstract class FlappyFunctionBase<TArgs, TReturn> : FlappyFunction
      where TArgs : class
      where TReturn : class
  {
    public InvokeFunctionDefinition<TArgs, TReturn> define;
    public JObject callingSchema { get; set; }

    public string name => define.name;

    public FlappyFunctionBase(InvokeFunctionDefinition<TArgs, TReturn> define)
    {
      this.define = define;
      this.callingSchema = buildJsonSchema(define);
    }

    [SuppressMessage("Maintainability", "CA1507:使用 nameof 表达符号名称", Justification = "<挂起>")]
    private static JObject buildJsonSchema(InvokeFunctionDefinition<TArgs, TReturn> define)
    {
      var schemaGenerator = new JSchemaGenerator();
      return new JObject
      {
        ["name"] = define.name,
        ["description"] = define.description,
        ["parameters"] = new JObject
        {
          ["type"] = "object",
          ["properties"] = new JObject
          {
            ["args"] = JObject.FromObject(schemaGenerator.Generate(define.args.GetType())),
            ["returnType"] = JObject.FromObject(schemaGenerator.Generate(define.returnType.GetType()))
          }
        }
      };
    }

    public abstract Task<TReturn> call(FlappyAgent agent, TArgs args);

    public async Task<JObject> sharp_syscall(FlappyAgent agent, JObject args)
    {
      return JObject.FromObject(await call(agent, args.ToObject<TArgs>()));
    }
  }
}
