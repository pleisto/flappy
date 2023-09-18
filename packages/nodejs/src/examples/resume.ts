import { createFlappyAgent, createInvokeFunction, createSynthesizedFunction } from '..'
import { z } from 'zod'
import OpenAI from 'openai'
import { ChatGPT } from '../llm/chatgpt'

const gpt35 = new ChatGPT(
  new OpenAI({
    apiKey: process.env.OPENAI_API_KEY!,
    baseURL: process.env.OPENAI_API_BASE!
  }),
  'gpt-3.5-turbo'
)

const MOCK_RESUME_DATA = `
我是一名资深的软件工程师，拥有超过七年的前端开发经验。我热衷于构建出色的用户界面，熟练运用HTML、CSS和JavaScript，并精通React、Vue以及Angular等前端框架。我曾参与过多个大型项目，负责设计和实现前端架构，确保网站的高性能和用户友好性。此外，我还具备项目管理的经验，能够带领团队按时交付高质量的成果。

### 项目经历

#### 1. 电商网站重构 (ABC 公司)
- 负责参与了ABC公司旗下电商网站的重构项目，担任前端技术负责人。
- 使用React框架重建网站前端，实现了页面响应式设计和动态加载功能，提升了用户体验。
- 优化前端性能，减少了页面加载时间，提高了网站整体性能。
- 设计并实施了用户行为跟踪和分析系统，为市场营销团队提供了关键的数据支持。

#### 2. 社交媒体应用开发 (XYZ 创业公司)
- 领导一个四人的前端开发团队，从零开始开发了一款社交媒体应用。
- 采用了Vue.js框架和Vuex进行状态管理，实现了实时聊天、帖子发布和用户互动功能。
- 集成了第三方登录和分享功能，提升了用户注册和活跃度。
- 成功将应用推向市场，用户数量从零增长到五万以上。

#### 3. 内部管理系统升级 (DEF 企业)
- 负责升级公司内部管理系统，从传统的后端渲染转变为现代化的前后端分离架构。
- 使用Angular框架开发新的前端界面，实现了快速的数据加载和交互功能。
- 利用GraphQL优化了与后端的数据通信，减少了不必要的请求次数，提高了系统效率。
- 通过培训和文档编写，帮助团队成员顺利过渡到新的技术栈。

### 技能和专业知识

- 前端技术: HTML, CSS, JavaScript, React, Vue, Angular, Redux, GraphQL
- 前端工具: Webpack, Babel, ESLint
- 项目管理: Agile, Scrum, Jira

### 教育背景

- 学士学位，计算机科学，北京大学，2012年
`

const resumeAgent = createFlappyAgent({
  llm: gpt35,
  functions: [
    createSynthesizedFunction({
      name: 'getMeta',
      description: 'Extract meta data from a resume full text.',
      args: z.object({
        lawsuit: z.string().describe('Resume full text.')
      }),
      returnType: z.object({
        name: z.string(),
        profession: z.string(),
        experienceYears: z.number(),
        skills: z.array(
          z.object({
            name: z.string(),
            proficiency: z.string()
          })
        ),
        projectExperiences: z.array(
          z.object({
            title: z.string(),
            role: z.string(),
            description: z.string()
          })
        ),
        education: z.object({
          degree: z.string(),
          fieldOfStudy: z.string(),
          university: z.string(),
          year: z.number()
        })
      })
    }),
    createInvokeFunction({
      name: 'getFrontendEngineerResumes',
      description: 'Get all frontend engineer resumes.',
      args: z.null(),
      returnType: z.array(z.string()),
      resolve: async (): Promise<string[]> => {
        // Do something
        // e.g. query SQL database
        console.debug('getFrontendEngineerResumes called')
        return [MOCK_RESUME_DATA]
      }
    })
  ]
})

void resumeAgent.createExecutePlan('找到前端工程师的简历')
