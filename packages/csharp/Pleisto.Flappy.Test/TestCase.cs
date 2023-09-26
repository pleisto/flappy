using LawProgram = Pleisto.Flappy.Test.Law.Program;

namespace Pleisto.Flappy.Test
{
  public class TestCase
  {
    /// <summary>
    /// Case test of Law
    /// </summary>
    /// <returns></returns>
    [Test]
    public async Task LawTestAsync()
    {
      LawProgram.ConsoleRun = false;
      await LawProgram.Main();
    }
  }
}
