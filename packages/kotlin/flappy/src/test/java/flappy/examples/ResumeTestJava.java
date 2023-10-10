package flappy.examples;

import flappy.FlappyBaseAgent;
import flappy.LLMResponse;
import flappy.llms.Dummy;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.example.java.Resume.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResumeTestJava {
  @Test
  public void resume() throws ExecutionException, InterruptedException {
    Dummy dummy = new Dummy(((_message, source, _cfg) -> {
      switch (source) {
        case FlappyBaseAgent.AGENT_SOURCE:
          return new LLMResponse.Success(
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
              """);
      }

      if (source.equals(resumeGetMeta.getSource())) {
        return new LLMResponse.Success(
          """

            {
              "name": "John Doe",
              "profession": "Software Engineer",
              "experienceYears": 7,
              "skills": [
                {
                  "name": "HTML",
                  "proficiency": "Expert"
                },
                {
                  "name": "CSS",
                  "proficiency": "Expert"
                },
                {
                  "name": "JavaScript",
                  "proficiency": "Expert"
                },
                {
                  "name": "React",
                  "proficiency": "Expert"
                },
                {
                  "name": "Vue",
                  "proficiency": "Expert"
                },
                {
                  "name": "Angular",
                  "proficiency": "Expert"
                },
                {
                  "name": "Redux",
                  "proficiency": "Expert"
                },
                {
                  "name": "GraphQL",
                  "proficiency": "Expert"
                },
                {
                  "name": "Webpack",
                  "proficiency": "Expert"
                },
                {
                  "name": "Babel",
                  "proficiency": "Expert"
                },
                {
                  "name": "ESLint",
                  "proficiency": "Expert"
                },
                {
                  "name": "Agile",
                  "proficiency": "Expert"
                },
                {
                  "name": "Scrum",
                  "proficiency": "Expert"
                },
                {
                  "name": "Jira",
                  "proficiency": "Expert"
                }
              ],
              "projectExperiences": [
                {
                  "title": "E-commerce Website Refactoring (ABC Company)",
                  "role": "Front-end Technical Lead",
                  "description": "Participated in the refactoring project of an e-commerce website under ABC Company, rebuilt the website frontend using the React framework, implemented responsive design and dynamic loading features, optimized front-end performance, and designed and implemented a system for user behavior tracking and analysis."
                },
                {
                  "title": "Social Media Application Development (XYZ Startup)",
                  "role": "Team Lead",
                  "description": "Led a four-person front-end development team, developed a social media application from scratch using the Vue.js framework and Vuex for state management, integrated third-party login and sharing features, and successfully launched the application to the market."
                },
                {
                  "title": "Internal Management System Upgrade (DEF Enterprise)",
                  "role": "Front-end Developer",
                  "description": "Responsible for upgrading the company's internal management system, developed a new front-end interface using the Angular framework, optimized data communication with the back-end using GraphQL, and facilitated the transition of team members to the new technology stack through training and documentation."
                }
              ],
              "education": {
                "degree": "Bachelor's Degree",
                "fieldOfStudy": "Computer Science",
                "university": "Peking University",
                "year": 2012
              }
            }
                    """.trim());
      }
      return new LLMResponse.Success("");
    }));

    FlappyBaseAgent resumeAgent = new FlappyBaseAgent(
      dummy, List.of(resumeGetMeta, getFrontendEngineerResumes)
    );

    Future<ResumeMetaReturn> returnFuture = resumeAgent.executePlanAsync(RESUME_EXECUTE_PLAN_PROMPT);

    ResumeMetaReturn ret = returnFuture.get();

    assertEquals(ret.getEducation().getYear(), 2012);
  }

}
