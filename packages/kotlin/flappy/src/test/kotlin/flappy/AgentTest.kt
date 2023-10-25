package flappy

import flappy.llms.Dummy
import org.example.kotlin.getFrontendEngineerResumes
import org.example.kotlin.resumeGetMeta
import kotlin.test.Test
import kotlin.test.assertEquals

class AgentTest {
  private val resumeAgent = FlappyBaseAgent(
    inferenceLLM = Dummy(),
    functions = listOf(resumeGetMeta, getFrontendEngineerResumes),
    maxRetry = 2
  )

  @Test
  fun functionDefinitionString() {
    assertEquals(
      resumeAgent.functionDefinitionString,
      """---
- name: "getMeta"
  description: "Extract meta data from a lawsuit full text."
  parameters:
    properties:
      args:
        description: "Function arguments"
        type: "object"
        properties:
          text:
            description: "Resume full text."
            type: "string"
        required:
        - "text"
      returnType:
        description: "Function return type"
        type: "object"
        properties:
          name:
            type: "string"
          profession:
            type: "string"
          experienceYears:
            type: "int"
          skills:
            type: "array"
            items:
              type: "object"
              properties:
                name:
                  type: "string"
                proficiency:
                  type: "string"
              required:
              - "name"
              - "proficiency"
          projectExperiences:
            type: "array"
            items:
              type: "object"
              properties:
                title:
                  type: "string"
                role:
                  type: "string"
                description:
                  type: "string"
              required:
              - "title"
              - "role"
              - "description"
          education:
            type: "object"
            properties:
              degree:
                type: "string"
              fieldOfStudy:
                type: "string"
              university:
                type: "string"
              year:
                type: "number"
            required:
            - "degree"
            - "fieldOfStudy"
            - "university"
            - "year"
        required:
        - "name"
        - "profession"
        - "experienceYears"
        - "skills"
        - "projectExperiences"
        - "education"
    type: "object"
- name: "getFrontendEngineerResumes"
  description: "Get all frontend engineer resumes."
  parameters:
    properties:
      args:
        description: "Function arguments"
        type: "null"
      returnType:
        description: "Function return type"
        type: "string"
    type: "object"
""".toUniversal()
    )
  }


  @Test
  fun lanOutputSchemaString() {
    assertEquals(
      resumeAgent.lanOutputSchemaString, """{
  "items": {
    "properties": {
      "id": {
        "type": "int",
        "description": "Increment id starting from 1"
      },
      "functionName": {
        "type": "string"
      },
      "args": {
        "description": "an object encapsulating all arguments for a function call. If an argument's value is derived from the return of a previous step, it should be as '%@_' + the ID of the previous step (e.g. '%@_1'). If an argument's value is derived from the **previous** step's function's return value's properties, '.' should be used to access its properties, else just use id with prefix. This approach should remain compatible with the 'args' attribute in the function's JSON schema.",
        "type": "object"
      },
      "thought": {
        "type": "string",
        "description": "The thought why this step is needed."
      }
    },
    "required": [
      "id",
      "functionName",
      "args",
      "thought"
    ],
    "description": "Base step.",
    "type": "object"
  },
  "type": "array",
  "description": "An array storing the steps."
}""".toUniversal()
    )
  }
}
