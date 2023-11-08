using Microsoft.Extensions.Logging;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json.Schema;
using Pleisto.Flappy.Exceptions;
using Pleisto.Flappy.Features.Invoke;
using Pleisto.Flappy.Features.Syntehesized;
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
        internal readonly int retry = 0;

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
                throw new NullReferenceException($"{nameof(Features)} not be null");
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
            var fn = FindFeature(name) ?? throw new InvalidProgramException($"no feature found: {name}");
            //   return (await fn.SharpSystemCall(this, JObject.FromObject(args))).ToObject<T>();
            return await fn.SystemCall(this, args);
        }

        /// <summary>
        /// Create and Execute Plan
        /// </summary>
        /// <param name="prompt">user input prompt</param>
        /// <param name="enableCot">enable CoT to improve the plan quality, but it will be generally more tokens. Default is true.</param>
        /// <returns></returns>
        public async Task<object> ExecutePlan(string prompt, bool enableCot = true)
        {
            JSchema zodSchema;
            var schema = GetSchemaGenerator();
            if (enableCot)
                zodSchema = schema.Generate(typeof(List<LanOutputSchemaCot>));
            else
                zodSchema = schema.Generate(typeof(List<LanOutputSchema>));

            var originalRequestMesasge = new ChatMLMessage[]
            {
        new ChatMLMessage
        {
          Role = ChatMLMessageRole.System,
          Content = TemplateRenderer.Render("agent.systemMessage",new Dictionary<string, object>
          {
            ["functions"] = new JArray(from i in FeatureDefinitions()
                                select JObject.FromObject(i)).JsonToString(),
            ["returnSchema"] = zodSchema.JsonToString()
          })
        },
        new ChatMLMessage
        {
          Role = ChatMLMessageRole.User,
          Content = TemplateRenderer.Render("agent.userMessage",new Dictionary<string, object>
          {
            ["prompt"] = prompt
          })
        }
            };
            var requestMessage = originalRequestMesasge;
            int retryCount = retry;
            JArray plan = null;
            ChatMLResponse result = null;
            while (true)
                try
                {
                    if (retryCount != retry)
                    {
                        logger.LogWarning("Plan retry {retryCount}/{retry}", retryCount, retry);
                    }
                    result = await llmPlaner.ChatComplete(requestMessage, null);
                    if (result.Success == false)
                    {
                        throw new LLMNotSuccessException();
                    }
                    plan = ParseComplete(result);

                    if (plan.IsValid(zodSchema, out IList<ValidationError> errorList) == false)
                        throw new InvalidJsonWithSchemaValidationException(errorList);
                    break;
                }
                catch (Exception ex)
                {
                    logger.LogError(ex, "Retry {retry} of {retryCount}", retryCount, retry);
                    if (retryCount <= 0)
                        throw new TooMoreRetryException(retryCount, ex);
                    retryCount -= 1;
                    if (result?.Success == true && (result.Data?.Length ?? 0) > 0)
                    {
                        requestMessage = originalRequestMesasge.Union(new ChatMLMessage[]
                        {
                            new ChatMLMessage
                            {
                                Role = ChatMLMessageRole.System,
                                Content = result?.Data ?? ""
                            },
                            new ChatMLMessage
                            {
                                Role = ChatMLMessageRole.User,
                                Content = TemplateRenderer.Render("error.retry",new Dictionary<string, object>
                                {
                                    ["message"]= ex.Message
                                })
                            }
                        }).ToArray();
                    }
                }

            LanOutputSchema[] plans;
            try
            {
                if (enableCot)
                {
                    plans = (from b in plan select b.ToObject<LanOutputSchemaCot>()).ToArray();
                }
                else
                {
                    plans = (from b in plan select b.ToObject<LanOutputSchema>()).ToArray();
                }
            }
            catch (Exception ex)
            {
                throw new StepPlainException(new JObject { ["plan"] = plan }, ex);
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
                    try
                    {
                        returnStore[step.Id] = JObject.FromObject(await fn.SystemCall(this, fn.JsonToArgs(arg)));
                    }
                    catch (Exception ex)
                    {
                        throw new InvalidProgramException($"feature unhandled exception! name={fn.Name}", ex);
                    }
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
