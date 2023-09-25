using Newtonsoft.Json;

namespace Pleisto.Flappy.Interfaces
{
  public class FunctionsDefinitionBase<TArgs, TReturns>
    where TArgs : class
    where TReturns : class
  {
    //[JsonRequired]
    public string name { get; set; }

    public string description { get; set; } = null;

    [JsonRequired]
    public TArgs args { get; set; }

    [JsonRequired]
    public TReturns returnType { get; set; }
  }
}
