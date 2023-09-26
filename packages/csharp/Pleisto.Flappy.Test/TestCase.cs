using LawProgram = Pleisto.Flappy.Test.Law.Program;

namespace Pleisto.Flappy.Test
{
  public class TestCase
  {
    bool NoGptTest => Environment.GetEnvironmentVariable("NO_GPT_TEST") == "true";
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
