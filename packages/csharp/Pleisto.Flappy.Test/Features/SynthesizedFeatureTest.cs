using NUnit.Framework;
using Pleisto.Flappy.Features.Syntehesized;
using Pleisto.Flappy.Interfaces;
using Pleisto.Flappy.Utils;

namespace Pleisto.Flappy.Tests.Features
{
    /// <summary>
    /// Synthesized Feature test
    /// </summary>
    public class SynthesizedFeatureTest :
    FeatureTestBase<
        SynthesizedFeature<EasyPayload<string>, EasyPayload<string>, FlappyFeatureOption>,
        SynthesizedFeatureDefinition<EasyPayload<string>, EasyPayload<string>>
        >
    {
  
    }
}
