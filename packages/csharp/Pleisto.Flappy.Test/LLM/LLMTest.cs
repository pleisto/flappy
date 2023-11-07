using NUnit.Framework;
using Pleisto.Flappy.LLM;
using Pleisto.Flappy.LLM.Interfaces;
using System.Threading.Tasks;

namespace Pleisto.Flappy.Tests.LLM
{
  /// <summary>
  /// LLM test model
  /// </summary>
  /// <typeparam name="T">Test Type of ILLMBase</typeparam>
  public abstract class LLMTest<T>
    where T : ILLMBase
  {
    /// <summary>
    /// feature to create ILLMBase
    /// </summary>
    /// <returns></returns>
    protected abstract T OnLLMCreate();

    /// <summary>
    /// Hello World Test
    /// </summary>
    /// <returns></returns>
    [Test]
    public async Task HelloWorldTestAsync()
    {
      var llm = OnLLMCreate();
      var result = await llm.ChatComplete(new ChatMLMessage[]
       {
         new ChatMLMessage(){ Content = "Hello world", Role = ChatMLMessageRole.User}
       }, null);
      if (result.Success == false)
        Assert.Fail();
      Assert.Pass(result.Data);
    }
  }
}
