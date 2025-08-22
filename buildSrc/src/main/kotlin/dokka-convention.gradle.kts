plugins {
  id("org.jetbrains.dokka")
  id("com.palantir.git-version")
}

dokka {
  moduleName = project.name

  dokkaPublications.html {
    suppressInheritedMembers = true
    failOnWarning = true
  }

  dokkaSourceSets.findByName("main")?.apply {
    includes.from("README.md")
    sourceLink {
      localDirectory = file("src/main/kotlin")
      val modulePath = projectDir.relativeTo(rootDir).path.replace('\\', '/')
      remoteUrl("https://github.com/KotlinOpenFoundation/kotlin-glfw/blob/main/$modulePath/src")
      remoteLineSuffix = "#L"
    }
  }

  pluginsConfiguration.html {
    footerMessage = "(c) Kotlin Open Foundation"
  }
}
