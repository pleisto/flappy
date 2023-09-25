using Newtonsoft.Json.Linq;
using Newtonsoft.Json.Schema;
using Pleisto.Flappy.Interfaces;
using Pleisto.Flappy.LLM;
using Pleisto.Flappy.LLM.Interfaces;
using Pleisto.Flappy.Utils;
using System.ComponentModel;

namespace Pleisto.Flappy
{
  public partial class FlappyAgent
  {
    internal readonly FlappyAgentConfig config;
    internal readonly LLMBase llm;
    internal readonly LLMBase llmPlaner;

    public FlappyAgent(FlappyAgentConfig config, LLMBase llm, LLMBase llmPlaner)
    {
      this.config = config;
      if ((config.functions?.Length ?? 0) <= 0)
        throw new NullReferenceException($"config.functions not be null");
      this.llm = llm ?? config.llm;
      this.llmPlaner = llmPlaner ?? this.llm;
    }

    public IEnumerable<FlappyFunction> functionsDefinitions() => from i in config.functions
                                                                 select i;

    public FlappyFunction findFunction(string name) => (from i in config.functions
                                                        where i.name.Equals(name?.Trim(), StringComparison.OrdinalIgnoreCase)
                                                        select i).FirstOrDefault();

    public IEnumerable<FlappyFunction> synthesizedFunctions() => from i in config.functions
                                                                 let type = i.GetType()
                                                                 where type.BaseType == typeof(SynthesizedFunction<,>)
                                                                 select i;

    public IEnumerable<FlappyFunction> invokeFunctions() => from i in config.functions
                                                            let type = i.GetType()
                                                            where type.BaseType == typeof(InvokeFunctions<,>)
                                                            select i;

    public async Task<object> callFunction(string name, object args)
    {
      var fn = findFunction(name) ?? throw new InvalidProgramException($"no function found: {name}");
      return await fn.sharp_syscall(this, JObject.FromObject(args));
    }

    public async Task<object> createExecutePlan(string prompt, bool enableCot = true)
    {
      var functions = new JArray(from i in functionsDefinitions()
                                 select JObject.FromObject(i)).JsonToString();

      var zodSchema = lanOutputSchema(enableCot);

      var returnSchema = zodSchema.JsonToString();

      var requestMessage = new ChatMLMessage[]
      {
                new ChatMLMessage
                {
                  role = ChatMLMessageRole.system,
                  content = @$"You are an AI assistant that makes step-by-step plans to solve problems, utilizing external functions. Each step entails one plan followed by a function-call, which will later be executed to gather args for that step.
Make as few plans as possible if it can solve the problem.
The functions list is described using the following JSON schema array:
{functions}

Your specified plans should be output as JSON object array and adhere to the following JSON schema:
{returnSchema}

Only the listed functions are allowed to be used."
                },
                new ChatMLMessage
                {
                  role = ChatMLMessageRole.user,
                  content = $"Prompt: {prompt}\n\nPlan array:"
                }
      };
      var result = await llmPlaner.chatComplete(requestMessage, null);
      if (result.success == false)
      {
        throw new InvalidOperationException("LLM operation return not success");
      }
      var plan = parseComplete(result);

      if (plan.IsValid(zodSchema, out IList<ValidationError> _) == false)
        throw new InvalidDataException("Json Schema is invalid");

      LanOutputSchema[] plans;
      if (enableCot)
      {
        plans = (from b in plan select b.ToObject<LanOutputSchemaCot>()).ToArray();
      }
      else
      {
        plans = (from b in plan select b.ToObject<LanOutputSchema>()).ToArray();
      }

      Dictionary<int, JObject> returnStore = new Dictionary<int, JObject>();

      foreach (var step in plans)
        try
        {
          var fn = findFunction(step.functionName) ?? throw new InvalidDataException($"Function definition not found: {step.functionName};");
          JObject arg = new JObject();
          foreach (var b in step.args)
          {
            if (b.Value.Type == JTokenType.String && b.Value.ToString().StartsWith(STEP_PREFIX))
            {
              var keys = b.Value.ToString().Substring(STEP_PREFIX.Length).Split('.');
              var stepId = int.Parse(keys[0]);
              var stepResult = returnStore[stepId];
              if (keys.Length == 1)
                arg[b.Key] = stepResult;
              else
                arg[b.Key] = stepResult[keys[1]];
            }
            else
            {
              arg[b.Key] = b.Value;
            }
          }
          JObject functionresult = await fn.sharp_syscall(this, arg);
          returnStore[step.id] = functionresult;
        }
        catch (Exception ex)
        {
          throw new InvalidProgramException($"unable to process step {Environment.NewLine}{JObject.FromObject(step)}", ex);
        }
      return returnStore[plan.Count];
    }

    private static JArray parseComplete(ChatMLResponse msg)
    {
      var startIdx = msg.data?.IndexOf('[') ?? -1;
      var endIdx = msg.data?.LastIndexOf(']') ?? -1;
      if (startIdx == -1 || endIdx == -1 || endIdx < startIdx)
        throw new InvalidDataException($"Invalid JSON response startIdx={startIdx} endIdx={endIdx}");
      var content = msg.data.Substring(startIdx, endIdx - startIdx + 1).Trim();
      try
      {
        JArray json = JArray.Parse(content);
        return json;
      }
      catch (Exception ex)
      {
        throw new InvalidDataException($"unable to parse json array startIdx={startIdx} endIdx={endIdx} raw={Environment.NewLine}{msg.data}" +
            $"{Environment.NewLine}SplitedData:{Environment.NewLine}{content}", ex);
      }
    }

    internal static JSchema lanOutputSchema(bool enableCot)
    {
      var schema = GetSchemaGenerator();
      if (enableCot)
        return schema.Generate(typeof(List<LanOutputSchemaCot>));
      else
        return schema.Generate(typeof(List<LanOutputSchema>));
    }

    private class LanOutputSchemaCot : LanOutputSchema
    {
      public string thought { get; set; }
    }

    private class LanOutputSchema
    {
      public int id { get; set; }

      public string functionName { get; set; }

      [Description($"an object encapsulating all arguments for a function call. If an argument's value is derived from the return of a previous step, it should be as '{STEP_PREFIX}' + the ID of the previous step (e.g. '{STEP_PREFIX}1'). If an 'returnType' in **previous** step's function's json schema is object, '.' should be used to access its properties, else just use id with prefix. This approach should remain compatible with the 'args' attribute in the function's JSON schema.")]
      public JObject args { get; set; }
    }
  }
}
