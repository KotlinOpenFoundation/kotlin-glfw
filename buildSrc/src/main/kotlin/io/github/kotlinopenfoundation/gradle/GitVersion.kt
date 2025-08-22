package io.github.kotlinopenfoundation.gradle

import com.palantir.gradle.gitversion.GitVersionCacheService
import com.palantir.gradle.gitversion.VersionDetails
import org.gradle.api.Project

fun VersionDetails.determineVersion(): String {
  val first = gitHash.startsWith(lastTag)
  val base = if (first) "0.0.0" else lastTag.removePrefix("v")
  val next = if (first)
    base
  else
    base.split(".")
      .map(String::toInt)
      .let { (major, minor, patch) -> listOf(major, minor, patch + 1) }
      .joinToString(".")

  return if (!isCleanTag)
    "$next-local"
  else if (commitDistance > 0)
    "$next-dev"
  else
    base
}

fun Project.getVersionDetails(prefix: String? = null): VersionDetails {
  val gitversionService = GitVersionCacheService.getSharedGitVersionCacheService(rootProject).get()
  val args = prefix?.let { mapOf("prefix" to prefix) }
  return gitversionService.getVersionDetails(project.rootDir, args)
}

fun Project.determineVersion(prefix: String? = null): String =
  getVersionDetails(prefix).determineVersion()
