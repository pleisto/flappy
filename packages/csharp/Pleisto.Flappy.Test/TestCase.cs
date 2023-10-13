
using Pleisto.Flappy.Examples;
using Pleisto.Flappy.Examples.Law;
using Pleisto.Flappy.Examples.Resume;

namespace Pleisto.Flappy.Test
{
  /// <summary>
  /// ChatGPT Test Case
  /// </summary>
  public class TestCase
  {
    /// <summary>
    /// Disable GPT Test Case on environment set
    /// </summary>
    static private bool NoGptTest => Environment.GetEnvironmentVariable("NO_GPT_TEST") == "true";

    async Task TestCaseBase<T>()
      where T : ExampleBase, new()
    {
      if (NoGptTest == false)
        await new T().OnExecuteAsync();
    }

    /// <summary>
    /// Case test of Law
    /// </summary>
    /// <returns></returns>
    [Test]
    public async Task LawTestAsync()
=> await TestCaseBase<LawCase>();

    /// <summary>
    /// Case test of Law
    /// </summary>
    /// <returns></returns>
    [Test]
    public async Task ResumeTestAsync()
=> await TestCaseBase<ResumeCase>();
  }
}
