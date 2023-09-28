using Newtonsoft.Json.Linq;
using Pleisto.Flappy.Interfaces;
using Pleisto.Flappy.LLM.Interfaces;

namespace Pleisto.Flappy
{
  /// <summary>
  /// SynthesizedFunction
  /// </summary>
  /// <typeparam name="TArgs">Argument of functions</typeparam>
  /// <typeparam name="TReturn">Return of functions</typeparam>
  public class SynthesizedFunction<TArgs, TReturn> : FlappyFunctionBase<TArgs, TReturn>, IFlappyFunction
    where TArgs : class
    where TReturn : class
  {
    /// <summary>
    /// Function Call
    /// </summary>
    /// <param name="agent"></param>
    /// <param name="args"></param>
    /// <returns></returns>
    public override async Task<TReturn> Call(FlappyAgent agent, TArgs args)
    {
      var describe = Define.Description;
      var schema = FlappyAgent.GetSchemaGenerator();
      var returnTypeSchema = schema.Generate(typeof(TReturn)); //extractSchema(callingSchema, "returnType");
      var argsSchema = schema.Generate(typeof(TArgs)); ;
      var prompt = JObject.FromObject(args); //(args as object) is string ? args as string : JObject.FromObject(args).ToString();
      var requestMessage = new ChatMLMessage[]{
                  new ChatMLMessage
                  {
                    Role = ChatMLMessageRole.System,
                    Content = $@"{describe}
User reqeust according to the following JSON Schema:
${argsSchema}

Translate it into JSON objecvts according to the following JSON Schema:
{returnTypeSchema}"
                  },
                  new ChatMLMessage
                  {
                    Role = ChatMLMessageRole.User,
                    Content = $"user request:{prompt}\n\njson object:"
                  }
              };
      var result = await agent.llm.ChatComplete(requestMessage);
      try
      {
        var data = ParseComplete(result);

        return data;
      }
      catch (Exception ex)
      {
        var repaired = await agent.llm.ChatComplete(requestMessage.Union(new ChatMLMessage[]
        {
                    new ChatMLMessage{
                        Role = ChatMLMessageRole.Assistant,
                        Content= result.Data ?? ""
                    },
                    new ChatMLMessage{
                        Role = ChatMLMessageRole.User,Content=@$"Your response is invalid for the following reason:
${ex.Message}

Please try again."}
        }).ToArray(), null);
        return ParseComplete(repaired);
      }
    }

    private TReturn ParseComplete(ChatMLResponse msg)
    {
      var startIdx = msg.Data?.IndexOf('{') ?? -1;
      var endIdx = msg.Data?.LastIndexOf('}') ?? -1;
      if (startIdx == -1 || endIdx == -1 || endIdx < startIdx)
        throw new InvalidDataException($"Invalid JSON response startIndex={startIdx} endIdx={endIdx}");

      var content = msg.Data.Substring(startIdx, endIdx - startIdx + 1).Trim();
      try
      {
        var json = JObject.Parse(content);
        return json.ToObject<TReturn>();
      }
      catch (Exception ex)
      {
        throw new InvalidDataException($"exception on {nameof(ParseComplete)} {Environment.NewLine}{content}", ex);
      }
    }

    /// <summary>
    /// Create Synthesized Function
    /// </summary>
    /// <param name="define">Synthesized Definition</param>
    public SynthesizedFunction(SynthesizedFunctionDefinition<TArgs, TReturn> define) : base(define)
    {
    }
  }
}
