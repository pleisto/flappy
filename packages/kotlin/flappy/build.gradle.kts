import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.dokka.gradle.DokkaTask

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

  signing
}

repositories {
  // Use Maven Central for resolving dependencies.
  mavenCentral()
}

mavenPublishing {
  publishToMavenCentral(SonatypeHost.S01)
  signAllPublications()
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
//        https://central.sonatype.com/publishing/deployments
//        maven {
//            name = "Sonatype"
//            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
//            credentials {
//                username = project.findProperty("sonatype.user") as String? ?: System.getenv("SONATYPE_USERNAME")
//                password = project.findProperty("sonatype.key") as String? ?: System.getenv("SONATYPE_TOKEN")
//            }
//        }
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

  implementation("io.ktor:ktor-client-java-jvm:2.3.4")
  implementation("com.aallam.openai:openai-client:3.4.1")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.+")
  implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
}

//mavenPublishing {
//    coordinates("com.pleisto", "flappy", "0.0.10")
//
//    pom {
//        name.set("flappy")
//        description.set("A description of what my library does.")
//        url.set("https://github.com/pleisto/flappy/")
//    }
//}

// Apply a specific Java toolchain to ease working on different environments.
java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(19))
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



tasks.withType<DokkaTask>().configureEach {
  moduleName.set(project.name)
  moduleVersion.set(project.version.toString())
  outputDirectory.set(buildDir.resolve("dokka/$name"))
  failOnWarning.set(false)
  suppressObviousFunctions.set(true)
  suppressInheritedMembers.set(false)
  offlineMode.set(false)
}
