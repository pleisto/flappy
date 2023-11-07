using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System.Text;

namespace Pleisto.Flappy.Utils
{
    /// <summary>
    /// Program extensions of Json
    /// </summary>
    internal static class JsonExtensions
    {
        /// <summary>
        /// Json convert to string with special formatting
        /// </summary>
        /// <param name="json"></param>
        /// <returns></returns>
        public static string JsonToString(this object json)
        {
            using var ms = new MemoryStream();
            using (var sw = new StreamWriter(ms, Encoding.UTF8, 512, true)
            {
                NewLine = "\n",
            })
            using (var jsonWriter = new JsonTextWriter(sw)
            {
                Indentation = 1,
                Formatting = Formatting.None,
            })
            {
                lock (jsonSerializer)
                    jsonSerializer.Serialize(jsonWriter, json);
            }
            return Encoding.UTF8.GetString(ms.ToArray()).Trim();
        }

        /// <summary>
        /// Convert json to object
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="json"></param>
        /// <returns></returns>
        public static T JsonToObject<T>(this JObject json)
        {
            lock (jsonSerializer)
                return json.ToObject<T>(jsonSerializer);
        }

        /// <summary>
        /// Convert object to json
        /// </summary>
        /// <param name="obj"></param>
        /// <returns></returns>
        public static JObject ObjectToJson(this object obj)
        {
            lock (jsonSerializer)
                return JObject.FromObject(obj, jsonSerializer);
        }

        /// <summary>
        /// Default jsonSerializer Settings
        /// </summary>
        private static readonly JsonSerializer jsonSerializer = new JsonSerializer()
        {
            //NullValueHandling = NullValueHandling.Ignore,
        };
    }
}
