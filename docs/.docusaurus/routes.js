import React from 'react';
import ComponentCreator from '@docusaurus/ComponentCreator';

export default [
  {
    path: '/__docusaurus/debug',
    component: ComponentCreator('/__docusaurus/debug', '622'),
    exact: true
  },
  {
    path: '/__docusaurus/debug/config',
    component: ComponentCreator('/__docusaurus/debug/config', '8c5'),
    exact: true
  },
  {
    path: '/__docusaurus/debug/content',
    component: ComponentCreator('/__docusaurus/debug/content', 'c8e'),
    exact: true
  },
  {
    path: '/__docusaurus/debug/globalData',
    component: ComponentCreator('/__docusaurus/debug/globalData', '869'),
    exact: true
  },
  {
    path: '/__docusaurus/debug/metadata',
    component: ComponentCreator('/__docusaurus/debug/metadata', '3a3'),
    exact: true
  },
  {
    path: '/__docusaurus/debug/registry',
    component: ComponentCreator('/__docusaurus/debug/registry', '7c7'),
    exact: true
  },
  {
    path: '/__docusaurus/debug/routes',
    component: ComponentCreator('/__docusaurus/debug/routes', 'c99'),
    exact: true
  },
  {
    path: '/markdown-page',
    component: ComponentCreator('/markdown-page', '094'),
    exact: true
  },
  {
    path: '/docs',
    component: ComponentCreator('/docs', '90f'),
    routes: [
      {
        path: '/docs/',
        component: ComponentCreator('/docs/', '2cb'),
        exact: true,
        sidebar: "docSidebar"
      },
      {
        path: '/docs/code-interpreter',
        component: ComponentCreator('/docs/code-interpreter', 'f58'),
        exact: true,
        sidebar: "docSidebar"
      },
      {
        path: '/docs/examples',
        component: ComponentCreator('/docs/examples', '665'),
        exact: true,
        sidebar: "docSidebar"
      },
      {
        path: '/docs/invoke-function',
        component: ComponentCreator('/docs/invoke-function', 'ade'),
        exact: true,
        sidebar: "docSidebar"
      },
      {
        path: '/docs/quick-start',
        component: ComponentCreator('/docs/quick-start', 'cd5'),
        exact: true,
        sidebar: "docSidebar"
      },
      {
        path: '/docs/sythesized-function',
        component: ComponentCreator('/docs/sythesized-function', 'a87'),
        exact: true,
        sidebar: "docSidebar"
      }
    ]
  },
  {
    path: '/',
    component: ComponentCreator('/', '707'),
    exact: true
  },
  {
    path: '*',
    component: ComponentCreator('*'),
  },
];
