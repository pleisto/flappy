package org.example.java;


import flappy.FlappyBaseAgent;
import flappy.FlappyFunctionBase;
import flappy.annotations.FlappyField;
import flappy.functions.FlappyInvokeFunction;
import flappy.functions.FlappySynthesizedFunction;
import flappy.llms.ChatGPT;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Resume {
  public static final String RESUME_EXECUTE_PLAN_PROMPT = "Find the resume of a frontend engineer and return their metadata.";
  static final String MOCK_RESUME_DATA = "I am a seasoned software engineer with over seven years of experience in front-end development. I am passionate about building excellent user interfaces, proficient in HTML, CSS, and JavaScript, and have a deep understanding of front-end frameworks such as React, Vue, and Angular. I have participated in several large-scale projects, responsible for designing and implementing front-end architecture, ensuring high performance and user-friendliness of websites. In addition, I also have project management experience, capable of leading teams to deliver high-quality outputs on time.\n" +
                                         "\n" +
                                         "### Project Experience\n" +
                                         "\n" +
                                         "#### 1. E-commerce Website Refactoring (ABC Company)\n" +
                                         "- Participated in the refactoring project of an e-commerce website under ABC Company, serving as the front-end technical lead.\n" +
                                         "- Rebuilt the website frontend using the React framework, implemented responsive design and dynamic loading features, improving the user experience.\n" +
                                         "- Optimized front-end performance, reducing page loading time, and improving the overall website performance.\n" +
                                         "- Designed and implemented a system for user behavior tracking and analysis, providing crucial data support for the marketing team.\n" +
                                         "\n" +
                                         "#### 2. Social Media Application Development (XYZ Startup)\n" +
                                         "- Led a four-person front-end development team, developed a social media application from scratch.\n" +
                                         "- Adopted the Vue.js framework and Vuex for state management, implemented real-time chat, post publishing, and user interaction features.\n" +
                                         "- Integrated third-party login and sharing features, enhancing user registration and activity.\n" +
                                         "- Successfully launched the application to the market, with the user count growing from zero to over 50,000.\n" +
                                         "\n" +
                                         "#### 3. Internal Management System Upgrade (DEF Enterprise)\n" +
                                         "- Responsible for upgrading the company's internal management system, transitioning from traditional back-end rendering to a modern front-end and back-end separate architecture.\n" +
                                         "- Developed a new front-end interface using the Angular framework, realized fast data loading and interactive features.\n" +
                                         "- Optimized data communication with the back-end using GraphQL, reducing unnecessary request times, and improving system efficiency.\n" +
                                         "- Facilitated the transition of team members to the new technology stack through training and documentation.\n" +
                                         "\n" +
                                         "### Skills and Expertise\n" +
                                         "\n" +
                                         "- Front-end technologies: HTML, CSS, JavaScript, React, Vue, Angular, Redux, GraphQL\n" +
                                         "- Front-end tools: Webpack, Babel, ESLint\n" +
                                         "- Project management: Agile, Scrum, Jira\n" +
                                         "\n" +
                                         "### Education\n" +
                                         "\n" +
                                         "- Bachelor's Degree, Computer Science, Peking University, 2012\n";

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
    ChatGPT llm = new ChatGPT(new ChatGPT.ChatGPTConfig(null, dotenv.get("OPENAI_TOKEN"), dotenv.get("OPENAI_API_BASE")));

    FlappyBaseAgent resumeAgent = new FlappyBaseAgent(
      llm, Arrays.asList(resumeGetMeta, getFrontendEngineerResumes)
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
