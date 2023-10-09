package flappy.examples

import flappy.FlappyBaseAgent
import flappy.LLMResponse
import flappy.llms.Dummy
import kotlinx.coroutines.runBlocking
import org.example.kotlin.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

val resumeDummy = Dummy { _, source, _ ->
  when (source) {
    FlappyBaseAgent.AGENT_SOURCE -> LLMResponse.Success(
      """
        [
          {
            "id": 1,
            "functionName": "getFrontendEngineerResumes",
            "args": {},
            "thought": "We need to retrieve all the frontend engineer resumes."
          },
          {
            "id": 2,
            "functionName": "getMeta",
            "args": {
              "text": "%@_1"
            },
            "thought": "We can extract the metadata from each resume."
          }
        ]
        """.trimIndent()
    )

    lawGetMeta.source -> LLMResponse.Success(
      """
          {
            "name": "姓名",
            "profession": "软件工程师",
            "experienceYears": 7,
            "skills": [
              {
                "name": "HTML",
                "proficiency": "熟练"
              },
              {
                "name": "CSS",
                "proficiency": "熟练"
              },
              {
                "name": "JavaScript",
                "proficiency": "熟练"
              },
              {
                "name": "React",
                "proficiency": "精通"
              },
              {
                "name": "Vue",
                "proficiency": "精通"
              },
              {
                "name": "Angular",
                "proficiency": "精通"
              }
            ],
            "projectExperiences": [
              {
                "title": "电商网站重构",
                "role": "前端技术负责人",
                "description": "负责参与了ABC公司旗下电商网站的重构项目，担任前端技术负责人。使用React框架重建网站前端，实现了页面响应式设计和动态加载功能，提升了用户体验。优化前端性能，减少了页面加载时间，提高了网站整体性能。设计并实施了用户行为跟踪和分析系统，为市场营销团队提供了关键的数据支持。"
              },
              {
                "title": "社交媒体应用开发",
                "role": "前端开发团队领导",
                "description": "领导一个四人的前端开发团队，从零开始开发了一款社交媒体应用。采用了Vue.js框架和Vuex进行状态管理，实现了实时聊天、帖子发布和用户互动功能。集成了第三方登录和分享功能，提升了用户注册和活跃度。成功将应用推向市场，用户数量从零增长到五万以上。"
              },
              {
                "title": "内部管理系统升级",
                "role": "系统升级负责人",
                "description": "负责升级公司内部管理系统，从传统的后端渲染转变为现代化的前后端分离架构。使用Angular框架开发新的前端界面，实现了快速的数据加载和交互功能。利用GraphQL优化了与后端的数据通信，减少了不必要的请求次数，提高了系统效率。通过培训和文档编写，帮助团队成员顺利过渡到新的技术栈。"
              }
            ],
            "education": {
              "degree": "学士学位",
              "fieldOfStudy": "计算机科学",
              "university": "北京大学",
              "year": "2012"
            }
          }
        """.trimIndent()
    )

    else -> LLMResponse.Success("")
  }
}

class ResumeTest {
  @Test
  fun resume() {

    val resumeAgent = FlappyBaseAgent(
      maxRetry = 2,
      inferenceLLM = resumeDummy,
      functions = listOf(resumeGetMeta, getFrontendEngineerResumes)
    )

    runBlocking {
      val ret =
        resumeAgent.executePlan<ResumeMetaReturn>(RESUME_EXECUTE_PLAN_PROMPT)

      assertEquals(ret.education.year, 2012)
    }
  }
}
