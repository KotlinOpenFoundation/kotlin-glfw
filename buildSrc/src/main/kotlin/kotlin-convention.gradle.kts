import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.tasks.UsesKotlinJavaToolchain

plugins {
  id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
  val os = OperatingSystem.current()
  val arch = System.getProperty("os.arch")?.lowercase()

  when {
    os.isWindows -> mingwX64()
    os.isLinux && arch == "aarch64" -> linuxArm64()
    os.isLinux -> linuxX64()
    os.isMacOsX && arch == "aarch64" -> macosArm64()
    os.isMacOsX -> macosX64()
    else -> error("Unsupported host: ${os.name} ($arch)")
  }
}

val service = project.extensions.getByType<JavaToolchainService>()
val customLauncher = service.launcherFor {
  languageVersion.set(JavaLanguageVersion.of(22))
  vendor.set(JvmVendorSpec.ADOPTIUM)
}

project.tasks.withType<UsesKotlinJavaToolchain>().configureEach {
  kotlinJavaToolchain.toolchain.use(customLauncher)
}
