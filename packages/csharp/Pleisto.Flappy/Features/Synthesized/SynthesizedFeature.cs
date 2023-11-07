using Newtonsoft.Json.Linq;
using Pleisto.Flappy.Exceptions;
using Pleisto.Flappy.Interfaces;
using Pleisto.Flappy.LLM.Interfaces;

namespace Pleisto.Flappy.Features.Syntehesized
{
  /// <summary>
  /// Synthesized Feature
  /// </summary>
  /// <typeparam name="TArgs">Argument of feature</typeparam>
  /// <typeparam name="TReturn">Return of feature</typeparam>
  /// <typeparam name="TOptions">Options of feature</typeparam>
  public class SynthesizedFeature<TArgs, TReturn, TOptions> : FlappyFeatureBase<TArgs, TReturn, TOptions>, IFlappyFeature
    where TArgs : class
    where TReturn : class
    where TOptions : FlappyFeatureOption
  {
    /// <summary>
    /// Feature Call
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

    private static TReturn ParseComplete(ChatMLResponse msg)
    {
      var startIdx = msg.Data?.IndexOf('{') ?? -1;
      var endIdx = msg.Data?.LastIndexOf('}') ?? -1;
      if (startIdx == -1 || endIdx == -1 || endIdx < startIdx)
        throw new InvalidJsonDataException(startIdx, endIdx, msg.Data);

      var content = msg.Data.Substring(startIdx, endIdx - startIdx + 1).Trim();
      try
      {
        var json = JObject.Parse(content);
        return json.ToObject<TReturn>();
      }
      catch (Exception ex)
      {
        throw new InvalidJsonDataException(startIdx, endIdx, msg.Data, content, ex);
      }
    }

    /// <summary>
    /// Create Synthesized Feature
    /// </summary>
    /// <param name="define">Synthesized Definition</param>
    public SynthesizedFeature(SynthesizedFeatureDefinition<TArgs, TReturn> define) : base(define)
    {
    }
  }
}
