# Kotlin GLFW

Kotlin Native bindings for [GLFW](https://www.glfw.org) - an open‑source, 
multi‑platform library for creating windows, contexts,
and managing input for OpenGL, OpenGL ES, and Vulkan on desktop platforms.

This project provides type-safe Kotlin/Native bindings that map closely to the GLFW API,
so you can write Kotlin-first native applications for Windows, macOS, and Linux platforms.

## Features

- Kotlin Multiplatform Native targets:
    - Windows MinGW (x64)
    - macOS (x64, arm64)
    - Linux (x64, arm64)
- Multiple levels of abstraction for all types of use:
    - 1:1 bindings to the GLFW C API for minimal overhead, maximum flexibility, and control
    - Thin low-level type-safe wrapper
    - Kotlin-first API on top of the wrapper
- Ready for OpenGL, OpenGL ES, and Vulkan context creation

## Modules

- `glfw-interop` - C bindings that allow calling GLFW functions from Kotlin/Native directly
- `glfw-wrapper` - type-safe Kotlin/Native wrapper around the C bindings that maps closely to the GLFW API
- `glfw-kotlin` - library using GLFW API that provides a more Kotlin-friendly API on top of the wrapper

## Getting started

### 1) Add the dependency

Use Gradle Kotlin DSL. Replace GROUP, ARTIFACT, and VERSION with the actual coordinates.

```kotlin
// build.gradle.kts (consumer project) 
plugins {
  kotlin("multiplatform") version "2.2.0"
}

kotlin {
  mingwX64()
  macosX64()
  macosArm64()
  linuxX64()
  linuxArm64()

  sourceSets {
    commonMain.dependencies {
      implementation("io.github.kotlinopenfoundation.glfw:glfw-kotlin:0.1.0-dev")
    }
  }
}

repositories {
  maven {
    url = uri("https://maven.pkg.github.com/KotlinOpenFoundation/glfw")
  }
}
```

If you only target a subset of platforms, keep only the relevant targets.

### 2) Basic usage example

Below is a minimal example showing the typical GLFW lifecycle:

1) initialize
2) create a window
3) run a loop
4) then clean up

The exact API names may mirror GLFW closely.

```kotlin
// src/nativeMain/kotlin/Main.kt
fun main() {
  Glfw().use { glfw ->
    Window.create(Size2D(800, 600), "Hello World").use { window ->
      // Initialize here      
      window.current = true
      glfw.swapInterval = 1
      while (!window.shouldClose) {
        // Render here
        window.swapBuffers()
        glfw.pollEvents()
      }
    }
  }
}
```

Notes:
- You can register callbacks for input, resize, etc. via the corresponding GLFW-style functions
  (e.g., set key callback, framebuffer size callback).
- To use Vulkan, create a Vulkan instance with required extensions from GLFW and proceed as usual.

## Platform specifics and prerequisites

- Windows (MinGW x64): Requires a Mingw toolchain on the build machine
  (Gradle will use the platform toolchain configured for Kotlin/Native).
- macOS (x64/arm64): Ensure Xcode command line tools are installed.
  OpenGL availability varies by macOS version; Vulkan requires MoltenVK.
- Linux (x64/arm64): Building GLFW from source may require development packages. 
  If you build from source, install common dependencies such as:
    - For X11: xorg-dev, libx11-dev, libxrandr-dev, libxi-dev, libxcursor-dev, libxinerama-dev
    - For Wayland: wayland-protocols, libwayland-dev, libxkbcommon-dev
    - Package names vary by distro.

Depending on how the wrapper is configured, GLFW may be:
- Linked as a vendored/native artifact bundled by the dependency, or
- Linked against a system-installed GLFW.

Refer to the project’s Gradle properties or documentation for toggling between vendored and system GLFW if applicable.

## Gradle tips

Incremental development:
Use Gradle’s run tasks generated per target (e.g., runDebugExecutableMingwX64) to execute your native binaries.

## Samples

- Minimal window creation (WIP)
- Input and callbacks (TODO)
- OpenGL rendering loop (TODO)
- Vulkan surface creation (TODO)

Check the samples directory if provided, or see the basic usage section to start.

## Building from source

If you want to build this project itself:

```bash
# macOS/Linux
./gradlew build

# Windows
gradlew.bat build
```

Artifacts are published via standard Gradle publishing workflows.

## Troubleshooting

- Linker errors on Linux: Ensure X11/Wayland dev packages are installed if building GLFW locally.
- Undefined OpenGL symbols: Remember to load function pointers for OpenGL if required by your GL binding/loader.
- macOS context/profile issues: Ensure you request a compatible OpenGL core profile and forward compatibility hints as needed.
- Vulkan instance creation fails: Query required instance extensions via GLFW and enable them.

## Versioning

Follows semantic versioning where possible.
Versions may track upstream GLFW releases and Kotlin versions as needed.

## Contributing

Contributions are welcome:
- File issues for bugs and feature requests
- Open PRs for fixes and enhancements
- Follow Kotlin and GLFW coding conventions where applicable

Please run the full build and tests locally before submitting PRs.

## License

This project is distributed under the MIT license. 
See the [LICENSE](LICENSE) file for details.
