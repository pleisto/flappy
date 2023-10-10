import React from 'react'
import clsx from 'clsx'
import styles from './styles.module.css'
import Translate, { translate } from '@docusaurus/Translate'

type FeatureItem = {
  title: string
  Svg: React.ComponentType<React.ComponentProps<'svg'>>
  description: JSX.Element
}

const FeatureList: FeatureItem[] = [
  {
    title: translate({ message: 'Easy to Use' }),
    Svg: require('@site/static/img/easy-to-use.svg').default,
    description: (
      <Translate id="easy-to-use-desc">
        Empower your projects with the potency of LLM-based agents via Flappy. Designed to be as straightforward as CRUD
        application development, it requires no prior AI experience. Unleash a new world of possibilities, where
        simplicity meets sophistication, all at your fingertips.
      </Translate>
    )
  },
  {
    title: translate({ message: 'Production-Ready' }),
    Svg: require('@site/static/img/production-ready.svg').default,
    description: (
      <Translate id="production-ready-desc">
        Beyond just research: discover a production-ready SDK meticulously designed with developers in mind. It expertly
        balances cost-efficiency with sandbox security, offering unparalleled stability for demanding commercial
        environments. Dive into a realm where innovation meets practicality, tailored to empower developers and
        accelerate your project's success.
      </Translate>
    )
  },
  {
    title: translate({ message: 'Programming Language-Agnostic' }),
    Svg: require('@site/static/img/language-agnostic.svg').default,
    description: (
      <Translate id="programming-language-agnostic-desc">
        Experience the immense potential of AI in your language of choice with Flappy. Engineered for universal
        compatibility, Flappy integrates seamlessly with any programming language. Utilize Python only when your
        application explicitly requires it. Welcome to the new era of coding, where language is no longer a barrier to
        your AI ambitions.
      </Translate>
    )
  }
]

function Feature({ title, Svg, description }: FeatureItem) {
  return (
    <div className={clsx('col col--4')}>
      <div className="margin-horiz-md">
        <Svg className={styles.featureSvg} role="img" />
      </div>
      <div className="margin-horiz-md">
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
    </div>
  )
}

export default function HomepageFeatures(): JSX.Element {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  )
}
