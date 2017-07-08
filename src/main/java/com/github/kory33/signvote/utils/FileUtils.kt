package com.github.kory33.signvote.utils

import com.github.kory33.signvote.constants.Formats
import com.google.gson.JsonObject
import com.google.gson.JsonParser

import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.Arrays
import java.util.stream.Stream

/**
 * An util class which handles file I/O
 */
object FileUtils {
    /**
     * delete folder and its content recursively.
     * @param targetDirectory directory to be deleted
     */
    fun deleteFolderRecursively(targetDirectory: File) {
        try {
            Files.walkFileTree(targetDirectory.toPath(), object : SimpleFileVisitor<Path>() {
                @Throws(IOException::class)
                override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                    Files.delete(file)
                    return FileVisitResult.CONTINUE
                }

                @Throws(IOException::class)
                override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
                    if (exc != null) {
                        throw exc
                    }
                    Files.delete(dir)
                    return FileVisitResult.CONTINUE
                }
            })
        } catch (e: IOException) {
            println("Error occured while browsing files under " + targetDirectory.toString() + ": " + e.toString())
        }

    }

    /**
     * Write json data to the given target file
     * @param targetFile target file to which json data should be written.
     * *                   File may not exist at the time of method invocation, but should not be a directory.
     * *
     * @param jsonObject a source json object
     */
    fun writeJSON(targetFile: File, jsonObject: JsonObject) {
        if (!targetFile.exists()) {
            val parent = targetFile.parentFile
            if (!parent.exists() && !parent.mkdirs()) {
                println("Failed to create directory " + parent.absolutePath)
                return
            }
        }

        try {
            Files.newOutputStream(targetFile.toPath()).use { oStream ->
                val writeData = jsonObject.toString().toByteArray(Formats.FILE_ENCODING)
                oStream.write(writeData)
            }
        } catch (exception: IOException) {
            println("Failed to write to file " + targetFile.absolutePath)
            exception.printStackTrace()
        }

    }

    /**
     * Read json data from the given target file

     */
    @Throws(IOException::class)
    fun readJSON(targetFile: File): JsonObject {
        Files.newBufferedReader(targetFile.toPath(), Formats.FILE_ENCODING).use { reader -> return JsonParser().parse(reader).asJsonObject }
    }

    /**
     * Get a stream of files that are present in the given directory
     * @param directory directory from which the list of files is fetched
     * *
     * @return a stream containing reference to files in the given directory
     * * or an empty stream if the given file does not represent a directory
     */
    fun getFileListStream(directory: File): Stream<File> {
        val files = directory.listFiles() ?: return Stream.empty<File>()
        return Arrays.stream(files)
    }

    /**
     * Get the name of the file with it's extension removed.
     * @param file target file
     * *
     * @return file name without the extension
     */
    fun getFileBaseName(file: File): String {
        val fileName = file.name
        val lastIndexOfDot = fileName.lastIndexOf(".")
        if (lastIndexOfDot == 0) {
            return fileName
        }
        return fileName.substring(0, lastIndexOfDot)
    }
}
