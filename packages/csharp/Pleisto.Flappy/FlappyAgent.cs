using Microsoft.Extensions.Logging;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json.Schema;
using Pleisto.Flappy.CodeInterpreter;
using Pleisto.Flappy.Exceptions;
using Pleisto.Flappy.Interfaces;
using Pleisto.Flappy.LLM;
using Pleisto.Flappy.LLM.Interfaces;
using Pleisto.Flappy.Utils;
using System.ComponentModel;
using Pleisto.Flappy.Features.Invoke;
using Pleisto.Flappy.Features.Syntehesized;

namespace Pleisto.Flappy
{
  /// <summary>
  /// Flappy Agent
  /// </summary>
  public partial class FlappyAgent
  {
    /// <summary>
    /// Config
    /// </summary>
    internal readonly FlappyAgentConfig config;

    /// <summary>
    /// LLM
    /// </summary>
    internal readonly ILLMBase llm;

    /// <summary>
    /// LLM PLaner
    /// </summary>
    internal readonly ILLMBase llmPlaner;

    /// <summary>
    /// Configured of Retry Count
    /// </summary>
    private readonly int retry = 0;

    /// <summary>
    /// Default of Retry Count
    /// </summary>
    private const int DEFAULT_RETRY = 1;

    /// <summary>
    /// Logger
    /// </summary>
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
      if ((config.Features?.Length ?? 0) <= 0)
        throw new NullReferenceException($"config.Features not be null");
      this.llm = llm ?? config.LLM;
      this.llmPlaner = llmPlaner ?? config.LLMPlaner ?? this.llm;
      this.retry = config.Retry ?? DEFAULT_RETRY;
      this.logger = logger;
    }

    /// <summary>
    /// Get feature definitions as a JSON Schema object array.
    /// </summary>
    /// <returns></returns>
    public IEnumerable<IFlappyFeature> FeatureDefinitions() => from i in config.Features
                                                               select i;

    /// <summary>
    /// Find feature by name.
    /// </summary>
    /// <param name="name"></param>
    /// <returns></returns>
    public IFlappyFeature FindFeature(string name) => (from i in config.Features
                                                       where i.Name.Equals(name?.Trim(), StringComparison.OrdinalIgnoreCase)
                                                       select i).FirstOrDefault();

    /// <summary>
    ///  List all synthesized features.
    /// </summary>
    /// <returns></returns>
    public IEnumerable<IFlappyFeature> SynthesizedFeatures() => from i in config.Features
                                                                let type = i.GetType()
                                                                where type.BaseType == typeof(SynthesizedFeature<,,>)
                                                                select i;

    /// <summary>
    /// List all invoke features.
    /// </summary>
    /// <returns></returns>
    public IEnumerable<IFlappyFeature> InvokeFeatures() => from i in config.Features
                                                           let type = i.GetType()
                                                           where type.BaseType == typeof(InvokeFeature<,,>)
                                                           select i;

    /// <summary>
    /// Call a feature by name.
    /// </summary>
    /// <param name="name"></param>
    /// <param name="args"></param>
    /// <returns></returns>
    /// <exception cref="InvalidProgramException"></exception>
    public async Task<object> CallFeature(string name, object args)
    {
      var fn = FindFeature(name) ?? throw new InvalidProgramException($"no function found: {name}");
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
      var features = new JArray(from i in FeatureDefinitions()
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
{features}

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
        throw new LLMNotSuccessException();
      }
      var plan = ParseComplete(result);

      if (plan.IsValid(zodSchema, out IList<ValidationError> errorList) == false)
        throw new InvalidJsonWithSchemaValidationException(errorList);

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
          var fn = FindFeature(step.FeatureName) ?? throw new InvalidDataException($"Feature definition not found: {step.FeatureName};");
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
          throw new StepPlainException(JObject.FromObject(step), ex);
        }
      return returnStore[plan.Count];
    }

    private static JArray ParseComplete(ChatMLResponse msg)
    {
      var startIdx = msg.Data?.IndexOf('[') ?? -1;
      var endIdx = msg.Data?.LastIndexOf(']') ?? -1;
      if (startIdx == -1 || endIdx == -1 || endIdx < startIdx)
        throw new InvalidJsonDataException(startIdx, endIdx, msg.Data);
      var content = msg.Data.Substring(startIdx, endIdx - startIdx + 1).Trim();
      try
      {
        JArray json = JArray.Parse(content);
        return json;
      }
      catch (Exception ex)
      {
        throw new InvalidJsonDataException(startIdx, endIdx, msg.Data, content, ex);
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
        throw new CodeInterpreterNotEnabledException();
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
            throw new Exception("Feature \"main\" not found");
          logger?.LogDebug("Generated Code: {}", data);
          var pythonResult = NativeHandler.EvalPythonCode($"{data}\nprint(main())", config.CodeInterpreter.EnableNetwork == true, config.CodeInterpreter.Env ?? new Dictionary<string, string>(), config.CodeInterpreter.CacheDir);
          if (string.IsNullOrWhiteSpace(pythonResult.StdErr))
            throw new Exception(data);
          logger?.LogDebug("Code Interpreter Output: {}", pythonResult.StdOut);
          return pythonResult.StdOut;
        }
        catch (Exception ex)
        {
          logger?.LogError("Error: {exString}", ex.ToString());
          if (retry <= 0)
            throw new CodeInterpreterRetryException(retry, ex);
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

      public string FeatureName { get; set; }

      [Description($"an object encapsulating all arguments for a function call. If an argument's value is derived from the return of a previous step, it should be as '{STEP_PREFIX}' + the ID of the previous step (e.g. '{STEP_PREFIX}1'). If an 'returnType' in **previous** step's function's json schema is object, '.' should be used to access its properties, else just use id with prefix. This approach should remain compatible with the 'args' attribute in the function's JSON schema.")]
      public JObject Args { get; set; }
    }
  }
}
