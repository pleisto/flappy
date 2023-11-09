using Newtonsoft.Json.Linq;
using NUnit.Framework;
using Pleisto.Flappy.Features.CodeInterpreter;
using Pleisto.Flappy.Features.Invoke;
using Pleisto.Flappy.Features.Syntehesized;
using Pleisto.Flappy.Interfaces;
using Pleisto.Flappy.Utils;
using System.Reflection;

namespace Pleisto.Flappy.Tests
{
    public class Agent
    {
        [Test]
        public async Task CallFeatureTestAsync()
        {
            const string callName = "testCall";
            const string callResult = "validConfigure";
            var agent = new FlappyAgent(new FlappyAgentConfig
            {
                Features = new IFlappyFeature[]
                 {
                     new InvokeFeature<EasyPayload<string>, EasyPayload<string>, FlappyFeatureOption>(new InvokeFeatureDefinition<EasyPayload<string>, EasyPayload<string>>
                     {
                         Name = callName,
                         Args = string.Empty,
                         Description = string.Empty,
                         ReturnType = string.Empty,
                         Resolve = new FlappyAgent.ResolveFeature<EasyPayload<string>, EasyPayload<string>>(i => Task.FromResult(i))
                     })
                 }
            }, null, null, null);
            var result = await agent.CallFeature(callName, callResult) as string;
            if (result == null)
                Assert.Fail("result is null!");
            Assert.That(result, Is.EqualTo(callResult));
        }

        [Test]
        public void AgentFunctionFilterTest()
        {
            var agent = new FlappyAgent(new FlappyAgentConfig
            {
                Features = new IFlappyFeature[]
                {
                     new InvokeFeature<EasyPayload<string>, EasyPayload<string>, FlappyFeatureOption>(new InvokeFeatureDefinition<EasyPayload<string>, EasyPayload<string>>
                     {
                         Name = "test-1",
                         Args = string.Empty,
                         Description = string.Empty,
                         ReturnType = string.Empty,
                         Resolve = new FlappyAgent.ResolveFeature<EasyPayload<string>, EasyPayload<string>>(i => Task.FromResult(i))
                     }),
                     new SynthesizedFeature<EasyPayload<string>,EasyPayload<string>,FlappyFeatureOption>(new SynthesizedFeatureDefinition<EasyPayload<string>, EasyPayload<string>>
                     {
                         Name = "test-2",
                         Args = string.Empty,
                         Description = string.Empty,
                         ReturnType = string.Empty,
                     }),
                     new CodeInterpreterFeature("test"),
                }
            }, null, null, null);
            Assert.Multiple(() =>
            {
                Assert.That(agent.InvokeFeatures().Count(), Is.EqualTo(1));
                Assert.That(agent.SynthesizedFeatures().Count(), Is.EqualTo(1));
                Assert.That(agent.CodeInterpreterFeatures().Count(), Is.EqualTo(1));
            });
        }

        [Test]
        public void BasicTypeOfFeatureDefinition()
        {
            var type = new InvokeFeature<EasyPayload<string>, EasyPayload<string>, FlappyFeatureOption>(new InvokeFeatureDefinition<EasyPayload<string>, EasyPayload<string>>
            {
                Name = "test-1",
                Args = string.Empty,
                Description = string.Empty,
                ReturnType = string.Empty,
                Resolve = new FlappyAgent.ResolveFeature<EasyPayload<string>, EasyPayload<string>>(i => Task.FromResult(i))
            }) as IFlappyFeature;
            var gotName = type.GetType().GetInterface(typeof(IInvokeFeature).FullName)?.FullName;
            Console.WriteLine(gotName);
            Assert.That(gotName, Is.EqualTo(typeof(IInvokeFeature).FullName));
        }
    }
}
