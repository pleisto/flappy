package org.example.java;


import flappy.FlappyBaseAgent;
import flappy.FlappyFunctionBase;
import flappy.annotations.FlappyField;
import flappy.functions.FlappyInvokeFunction;
import flappy.functions.FlappySynthesizedFunction;
import flappy.llms.ChatGPT;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Resume {
  public static final String RESUME_EXECUTE_PLAN_PROMPT = "找到前端工程师的简历并返回他的元数据";
  static final String MOCK_RESUME_DATA = """
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
    """;

  public static FlappyFunctionBase<?, ?> resumeGetMeta = new FlappySynthesizedFunction(
    "getMeta",
    "Extract meta data from a lawsuit full text.",
    ResumeMetaArguments.class,
    ResumeMetaReturn.class
  );

  public static FlappyFunctionBase<?, ?> getFrontendEngineerResumes = new FlappyInvokeFunction(
    "getFrontendEngineerResumes",
    "Get all frontend engineer resumes.",
    ResumeMetaArguments.class,
    String.class,
    (a, agent, $completion) -> MOCK_RESUME_DATA
  );

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    Dotenv dotenv = Dotenv.load();
    ChatGPT llm = new ChatGPT("gpt-3.5-turbo", new ChatGPT.ChatGPTConfig(dotenv.get("OPENAI_TOKEN"), dotenv.get("OPENAI_API_BASE")));

    FlappyBaseAgent resumeAgent = new FlappyBaseAgent(
      llm, List.of(resumeGetMeta, getFrontendEngineerResumes)
    );


    Future<ResumeMetaReturn> future = resumeAgent.executePlanAsync(RESUME_EXECUTE_PLAN_PROMPT);
    ResumeMetaReturn ret = future.get();

    System.out.println("################# RESULT ################");
    System.out.println(ret.name);
    System.out.println("################# RESULT ################");
  }

  static class ResumeProjectExperiences {
    @FlappyField
    String title;

    @FlappyField
    String role;

    @FlappyField
    String description;

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getRole() {
      return role;
    }

    public void setRole(String role) {
      this.role = role;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }
  }

  public static class ResumeEducation {
    @FlappyField
    String degree;

    @FlappyField
    String fieldOfStudy;

    @FlappyField
    String university;

    @FlappyField
    Number year;

    public String getDegree() {
      return degree;
    }

    public void setDegree(String degree) {
      this.degree = degree;
    }

    public String getFieldOfStudy() {
      return fieldOfStudy;
    }

    public void setFieldOfStudy(String fieldOfStudy) {
      this.fieldOfStudy = fieldOfStudy;
    }

    public String getUniversity() {
      return university;
    }

    public void setUniversity(String university) {
      this.university = university;
    }

    public Number getYear() {
      return year;
    }

    public void setYear(Number year) {
      this.year = year;
    }
  }

  public static class ResumeSkills {
    @FlappyField
    String name;

    @FlappyField
    String proficiency;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getProficiency() {
      return proficiency;
    }

    public void setProficiency(String proficiency) {
      this.proficiency = proficiency;
    }
  }

  public static class ResumeMetaArguments {

    @FlappyField(description = "Resume full text.")
    String text;

    public String getText() {
      return text;
    }

    public void setText(String text) {
      this.text = text;
    }
  }

  public static class ResumeMetaReturn {
    @FlappyField
    String name;

    @FlappyField
    String profession;

    @FlappyField
    Integer experienceYears;


    @FlappyField
    List<ResumeSkills> skills;

    @FlappyField
    List<ResumeProjectExperiences> projectExperiences;

    @FlappyField
    ResumeEducation education;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getProfession() {
      return profession;
    }

    public void setProfession(String profession) {
      this.profession = profession;
    }

    public Integer getExperienceYears() {
      return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
      this.experienceYears = experienceYears;
    }

    public List<ResumeSkills> getSkills() {
      return skills;
    }

    public void setSkills(List<ResumeSkills> skills) {
      this.skills = skills;
    }

    public List<ResumeProjectExperiences> getProjectExperiences() {
      return projectExperiences;
    }

    public void setProjectExperiences(List<ResumeProjectExperiences> projectExperiences) {
      this.projectExperiences = projectExperiences;
    }

    public ResumeEducation getEducation() {
      return education;
    }

    public void setEducation(ResumeEducation education) {
      this.education = education;
    }
  }


}
