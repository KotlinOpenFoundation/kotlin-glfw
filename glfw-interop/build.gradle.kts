import io.github.kotlinopenfoundation.gradle.tasks.DownloadFile
import org.gradle.internal.extensions.stdlib.capitalized
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

val glfwLibVersion = "3.4"
val glfwLibBaseUrl = "https://github.com/glfw/glfw/releases/download"

plugins {
  id("dokka-convention")
  id("kotlin-convention")
  alias(libs.plugins.kotlin.powerassert)
}

kotlin {
  targets.withType<KotlinNativeTarget>().configureEach {
    compilations["main"].cinterops {
      val glfw by creating
    }
  }

  sourceSets {
    commonMain.dependencies {
      implementation(libs.kotlin.logging)
    }
    commonTest.dependencies {
      implementation(libs.kotlin.test)
    }
  }
}

tasks {
  mapOf(
    "Mingw" to ".bin.WIN64",
    "Macos" to ".bin.MACOS",
    "Src" to ""
  ).forEach { (type, suffix) ->
    val fileName = "glfw-$glfwLibVersion$suffix.zip"

    val downloadTask = register<DownloadFile>("downloadGlfwLib${type.capitalized()}") {
      uri = uri("$glfwLibBaseUrl/$glfwLibVersion/$fileName")
      output = project.layout.buildDirectory.file("lib/glfw/$fileName")
      etag = project.layout.buildDirectory.file("lib/glfw/$fileName.etag").get().asFile.absolutePath
    }

    register<Copy>("extractGlfwLib$type") {
      dependsOn(downloadTask)
      from(zipTree(downloadTask.get().output)) {
        include("**")
        includeEmptyDirs = false
        eachFile {
          relativePath = RelativePath(true, *relativePath.segments.drop(1).toTypedArray())
        }
      }
      val dir = project.layout.buildDirectory.dir("lib/glfw/${type.lowercase()}")
      into(dir)
      outputs.dir(dir)
      outputs.cacheIf { dir.get().asFile.list()?.isNotEmpty() ?: false }
    }
  }

  val cmakeConfigureTask = register<Exec>("cmakeConfigureGlfwLibSrc") {
    dependsOn("extractGlfwLibSrc")

    workingDir = project.layout.buildDirectory.dir("lib/glfw/src").get().asFile
    commandLine(
      "cmake", "-S", ".", "-B", "build",
      "-DCMAKE_BUILD_TYPE=Release",
      "-DGLFW_BUILD_EXAMPLES=OFF",
      "-DGLFW_BUILD_TESTS=OFF",
      "-DGLFW_BUILD_DOCS=OFF",
      "-DGLFW_BUILD_WAYLAND=OFF" // TODO: support wayland
    )
  }

  val cmakeBuildTask = register<Exec>("cmakeBuildGlfwLibSrc") {
    dependsOn(cmakeConfigureTask)

    workingDir = project.layout.buildDirectory.dir("lib/glfw/src").get().asFile
    commandLine("cmake", "--build", "build", "-j", Runtime.getRuntime().availableProcessors())
    outputs.dir(project.layout.buildDirectory.dir("lib/glfw/src/build/src"))
  }

  val copyCompiledLibTask = register<Copy>("copyGlfwLibSrc") {
    dependsOn(cmakeBuildTask)
    from(project.layout.buildDirectory.dir("lib/glfw/src/build/src")) {
      include("libglfw3.a")
      include("*.h")
    }
    val dir = project.layout.buildDirectory.dir("lib/glfw/src/lib")
    into(dir)
    outputs.dir(dir)
    outputs.cacheIf { true }
  }

  kotlin.targets.withType<KotlinNativeTarget> {
    val platform = name.removeSuffix("X64").removeSuffix("Arm64").capitalized()
    named("cinteropGlfw${name.capitalized()}") {
      dependsOn(findByName("extractGlfwLib$platform") ?: copyCompiledLibTask)
    }
  }
}
