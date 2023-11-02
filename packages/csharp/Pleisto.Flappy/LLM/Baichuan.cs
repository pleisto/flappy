using Newtonsoft.Json.Linq;
using Pleisto.Flappy.LLM.Interfaces;
using Pleisto.Flappy.Utils;
using System.Security.Cryptography;
using System.Text;

namespace Pleisto.Flappy.LLM
{
  /// <summary>
  /// LLM of Baichuan
  /// https://platform.baichuan-ai.com/docs/api
  /// </summary>
  public class Baichuan : ILLMBase
  {
    /// <summary>
    /// Init baichuan API
    /// </summary>
    /// <param name="apiKey">Baichuan API Token with Bearer</param>
    /// <param name="model">model to use</param>
    /// <param name="secretKey">Baichuan API Secret Key</param>
    public Baichuan(string model, string apiKey, string secretKey)
    {
      this.apiKey = apiKey;
      this.model = model;
      this.secretKey = secretKey;
    }

    private readonly string secretKey;
    private readonly string apiKey;
    private readonly string model;
    private readonly DateTime UTCBeginTime = new DateTime(1970, 1, 1, 0, 0, 0, 0);

    /// <inheritdoc/>
    public int MaxTokens => 4096;

    /// <inheritdoc/>
    public async Task<ChatMLResponse> ChatComplete(ChatMLMessage[] message, GenerateConfig config = null)
    {
      var ts = ((int)(DateTime.UtcNow - UTCBeginTime).TotalSeconds).ToString();
      using HttpClient client = new HttpClient();
      client.DefaultRequestHeaders.Add("Authorization", apiKey);
      client.DefaultRequestHeaders.Add("X-BC-Timestamp", ts);
      client.DefaultRequestHeaders.Add("X-BC-Sign-Algo", "MD5");
      var content = new
      {
        model = model ?? "Baichuan2-53B",
        messages = from b in message
                   select new
                   {
                     role = b.Role.ToString().ToLower(),
                     content = b.Content,
                   },
      }.ObjectToJson().JsonToString();
      var contentJson = new JObject
      {
        ["model"] = model ?? "Baichuan2-53B",
        ["messages"] = new JArray(from b in message
                                  select new JObject
                                  {
                                    ["role"] = b.Role.ToString().ToLower(),
                                    ["content"] = b.Content
                                  })
      };
      if (config != null)
      {
        contentJson["parameters"] = new JObject()
        {
          ["temperature"] = config.Temperature,
          ["top_p"] = config.Top_P
        };
      }
      var sign = ComputeMD5($"{secretKey}{content}{ts}");
      client.DefaultRequestHeaders.Add("X-BC-Signature", sign);
      using var response = await client.PostAsync("https://api.baichuan-ai.com/v1/chat", new StringContent(content, Encoding.UTF8, "application/json"));
      var responseJson = JObject.Parse(await response.Content.ReadAsStringAsync());
      if ((int)responseJson["code"] == 0)
        return new ChatMLResponse
        {
          Data = ((JArray)responseJson["data"]["messages"]).First()["content"].ToString(),
          Success = true
        };
      else
      {
        Console.WriteLine($"Invalid Baichuan Response: {responseJson["msg"]}");
        return new ChatMLResponse
        {
          Success = false,
        };
      }
    }

    private static string ComputeMD5(string data)
    {
#if NET7_0_OR_GREATER
      var hash = MD5.HashData(Encoding.UTF8.GetBytes(data));
#else
      using var md5 = MD5.Create();
      var hash = md5.ComputeHash(Encoding.UTF8.GetBytes(data));
#endif
      return string.Join("", from b in hash
                             select b.ToString("x2"));
    }
  }
}
