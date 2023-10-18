using McMaster.Extensions.CommandLineUtils;
using Pleisto.Flappy.Examples.CodeInterpreter;
using Pleisto.Flappy.Examples.Law;
using Pleisto.Flappy.Examples.Resume;
using System;
using System.Runtime.CompilerServices;

[assembly: InternalsVisibleTo("Pleisto.Flappy.Test")]

namespace Pleisto.Flappy.Examples
{
  [Subcommand(typeof(LawCase))]
  [Subcommand(typeof(ResumeCase))]
  [Subcommand(typeof(CodeInterpreterCase))]
  public class Program
  {
    public static int Main(string[] args)
    {
      try
      {
        return CommandLineApplication.Execute<Program>(args);
      }
      catch (Exception ex)
      {
        Console.Error.WriteLine(ex.ToString());
        return -1;
      }
    }

    public void OnExecute(CommandLineApplication app)
    {
      app.ShowHelp();
    }
  }
}
