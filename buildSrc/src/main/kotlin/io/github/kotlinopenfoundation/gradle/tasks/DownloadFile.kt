package io.github.kotlinopenfoundation.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.File
import java.net.HttpURLConnection
import java.net.URI

@CacheableTask
abstract class DownloadFile : DefaultTask() {
  @get:Input
  abstract val uri: Property<URI>

  @get:OutputFile
  abstract val output: RegularFileProperty

  @get:Input
  @get:Optional
  abstract val etag: Property<String>

  init {
    outputs.cacheIf { true }
  }

  @TaskAction
  fun download() {
    val dest = output.get().asFile
    dest.parentFile.mkdirs()

    val connection = uri.get().toURL().openConnection() as HttpURLConnection
    val etagFile = etag.orNull?.let { File(it) }
    etagFile
      ?.takeIf { it.exists() && dest.exists() }
      ?.readText()
      ?.let { connection.setRequestProperty("If-None-Match", it) }

    connection.connect()
    when (connection.responseCode) {
      HttpURLConnection.HTTP_NOT_MODIFIED -> return logger.lifecycle("File is up to date: ${dest.name}")
      HttpURLConnection.HTTP_OK -> {}
      else -> throw GradleException("Failed to download file: HTTP ${connection.responseCode}")
    }

    dest.outputStream().use { out -> connection.inputStream.use { it.copyTo(out) } }
    connection.getHeaderField("ETag")?.let { newEtag ->
      etagFile?.also { it.parentFile.mkdirs() }?.writeText(newEtag)
    }
    logger.lifecycle("Downloaded: ${dest.name}")
  }
}
