package flappy

import flappy.llms.Dummy
import org.example.kotlin.getFrontendEngineerResumes
import org.example.kotlin.resumeGetMeta
import kotlin.test.Test
import kotlin.test.assertEquals

class AgentTest {
  private val resumeAgent = FlappyBaseAgent(
    inferenceLLM = Dummy(),
    features = listOf(resumeGetMeta, getFrontendEngineerResumes),
    maxRetry = 2
  )

  @Test
  fun prompt() {
    assertEquals(
      resumeAgent.buildExecuteMessage("prompt").asJSON(),
      """[{"content":"You are an AI assistant that makes step-by-step plans to solve problems, utilizing external functions. Each step entails one plan followed by a function-call, which will later be executed to gather args for that step.\nMake as few plans as possible if it can solve the problem.\nThe functions list is described using the following YAML schema array:\n---\n- name: \"getMeta\"\n  description: \"Extract meta data from a lawsuit full text.\"\n  parameters:\n    properties:\n      args:\n        description: \"Function arguments\"\n        type: \"object\"\n        properties:\n          text:\n            description: \"Resume full text.\"\n            type: \"string\"\n        required:\n        - \"text\"\n      returnType:\n        description: \"Function return type\"\n        type: \"object\"\n        properties:\n          name:\n            type: \"string\"\n          profession:\n            type: \"string\"\n          experienceYears:\n            type: \"int\"\n          skills:\n            type: \"array\"\n            items:\n              type: \"object\"\n              properties:\n                name:\n                  type: \"string\"\n                proficiency:\n                  type: \"string\"\n              required:\n              - \"name\"\n              - \"proficiency\"\n          projectExperiences:\n            type: \"array\"\n            items:\n              type: \"object\"\n              properties:\n                title:\n                  type: \"string\"\n                role:\n                  type: \"string\"\n                description:\n                  type: \"string\"\n              required:\n              - \"title\"\n              - \"role\"\n              - \"description\"\n          education:\n            type: \"object\"\n            properties:\n              degree:\n                type: \"string\"\n              fieldOfStudy:\n                type: \"string\"\n              university:\n                type: \"string\"\n              year:\n                type: \"number\"\n            required:\n            - \"degree\"\n            - \"fieldOfStudy\"\n            - \"university\"\n            - \"year\"\n        required:\n        - \"name\"\n        - \"profession\"\n        - \"experienceYears\"\n        - \"skills\"\n        - \"projectExperiences\"\n        - \"education\"\n    type: \"object\"\n- name: \"getFrontendEngineerResumes\"\n  description: \"Get all frontend engineer resumes.\"\n  parameters:\n    properties:\n      args:\n        description: \"Function arguments\"\n        type: \"null\"\n      returnType:\n        description: \"Function return type\"\n        type: \"string\"\n    type: \"object\"\n\n\nYour specified plans should be output as JSON object array and adhere to the following JSON schema:\n{\n  \"items\": {\n    \"properties\": {\n      \"id\": {\n        \"type\": \"int\",\n        \"description\": \"Increment id starting from 1\"\n      },\n      \"functionName\": {\n        \"type\": \"string\"\n      },\n      \"args\": {\n        \"description\": \"an object encapsulating all arguments for a function call. If an argument's value is derived from the return of a previous step, it should be as '%@_' + the ID of the previous step (e.g. '%@_1'). If an argument's value is derived from the **previous** step's function's return value's properties, '.' should be used to access its properties, else just use id with prefix. This approach should remain compatible with the 'args' attribute in the function's JSON schema.\",\n        \"type\": \"object\"\n      },\n      \"thought\": {\n        \"type\": \"string\",\n        \"description\": \"The thought why this step is needed.\"\n      }\n    },\n    \"required\": [\n      \"id\",\n      \"functionName\",\n      \"args\",\n      \"thought\"\n    ],\n    \"description\": \"Base step.\",\n    \"type\": \"object\"\n  },\n  \"type\": \"array\",\n  \"description\": \"An array storing the steps.\"\n}\n\nOnly the listed functions are allowed to be used.","role":"SYSTEM"},{"content":"Prompt: prompt\n\nPlan array:","role":"USER"}]"""
    )
  }

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
"""
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
}"""
    )
  }
}
