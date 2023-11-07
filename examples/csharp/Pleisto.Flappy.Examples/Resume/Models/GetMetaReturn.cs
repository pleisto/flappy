using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace Pleisto.Flappy.Test.Resume
{
  [JsonObject(ItemNullValueHandling = NullValueHandling.Include)]
  internal class GetMetaReturn
  {
    public string name { get; set; } = string.Empty;

    public string profession { get; set; } = string.Empty;

    public int experienceYears { get; set; }

    public MetaReturnSkill[] skills { get; set; } = Array.Empty<MetaReturnSkill>();

    public MetaReturnExperiences[] projectExperiences { get; set; } = Array.Empty<MetaReturnExperiences>();

    public MetaReturnEducation[] education { get; set; } = Array.Empty<MetaReturnEducation>();

    public override string ToString()
    {
      return JObject.FromObject(this).ToString();
    }
  }
}
