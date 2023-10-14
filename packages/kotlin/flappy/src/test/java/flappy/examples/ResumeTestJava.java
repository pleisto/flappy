package flappy.examples;

import flappy.FlappyBaseAgent;
import flappy.LLMResponse;
import flappy.llms.Dummy;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
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
            "[\n" +
              "   {\n" +
              "     \"id\": 1,\n" +
              "     \"functionName\": \"getFrontendEngineerResumes\",\n" +
              "     \"args\": {},\n" +
              "     \"thought\": \"We need to retrieve all the frontend engineer resumes.\"\n" +
              "   },\n" +
              "   {\n" +
              "     \"id\": 2,\n" +
              "     \"functionName\": \"getMeta\",\n" +
              "     \"args\": {\n" +
              "       \"text\": \"%@_1\"\n" +
              "     },\n" +
              "     \"thought\": \"We can extract the metadata from each resume.\"\n" +
              "   }\n" +
              " ]\n");
      }

      if (source.equals(resumeGetMeta.getSource())) {
        return new LLMResponse.Success(
          ("\n" +
            "{\n" +
            "  \"name\": \"John Doe\",\n" +
            "  \"profession\": \"Software Engineer\",\n" +
            "  \"experienceYears\": 7,\n" +
            "  \"skills\": [\n" +
            "    {\n" +
            "      \"name\": \"HTML\",\n" +
            "      \"proficiency\": \"Expert\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"CSS\",\n" +
            "      \"proficiency\": \"Expert\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"JavaScript\",\n" +
            "      \"proficiency\": \"Expert\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"React\",\n" +
            "      \"proficiency\": \"Expert\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Vue\",\n" +
            "      \"proficiency\": \"Expert\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Angular\",\n" +
            "      \"proficiency\": \"Expert\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Redux\",\n" +
            "      \"proficiency\": \"Expert\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"GraphQL\",\n" +
            "      \"proficiency\": \"Expert\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Webpack\",\n" +
            "      \"proficiency\": \"Expert\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Babel\",\n" +
            "      \"proficiency\": \"Expert\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"ESLint\",\n" +
            "      \"proficiency\": \"Expert\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Agile\",\n" +
            "      \"proficiency\": \"Expert\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Scrum\",\n" +
            "      \"proficiency\": \"Expert\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Jira\",\n" +
            "      \"proficiency\": \"Expert\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"projectExperiences\": [\n" +
            "    {\n" +
            "      \"title\": \"E-commerce Website Refactoring (ABC Company)\",\n" +
            "      \"role\": \"Front-end Technical Lead\",\n" +
            "      \"description\": \"Participated in the refactoring project of an e-commerce website under ABC Company, rebuilt the website frontend using the React framework, implemented responsive design and dynamic loading features, optimized front-end performance, and designed and implemented a system for user behavior tracking and analysis.\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"title\": \"Social Media Application Development (XYZ Startup)\",\n" +
            "      \"role\": \"Team Lead\",\n" +
            "      \"description\": \"Led a four-person front-end development team, developed a social media application from scratch using the Vue.js framework and Vuex for state management, integrated third-party login and sharing features, and successfully launched the application to the market.\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"title\": \"Internal Management System Upgrade (DEF Enterprise)\",\n" +
            "      \"role\": \"Front-end Developer\",\n" +
            "      \"description\": \"Responsible for upgrading the company's internal management system, developed a new front-end interface using the Angular framework, optimized data communication with the back-end using GraphQL, and facilitated the transition of team members to the new technology stack through training and documentation.\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"education\": {\n" +
            "    \"degree\": \"Bachelor's Degree\",\n" +
            "    \"fieldOfStudy\": \"Computer Science\",\n" +
            "    \"university\": \"Peking University\",\n" +
            "    \"year\": 2012\n" +
            "  }\n" +
            "}\n").trim());
      }
      return new LLMResponse.Success("");
    }));

    FlappyBaseAgent resumeAgent = new FlappyBaseAgent(
      dummy, Arrays.asList(resumeGetMeta, getFrontendEngineerResumes)
    );

    Future<ResumeMetaReturn> returnFuture = resumeAgent.executePlanAsync(RESUME_EXECUTE_PLAN_PROMPT);

    ResumeMetaReturn ret = returnFuture.get();

    assertEquals(ret.getEducation().getYear(), 2012);
  }

}
