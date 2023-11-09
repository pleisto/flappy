using Newtonsoft.Json.Linq;
using Pleisto.Flappy.Exceptions;
using Pleisto.Flappy.Interfaces;
using Pleisto.Flappy.LLM.Interfaces;
using Pleisto.Flappy.Utils;

namespace Pleisto.Flappy.Features.Syntehesized
{

    /// <summary>
    /// Synthesized Feature
    /// </summary>
    /// <typeparam name="TArgs">Argument of feature</typeparam>
    /// <typeparam name="TReturn">Return of feature</typeparam>
    /// <typeparam name="TOptions">Options of feature</typeparam>
    public class SynthesizedFeature<TArgs, TReturn, TOptions> : FlappyFeatureBase<TArgs, TReturn, TOptions>, IFlappyFeature , ISynthesizedFeature
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
            var returnTypeSchema = JsonSchema.FromType<TReturn>();
            var argsSchema = JsonSchema.FromType<TArgs>();
            var prompt = JObject.FromObject(args); //(args as object) is string ? args as string : JObject.FromObject(args).ToString();
            var originalRequestMessage = new ChatMLMessage[]{
                  new ChatMLMessage
                  {
                    Role = ChatMLMessageRole.System,
                    Content = TemplateRenderer.Render("features.synthesized.systemMessage",new Dictionary<string, object>
                    {
                      ["describe"] = describe,
                      ["argsSchema"] = argsSchema.JsonToString(),
                      ["returnTypeSchema"] = returnTypeSchema.JsonToString(),
                    })
                  },
                  new ChatMLMessage
                  {
                    Role = ChatMLMessageRole.User,
                    Content = TemplateRenderer.Render("features.synthesized.userMessage",new Dictionary<string, object>
                    {
                      ["prompt"] = prompt,
                    })
                  }
              };
            var requestMessage = originalRequestMessage;
            ChatMLResponse result = null;
            int retry = Options?.Retry ?? agent.retry;

            while (true)
                try
                {
                    result = await agent.llm.ChatComplete(requestMessage);
                    var data = ParseComplete(result);

                    return data;
                }
                catch (Exception ex)
                {
                    if (retry <= 0)
                        throw new TooMoreRetryException(retry, ex);
                    retry -= 1;
                    if (result?.Success == true && (result.Data?.Length ?? 0) > 0)
                    {
                        requestMessage = originalRequestMessage.Union(new ChatMLMessage[]
                        {
              new ChatMLMessage{
                Role = ChatMLMessageRole.Assistant,
                Content = result?.Data ?? ""
              },
              new ChatMLMessage
              {
                Role= ChatMLMessageRole.User,
                Content = TemplateRenderer.Render("error.retry",new Dictionary<string, object>
                {
                  ["message"] = ex.Message
                })
              }
                        }).ToArray();
                    }
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
