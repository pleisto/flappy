using LawProgram = Pleisto.Flappy.Test.Law.Program;

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
    private bool NoGptTest => Environment.GetEnvironmentVariable("NO_GPT_TEST") == "true";

    /// <summary>
    /// Case test of Law
    /// </summary>
    /// <returns></returns>
    [Test]
    public async Task LawTestAsync()
    {
      if (NoGptTest == false)
      {
        LawProgram.ConsoleRun = false;
        await LawProgram.Main();
      }
    }
  }
}
