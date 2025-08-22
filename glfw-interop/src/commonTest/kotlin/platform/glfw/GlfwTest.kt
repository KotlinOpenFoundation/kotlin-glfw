package platform.glfw

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toKString
import kotlin.test.*

@OptIn(ExperimentalForeignApi::class)
class GlfwTest {
  @BeforeTest
  fun setUp() {
    glfwSetErrorCallback(staticCFunction { error, description ->
      fail("GLFW Error: $error: ${description?.toKString()}")
    })
  }

  @AfterTest
  fun cleanUp() {
    glfwTerminate()
    glfwSetErrorCallback(null)
  }

  @Test
  fun initAndTerminate() {
    if (glfwInit() != GLFW_TRUE) {
      fail("Failed to initialize GLFW")
    }
    glfwTerminate()
  }

  @Test
  fun openAndCloseWindow() {
    if (glfwInit() != GLFW_TRUE) {
      fail("Failed to initialize GLFW")
    }

    // reset widow hints
    glfwDefaultWindowHints()

    // OpenGL Core Profile
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)

    // Headless-friendly defaults
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    glfwWindowHint(GLFW_DOUBLEBUFFER, GLFW_FALSE)

    val window = glfwCreateWindow(640, 480, "Test Window", null, null)
    assertNotNull(window, "Failed to create GLFW window")
    glfwMakeContextCurrent(window)
    assertEquals(window, glfwGetCurrentContext(), "Failed to make GLFW window current")
    glfwDestroyWindow(window)

    glfwTerminate()
  }
}
