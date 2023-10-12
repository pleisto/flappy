using Microsoft.Extensions.Logging;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json.Schema;
using Pleisto.Flappy.CodeInterpreter;
using Pleisto.Flappy.Interfaces;
using Pleisto.Flappy.LLM;
using Pleisto.Flappy.LLM.Interfaces;
using Pleisto.Flappy.Utils;
using System.ComponentModel;

namespace Pleisto.Flappy
{
  /// <summary>
  /// Flappy Agent
  /// </summary>
  public partial class FlappyAgent
  {
    internal readonly FlappyAgentConfig config;
    internal readonly ILLMBase llm;
    internal readonly ILLMBase llmPlaner;
    private int retry = 0;
    private const int DEFAULT_RETRY = 1;
    private readonly ILogger<FlappyAgent> logger;

    /// <summary>
    /// Create a flappy agent.
    /// </summary>
    /// <param name="config">config of flappy</param>
    /// <param name="llm"></param>
    /// <param name="llmPlaner"></param>
    /// <param name="logger">Logger of FlappyAgent</param>
    /// <exception cref="NullReferenceException"></exception>
    public FlappyAgent(FlappyAgentConfig config, ILLMBase llm, ILLMBase llmPlaner, ILogger<FlappyAgent> logger)
    {
      this.config = config;
      if ((config.Functions?.Length ?? 0) <= 0)
        throw new NullReferenceException($"config.functions not be null");
      this.llm = llm ?? config.LLM;
      this.llmPlaner = llmPlaner ?? this.llm;
      this.retry = config.Retry ?? DEFAULT_RETRY;
      this.logger = logger;
    }

    /// <summary>
    /// Get function definitions as a JSON Schema object array.
    /// </summary>
    /// <returns></returns>
    public IEnumerable<IFlappyFunction> FunctionsDefinitions() => from i in config.Functions
                                                                  select i;

    /// <summary>
    /// Find function by name.
    /// </summary>
    /// <param name="name"></param>
    /// <returns></returns>
    public IFlappyFunction FindFunction(string name) => (from i in config.Functions
                                                         where i.Name.Equals(name?.Trim(), StringComparison.OrdinalIgnoreCase)
                                                         select i).FirstOrDefault();

    /// <summary>
    ///  List all synthesized functions.
    /// </summary>
    /// <returns></returns>
    public IEnumerable<IFlappyFunction> SynthesizedFunctions() => from i in config.Functions
                                                                  let type = i.GetType()
                                                                  where type.BaseType == typeof(SynthesizedFunction<,>)
                                                                  select i;

    /// <summary>
    /// List all invoke functions.
    /// </summary>
    /// <returns></returns>
    public IEnumerable<IFlappyFunction> InvokeFunctions() => from i in config.Functions
                                                             let type = i.GetType()
                                                             where type.BaseType == typeof(InvokeFunction<,>)
                                                             select i;

    /// <summary>
    /// Call a function by name.
    /// </summary>
    /// <param name="name"></param>
    /// <param name="args"></param>
    /// <returns></returns>
    /// <exception cref="InvalidProgramException"></exception>
    public async Task<object> CallFunction(string name, object args)
    {
      var fn = FindFunction(name) ?? throw new InvalidProgramException($"no function found: {name}");
      return await fn.SharpSystemCall(this, JObject.FromObject(args));
    }

    /// <summary>
    ///
    /// </summary>
    /// <param name="prompt">user input prompt</param>
    /// <param name="enableCot">enable CoT to improve the plan quality, but it will be generally more tokens. Default is true.</param>
    /// <returns></returns>
    public async Task<object> CreateExecutePlan(string prompt, bool enableCot = true)
    {
      var functions = new JArray(from i in FunctionsDefinitions()
                                 select JObject.FromObject(i)).JsonToString();

      var zodSchema = GetLanOutputSchema(enableCot);

      var returnSchema = zodSchema.JsonToString();

      var requestMessage = new ChatMLMessage[]
      {
        new ChatMLMessage
        {
          Role = ChatMLMessageRole.System,
          Content = @$"You are an AI assistant that makes step-by-step plans to solve problems, utilizing external functions. Each step entails one plan followed by a function-call, which will later be executed to gather args for that step.
Make as few plans as possible if it can solve the problem.
The functions list is described using the following JSON schema array:
{functions}

Your specified plans should be output as JSON object array and adhere to the following JSON schema:
{returnSchema}

Only the listed functions are allowed to be used."
        },
        new ChatMLMessage
        {
          Role = ChatMLMessageRole.User,
          Content = $"Prompt: {prompt}\n\nPlan array:"
        }
      };
      var result = await llmPlaner.ChatComplete(requestMessage, null);
      if (result.Success == false)
      {
        throw new InvalidOperationException("LLM operation return not success");
      }
      var plan = ParseComplete(result);

      if (plan.IsValid(zodSchema, out IList<ValidationError> errorList) == false)
        throw new InvalidDataException($"Json Schema is invalid");

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
          var fn = FindFunction(step.FunctionName) ?? throw new InvalidDataException($"Function definition not found: {step.FunctionName};");
          JObject arg = new JObject();
          foreach (var b in step.Args)
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
          JObject functionresult = await fn.SharpSystemCall(this, arg);
          returnStore[step.Id] = functionresult;
        }
        catch (Exception ex)
        {
          throw new InvalidProgramException($"unable to process step {Environment.NewLine}{JObject.FromObject(step)}", ex);
        }
      return returnStore[plan.Count];
    }

    private static JArray ParseComplete(ChatMLResponse msg)
    {
      var startIdx = msg.Data?.IndexOf('[') ?? -1;
      var endIdx = msg.Data?.LastIndexOf(']') ?? -1;
      if (startIdx == -1 || endIdx == -1 || endIdx < startIdx)
        throw new InvalidDataException($"Invalid JSON response startIdx={startIdx} endIdx={endIdx}");
      var content = msg.Data.Substring(startIdx, endIdx - startIdx + 1).Trim();
      try
      {
        JArray json = JArray.Parse(content);
        return json;
      }
      catch (Exception ex)
      {
        throw new InvalidDataException($"unable to parse json array startIdx={startIdx} endIdx={endIdx} raw={Environment.NewLine}{msg.Data}" +
            $"{Environment.NewLine}SplitedData:{Environment.NewLine}{content}", ex);
      }
    }

    /// <summary>
    /// Call by code interpreter
    /// </summary>
    /// <param name="prompt"></param>
    /// <returns></returns>
    /// <exception cref="Exception"></exception>
    public async Task<object> CallCodeInterpreter(string prompt)
    {
      if (config.CodeInterpreter == null)
        throw new Exception($"Code interpreter is not enabled!");
      var originalRequestMessage = new ChatMLMessage[]
      {
        new ChatMLMessage
        {
          Role = ChatMLMessageRole.System,
          Content= $@"You are an AI that writes Python code using only the built-in library. After you supply the Python code, it will be run in a safe sandbox. The execution time is limited to 120 seconds. The task is to define a function named ""main"" that doesn't take any parameters. The output should be a JSON object: {{""code"": string}}.
            Network access is {(config.CodeInterpreter.EnableNetwork == true ? "enabled" : "disabled")}"
        },
        new ChatMLMessage
        {
          Role = ChatMLMessageRole.User,
          Content = $"{prompt}\n\noutput json:\n"
        }
      };

      var retry = this.retry;
      var requestMessage = originalRequestMessage;
      ChatMLResponse result = null;
      while (true)
      {
        try
        {
          if (retry != this.retry)
          {
            logger?.LogDebug("Attempt retry: {}", this.retry - retry);
          }
          result = await llm.ChatComplete(requestMessage);
          var data = JObject.Parse(result.Data)["code"]?.ToString()?.Replace("\\n", "\n");
          if (data == null)
            throw new Exception("Invalid JSON response");
          if (data.Contains("def main():"))
            throw new Exception("Function \"main\" not found");
          logger?.LogDebug("Generated Code: {}", data);
          var pythonResult = NativeHandler.EvalPythonCode($"{data}\nprint(main())", config.CodeInterpreter.EnableNetwork == true, config.CodeInterpreter.Env ?? new Dictionary<string, string>(), config.CodeInterpreter.CacheDir);
          if (string.IsNullOrWhiteSpace(pythonResult.StdErr))
            throw new Exception(data);
          logger?.LogDebug("Code Interpreter Output: {}", pythonResult.StdOut);
          return pythonResult.StdOut;
        }
        catch (Exception ex)
        {
          logger?.LogError(ex.ToString());
          if (retry <= 0)
            throw new Exception($"Interrupted, function call failed. Please refer to the error message above.");
          retry -= 1;
          if (result?.Success == true && result.Data != null)
          {
            requestMessage = requestMessage.Union(new ChatMLMessage[]
            {
              new ChatMLMessage
              {
                Role = ChatMLMessageRole.Assistant,
                Content = result?.Data
              },
              new ChatMLMessage
              {
                Role = ChatMLMessageRole.User,
                Content= @$"You response is invalid for the following reason:
            {ex.Message}

            Please try again."
              }
            }).ToArray();
          }
        }
      }
    }

    internal static JSchema GetLanOutputSchema(bool enableCot)
    {
      var schema = GetSchemaGenerator();
      if (enableCot)
        return schema.Generate(typeof(List<LanOutputSchemaCot>));
      else
        return schema.Generate(typeof(List<LanOutputSchema>));
    }

    private class LanOutputSchemaCot : LanOutputSchema
    {
      public string Thought { get; set; }
    }

    private class LanOutputSchema
    {
      public int Id { get; set; }

      public string FunctionName { get; set; }

      [Description($"an object encapsulating all arguments for a function call. If an argument's value is derived from the return of a previous step, it should be as '{STEP_PREFIX}' + the ID of the previous step (e.g. '{STEP_PREFIX}1'). If an 'returnType' in **previous** step's function's json schema is object, '.' should be used to access its properties, else just use id with prefix. This approach should remain compatible with the 'args' attribute in the function's JSON schema.")]
      public JObject Args { get; set; }
    }
  }
}
