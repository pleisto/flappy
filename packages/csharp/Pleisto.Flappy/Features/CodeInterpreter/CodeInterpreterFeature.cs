using Pleisto.Flappy.CodeInterpreter;
using Pleisto.Flappy.Interfaces;
using Pleisto.Flappy.Utils;

namespace Pleisto.Flappy.Features.CodeInterpreter
{  /// <summary>
   /// CodeInterpreter feature
   /// </summary>
    public class CodeInterpreterFeature : CodeInterpreterFeature<CodeInterpreterInput, CodeInterpreterOutput, CodeInterpreterOptions>
    {
        /// <summary>
        /// Create CodeInterpreter feature
        /// </summary>
        /// <param name="Name"></param>
        /// <param name="options"></param>
        public CodeInterpreterFeature(string Name, CodeInterpreterOptions options = null) : base(Name, options)
        {
        }
    }

    /// <summary>
    /// CodeInterpreter feature
    /// </summary>
    /// <typeparam name="TArgs">Argument of feature</typeparam>
    /// <typeparam name="TReturn">Return of feature</typeparam>
    /// <typeparam name="TOptions">Options of feature</typeparam>
    public class CodeInterpreterFeature<TArgs, TReturn, TOptions> : FlappyFeatureBase<TArgs, TReturn, TOptions>, IFlappyFeature,ICodeInterpreterFeature
      where TArgs : CodeInterpreterInput
      where TReturn : CodeInterpreterOutput, new()
      where TOptions : CodeInterpreterOptions
    {
        /// <summary>
        /// Create CodeInterpreter feature
        /// </summary>
        /// <param name="Name"></param>
        /// <param name="options"></param>
        public CodeInterpreterFeature(string Name, CodeInterpreterOptions options = null) : base(new CodeInterpreterFeatureDefinition<TArgs, TReturn>
        {
            Name = Name,
            Description = TemplateRenderer.Render("features.codeInterpreter.description", new Dictionary<string, object>
            {
                ["#enabled"] = options?.EnableNetwork ?? false
            })
        })
        {
        }

        /// <summary>
        /// CodeInterpreter call entry
        /// </summary>
        /// <param name="agent"></param>
        /// <param name="args"></param>
        /// <returns></returns>
        /// <exception cref="NotImplementedException"></exception>
        public override Task<TReturn> Call(FlappyAgent agent, TArgs args)
        {
            if (string.IsNullOrWhiteSpace(args?.Code))
                throw new InvalidProgramException($"null or whitespace codeinterpreter code");
            if (args.Code.Contains("def main():"))
                throw new InvalidProgramException("Function \"main\" not found");

            var result = NativeHandler.EvalPythonCode(args.Code, Options?.EnableNetwork ?? false, Options?.Env ?? new Dictionary<string, string>(), Options?.CacheDir);
            if (string.IsNullOrWhiteSpace(result.StdErr) == false)
                throw new Exception(result.StdErr);
            return Task.FromResult(new TReturn
            {
                Result = result.StdOut
            });
        }
    }
}
