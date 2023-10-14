import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.dokka.DokkaConfiguration.Visibility
import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URL

/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin library project to get you started.
 * For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.3/userguide/building_java_projects.html in the Gradle documentation.
 */

plugins {
  // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
  id("org.jetbrains.kotlin.jvm") version "1.9.10"
  id("org.jetbrains.dokka") version "1.9.0"

  // Apply the java-library plugin for API and implementation separation.
  `java-library`

  id("com.vanniktech.maven.publish") version "0.25.3"

  id("com.google.osdetector") version "1.7.3"

  signing
}

repositories {
  // Use Maven Central for resolving dependencies.
  mavenCentral()
}

publishing {
  repositories {
    // mavenCentral()
    maven {
      name = "GitHubPackages"
      url = uri("https://maven.pkg.github.com/pleisto/flappy")
      credentials {
        username = System.getenv("ORG_GRADLE_PROJECT_GithubPackagesUsername")
        password = System.getenv("ORG_GRADLE_PROJECT_GithubPackagesPassword")
      }
    }
  }
}

dependencies {
  // Use the Kotlin JUnit 5 integration.
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

  // Use the JUnit 5 integration.
  testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.0")

  testImplementation(kotlin("test"))
  testImplementation(project(mapOf("path" to ":kotlin-example")))
  testImplementation(project(mapOf("path" to ":java-example")))

  testRuntimeOnly("org.junit.platform:junit-platform-launcher")

  implementation(kotlin("reflect"))
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.3")

  implementation("io.ktor:ktor-client-java-jvm:2.3.5")
  implementation("io.ktor:ktor-client-core:2.3.5")
  implementation("io.ktor:ktor-client-okhttp:2.3.5")
  implementation("com.aallam.openai:openai-client:3.5.0")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.3")
  implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
  implementation("com.pleisto:flappy-java-bindings:0.0.4")
  implementation("com.pleisto:flappy-java-bindings:0.0.4:${osdetector.classifier}")
}

mavenPublishing {
  publishToMavenCentral(SonatypeHost.S01, true)
  signAllPublications()

//  coordinates("com.pleisto", "flappy", "0.0.10")

  pom {
    name.set("flappy")
    inceptionYear.set("2023")
    description.set("A library developed to streamline the creation of AI applications and agents that use Large Language Models (LLMs).")
    url.set("https://github.com/pleisto/flappy")
    licenses {
      license {
        name.set("The Apache License, Version 2.0")
        url.set("https://raw.githubusercontent.com/pleisto/flappy/main/LICENSE")
      }
    }

    developers {
      developer {
        id.set("clszzyh")
        name.set("Yuhang Shi")
        url.set("https://github.com/clszzyh")
        organization.set("pleisto")
        organizationUrl.set("https://github.com/pleisto")
      }
    }

    scm {
      url.set("https://github.com/pleisto/flappy")
      connection.set("scm:git:https://git@github.com/pleisto/flappy.git")
    }
  }
}

// Apply a specific Java toolchain to ease working on different environments.
java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
  }
}

tasks.test {
  // Use JUnit Platform for unit tests.
  useJUnitPlatform()

  // show standard out and standard error of the test JVM(s) on the console
  testLogging.showStandardStreams = true

  testLogging {
    events("passed")
  }

}

//https://kotlinlang.org/docs/dokka-gradle.html#package-options
tasks.withType<DokkaTask>().configureEach {
  moduleName.set(project.name)
  moduleVersion.set(project.version.toString())
  outputDirectory.set(buildDir.resolve("dokka/$name"))
  failOnWarning.set(false)
  suppressObviousFunctions.set(true)
  suppressInheritedMembers.set(false)
  offlineMode.set(false)

  dokkaSourceSets {
    named("main") {
      suppress.set(false)
      displayName.set(name)
      documentedVisibilities.set(setOf(Visibility.PUBLIC))
      reportUndocumented.set(false)
      skipEmptyPackages.set(true)
      skipDeprecated.set(false)
      suppressGeneratedFiles.set(true)
      includeNonPublic.set(false)

      jdkVersion.set(11)
      includes.from(project.files(), "../README.md")
      sourceRoots.from(file("src/main"))

      sourceLink {
        localDirectory.set(projectDir.resolve("src"))
        remoteUrl.set(URL("https://github.com/pleisto/flappy/tree/main/packages/kotlin/flappy/src"))
        remoteLineSuffix.set("#L")
      }

      perPackageOption {
        matchingRegex.set("flappy.examples")
        suppress.set(true)
      }
    }
  }
}
