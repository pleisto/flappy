using NUnit.Framework;
using Pleisto.Flappy.Examples;
using Pleisto.Flappy.Examples.CodeInterpreter;
using Pleisto.Flappy.Examples.Law;
using Pleisto.Flappy.Examples.Resume;
using System;
using System.Threading.Tasks;

namespace Pleisto.Flappy.Tests
{
  /// <summary>
  /// ChatGPT Test Case
  /// </summary>
  public class Samples
  {
    /// <summary>
    /// Disable GPT Test Case on environment set
    /// </summary>
    private static bool NoGptTest => Environment.GetEnvironmentVariable("NO_GPT_TEST") == "true";

    private async Task TestCaseBase<T>()
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

    /// <summary>
    /// Case test of Code Interpreter
    /// </summary>
    /// <returns></returns>
    [Test]
    public async Task CodeInterpreterTaskAsync()
      => await TestCaseBase<CodeInterpreterCase>();
  }
}
