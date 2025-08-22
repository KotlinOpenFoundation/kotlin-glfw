import io.github.kotlinopenfoundation.gradle.determineVersion

plugins {
  id("org.jetbrains.kotlin.multiplatform") apply false
  id("dokka-convention")
}

group = "io.github.kotlinopenfoundation.glfw"
version = determineVersion()

subprojects {
  group = rootProject.group
  version = rootProject.version

  apply(plugin = "maven-publish")

  configure<PublishingExtension> {
    repositories {
      maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/KotlinOpenFoundation/glfw")
        credentials {
          username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
          password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
        }
      }
    }

    publications {
      register<MavenPublication>("gpr") {
        from(components.findByName("kotlin"))
      }
    }
  }
}

dependencies {
  dokka(project(":glfw-interop:"))
}
