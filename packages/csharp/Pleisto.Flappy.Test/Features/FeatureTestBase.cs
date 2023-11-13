using NUnit.Framework;
using Pleisto.Flappy.Interfaces;

namespace Pleisto.Flappy.Tests.Features
{
    public abstract class FeatureTestBase<TFeature, TFeatureDefine>
           where TFeature : class, IFlappyFeature
           where TFeatureDefine : IFlappyFeatureDefinition, new()

    {
        protected class FeatureGenerated
        {
            public TFeature Feature { get; set; }
            public TFeatureDefine Define { get; set; }
        }

        protected virtual FeatureGenerated CreateFeature()
        {
            var define = new TFeatureDefine()
            {
                Name = "Test",
                Description = "test-description",
            };
            return new FeatureGenerated
            {
                Feature = Activator.CreateInstance(typeof(TFeature), new object[] { define }) as TFeature,
                Define = define
            };
        }

        /// <summary>
        /// Test map from feature to define
        /// </summary>
        [Test]
        public void DefineMapper()
        {
            var feature = CreateFeature();
            Assert.Multiple(() =>
            {
                Assert.That(feature.Feature.Name, Is.EqualTo(feature.Define.Name));
                Assert.That(feature.Define, Is.EqualTo(feature.Feature.Define));
            });
        }
    }
}
