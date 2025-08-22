package platform.glfw

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toKString
import kotlin.test.*

@OptIn(ExperimentalForeignApi::class)
class GlfwTest {
  @BeforeClass
  fun setUp() {
    glfwSetErrorCallback(staticCFunction { error, description ->
      throw Exception("GLFW Error: $error: ${description?.toKString()}")
    })
  }

  @AfterTest
  fun cleanUp() {
    glfwTerminate()
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
