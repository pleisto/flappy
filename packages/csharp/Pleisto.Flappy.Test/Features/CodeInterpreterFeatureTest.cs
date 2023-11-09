using Pleisto.Flappy.Features.CodeInterpreter;
using Pleisto.Flappy.Interfaces;

namespace Pleisto.Flappy.Tests.Features
{
    /// <summary>
    /// Code Interpreter Feature test
    /// </summary>
    public class CodeInterpreterFeatureTest :
    FeatureTestBase<
        CodeInterpreterFeature<CodeInterpreterInput, CodeInterpreterOutput, CodeInterpreterOptions>,
        CodeInterpreterFeatureDefinition<CodeInterpreterInput, CodeInterpreterOutput>
        >
    {
        protected override FeatureGenerated CreateFeature()
        {
            var ret = new CodeInterpreterFeature("testCodeInterpreter");
            return new()
            {
                Feature = ret,
                Define = ret.Define as CodeInterpreterFeatureDefinition<CodeInterpreterInput,CodeInterpreterOutput>
            };
        }
    }
}
