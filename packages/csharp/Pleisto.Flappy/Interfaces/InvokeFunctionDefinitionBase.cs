using Newtonsoft.Json;

namespace Pleisto.Flappy.Interfaces
{
  public class InvokeFunctionDefinitionBase<TArgs, TReturn>
    where TArgs : new()
    where TReturn : new()
  {
    [JsonRequired]
    public string name { get; set; }

    public string description { get; set; }

    //[JsonRequired]
    //public TArgs args { get; set; }

    //[JsonRequired]
    //public TReturn returnType { get; set; }
  }
}
