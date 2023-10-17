// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

const lightCodeTheme = require('prism-react-renderer/themes/github')
const darkCodeTheme = require('prism-react-renderer/themes/dracula')
const githubUrl = 'https://github.com/pleisto/flappy'

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'Flappy',
  favicon: 'img/favicon.ico',

  // Set the production url of your site here
  url: 'https://flappy.pleisto.com',
  // Set the /<baseUrl>/ pathname under which your site is served
  // For GitHub pages deployment, it is often '/<projectName>/'
  baseUrl: '/',

  // GitHub pages deployment config.
  // If you aren't using GitHub pages, you don't need these.
  organizationName: 'pleisto', // Usually your GitHub org/user name.
  projectName: 'flappy', // Usually your repo name.

  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',

  // Even if you don't use internalization, you can use this field to set useful
  // metadata like html lang. For example, if your site is Chinese, you may want
  // to replace "en" with "zh-Hans".
  i18n: {
    defaultLocale: 'en',
    locales: ['en', 'ja', 'zh-Hans']
  },

  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          sidebarPath: require.resolve('./sidebars.js'),
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          editUrl: 'https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/'
        },
        blog: false,
        theme: {
          customCss: require.resolve('./src/css/theme.css')
        }
      })
    ]
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      // Replace with your project's social card
      image: 'img/docusaurus-social-card.jpg',
      navbar: {
        title: 'Flappy',
        logo: {
          alt: 'Flappy',
          src: 'img/logo.svg'
        },
        items: [
          {
            type: 'docSidebar',
            sidebarId: 'docSidebar',
            position: 'left',
            label: 'Docs'
          },
          {
            href: githubUrl,
            label: 'GitHub',
            position: 'right'
          }
          // ,
          // {
          //   type: 'localeDropdown',
          //   position: 'right'
          // }
        ]
      },
      footer: {
        style: 'light',
        links: [
          {
            title: 'Docs',
            items: [
              {
                label: 'Quick Start',
                to: '/docs/quick-start'
              }
            ]
          },
          {
            title: 'Community',
            items: [
              {
                label: 'Discord',
                href: 'https://discord.gg/aJXJECdn'
              },
              {
                label: 'Twitter',
                href: 'https://twitter.com/pleistoHQ'
              }
            ]
          },
          {
            title: 'More',
            items: [
              {
                label: 'GitHub',
                href: githubUrl
              }
            ]
          }
        ],
        copyright: `Copyright © ${new Date().getFullYear()} Pleisto, Inc. Built with Docusaurus.`
      },
      prism: {
        theme: lightCodeTheme,
        darkTheme: darkCodeTheme,
        additionalLanguages: ['java', 'kotlin', 'csharp']
      }
    })
}

module.exports = config
