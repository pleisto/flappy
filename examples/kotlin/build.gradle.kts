plugins {
  // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
  id("org.jetbrains.kotlin.jvm") version "1.9.10"

  // Apply the java-library plugin for API and implementation separation.
  `java-library`
}

group = "org.example"
version = "unspecified"

repositories {
  mavenCentral()
}

dependencies {
  implementation(project(mapOf("path" to ":flappy")))
  implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
  implementation("com.aallam.openai:openai-client:3.4.1")
  testImplementation(platform("org.junit:junit-bom:5.9.1"))
  testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
  useJUnitPlatform()
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(19))
  }
}
