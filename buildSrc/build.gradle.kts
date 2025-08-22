import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.UsesKotlinJavaToolchain

plugins {
  `kotlin-dsl`
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation(libs.dokka.gradle.plugin)
  implementation(libs.git.version.gradle.plugin)
  implementation(libs.kotlin.multiplatform.gradle.plugin)
}

val service = project.extensions.getByType<JavaToolchainService>()
val customLauncher = service.launcherFor {
  languageVersion.set(JavaLanguageVersion.of(22))
  vendor.set(JvmVendorSpec.ADOPTIUM)
}

project.tasks.withType<UsesKotlinJavaToolchain>().configureEach {
  kotlinJavaToolchain.toolchain.use(customLauncher)
}
