namespace Pleisto.Flappy.LLM.Interfaces
{
  public class GenerateConfig
  {
    public int maxTokens { get; set; }
    public int temperature { get; set; }
    public int top_p { get; set; }
  }
}
