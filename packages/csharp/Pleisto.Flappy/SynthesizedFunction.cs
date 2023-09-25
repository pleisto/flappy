using Newtonsoft.Json.Linq;
using Pleisto.Flappy.Interfaces;
using Pleisto.Flappy.LLM.Interfaces;

namespace Pleisto.Flappy
{
  public class SynthesizedFunction<TArgs, TReturn> : FlappyFunctionBase<TArgs, TReturn>, FlappyFunction
    where TArgs : class
    where TReturn : class
  {
    public override async Task<TReturn> call(FlappyAgent agent, TArgs args)
    {
      var describe = define.description;
      var schema = FlappyAgent.GetSchemaGenerator();
      var returnTypeSchema = schema.Generate(typeof(TReturn)); //extractSchema(callingSchema, "returnType");
      var argsSchema = schema.Generate(typeof(TArgs)); ;
      var prompt = JObject.FromObject(args); //(args as object) is string ? args as string : JObject.FromObject(args).ToString();
      var requestMessage = new ChatMLMessage[]{
                  new ChatMLMessage
                  {
                    role = ChatMLMessageRole.system,
                    content = $@"{describe}
User reqeust according to the following JSON Schema:
${argsSchema}

Translate it into JSON objecvts according to the following JSON Schema:
{returnTypeSchema}"
                  },
                  new ChatMLMessage
                  {
                    role = ChatMLMessageRole.user,
                    content = $"user request:{prompt}\n\njson object:"
                  }
              };
      var result = await agent.llm.chatComplete(requestMessage);
      try
      {
        var data = parseComplete(result);

        return data;
      }
      catch (Exception ex)
      {
        var repaired = await agent.llm.chatComplete(requestMessage.Union(new ChatMLMessage[]
        {
                    new ChatMLMessage{
                        role = ChatMLMessageRole.assistant,
                        content= result.data ?? ""
                    },
                    new ChatMLMessage{
                        role = ChatMLMessageRole.user,content=@$"Your response is invalid for the following reason:
${ex.Message}

Please try again."}
        }).ToArray(), null);
        return parseComplete(repaired);
      }
    }

    private TReturn parseComplete(ChatMLResponse msg)
    {
      var startIdx = msg.data?.IndexOf('{') ?? -1;
      var endIdx = msg.data?.LastIndexOf('}') ?? -1;
      if (startIdx == -1 || endIdx == -1 || endIdx < startIdx)
        throw new InvalidDataException($"Invalid JSON response startIndex={startIdx} endIdx={endIdx}");

      var content = msg.data.Substring(startIdx, endIdx - startIdx + 1).Trim();
      try
      {
        var json = JObject.Parse(content);
        return json.ToObject<TReturn>();
      }
      catch (Exception ex)
      {
        throw new InvalidDataException($"exception on {nameof(parseComplete)} {Environment.NewLine}{content}", ex);
      }
    }

    public SynthesizedFunction(InvokeFunctionDefinition<TArgs, TReturn> define) : base(define)
    {
    }
  }
}
