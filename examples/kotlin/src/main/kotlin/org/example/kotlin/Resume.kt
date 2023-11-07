package org.example.kotlin

import flappy.FlappyBaseAgent
import flappy.FlappyClass
import flappy.annotations.FlappyField
import flappy.features.FlappyInvokeFunction
import flappy.features.FlappySynthesizedFunction
import flappy.llms.ChatGPT
import io.github.cdimascio.dotenv.dotenv

val resumeGetMeta = FlappySynthesizedFunction(
  name = "getMeta",
  description = "Extract meta data from a lawsuit full text.",
  args = ResumeMetaArguments::class.java,
  returnType = ResumeMetaReturn::class.java
)

val getFrontendEngineerResumes = FlappyInvokeFunction(
  name = "getFrontendEngineerResumes",
  description = "Get all frontend engineer resumes.",
  args = FlappyClass.Null.javaClass,
  returnType = String::class.java,
  invoker = { _, _ -> MOCK_RESUME_DATA }
)


val MOCK_RESUME_DATA = """
          I am a seasoned software engineer with over seven years of experience in front-end development. I am passionate about building excellent user interfaces, proficient in HTML, CSS, and JavaScript, and have a deep understanding of front-end frameworks such as React, Vue, and Angular. I have participated in several large-scale projects, responsible for designing and implementing front-end architecture, ensuring high performance and user-friendliness of websites. In addition, I also have project management experience, capable of leading teams to deliver high-quality outputs on time.

          ### Project Experience

          #### 1. E-commerce Website Refactoring (ABC Company)
          - Participated in the refactoring project of an e-commerce website under ABC Company, serving as the front-end technical lead.
          - Rebuilt the website frontend using the React framework, implemented responsive design and dynamic loading features, improving the user experience.
          - Optimized front-end performance, reducing page loading time, and improving the overall website performance.
          - Designed and implemented a system for user behavior tracking and analysis, providing crucial data support for the marketing team.

          #### 2. Social Media Application Development (XYZ Startup)
          - Led a four-person front-end development team, developed a social media application from scratch.
          - Adopted the Vue.js framework and Vuex for state management, implemented real-time chat, post publishing, and user interaction features.
          - Integrated third-party login and sharing features, enhancing user registration and activity.
          - Successfully launched the application to the market, with the user count growing from zero to over 50,000.

          #### 3. Internal Management System Upgrade (DEF Enterprise)
          - Responsible for upgrading the company's internal management system, transitioning from traditional back-end rendering to a modern front-end and back-end separate architecture.
          - Developed a new front-end interface using the Angular framework, realized fast data loading and interactive features.
          - Optimized data communication with the back-end using GraphQL, reducing unnecessary request times, and improving system efficiency.
          - Facilitated the transition of team members to the new technology stack through training and documentation.

          ### Skills and Expertise

          - Front-end technologies: HTML, CSS, JavaScript, React, Vue, Angular, Redux, GraphQL
          - Front-end tools: Webpack, Babel, ESLint
          - Project management: Agile, Scrum, Jira

          ### Education

          - Bachelor's Degree, Computer Science, Peking University, 2012
            """.trimIndent()

const val RESUME_EXECUTE_PLAN_PROMPT = "Find the resume of a frontend engineer and return their metadata."


class ResumeProjectExperiences(
  @FlappyField
  val title: String,

  @FlappyField
  val role: String,

  @FlappyField
  val description: String
)

class ResumeEducation(
  @FlappyField
  val degree: String,

  @FlappyField
  val fieldOfStudy: String,

  @FlappyField
  val university: String,

  @FlappyField
  val year: Number
)

class ResumeSkills(
  @FlappyField
  val name: String,

  @FlappyField
  val proficiency: String
)


class ResumeMetaArguments(
  @FlappyField(description = "Resume full text.")
  val text: String
)


class ResumeMetaReturn(
  @FlappyField
  val name: String,

  @FlappyField
  val profession: String,

  @FlappyField
  val experienceYears: Int,

  @FlappyField
  val skills: List<ResumeSkills>,

  @FlappyField
  val projectExperiences: Array<ResumeProjectExperiences>,

  @FlappyField
  val education: ResumeEducation,
)

suspend fun main(args: Array<String>) {
  val dotenv = dotenv()


  val chatGPT = ChatGPT(
    chatGPTConfig = ChatGPT.ChatGPTConfig(token = dotenv["OPENAI_TOKEN"], host = dotenv["OPENAI_API_BASE"])
  )

  val agent = FlappyBaseAgent(
    inferenceLLM = chatGPT,
    features = listOf(resumeGetMeta, getFrontendEngineerResumes),
    maxRetry = 2
  )

  agent.use {
    it.executePlan<ResumeMetaReturn>(RESUME_EXECUTE_PLAN_PROMPT)
  }
}
