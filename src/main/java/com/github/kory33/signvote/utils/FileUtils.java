package com.github.kory33.signvote.utils;

import com.github.kory33.signvote.constants.Formats;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * An util class which handles file I/O
 */
public class FileUtils {
    /**
     * delete folder and its content recursively.
     * @param targetDirectory directory to be deleted
     */
    public static void deleteFolderRecursively(final File targetDirectory) {
        try {
            Files.walkFileTree(targetDirectory.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (exc != null) {
                        throw exc;
                    }
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.out.println("Error occured while browsing files under " + targetDirectory.toString() + ": " + e.toString());
        }
    }

    /**
     * Write json data to the given target file
     * @param targetFile target file to which json data should be written.
     *                   File may not exist at the time of method invocation, but should not be a directory.
     * @param jsonObject a source json object
     */
    public static void writeJSON(final File targetFile, JsonObject jsonObject) {
        if (!targetFile.exists()) {
            File parent = targetFile.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                System.out.println("Failed to create directory " + parent.getAbsolutePath());
                return;
            }
        }

        try(OutputStream oStream = Files.newOutputStream(targetFile.toPath())) {
            byte[] writeData = jsonObject.toString().getBytes(Formats.FILE_ENCODING);
            oStream.write(writeData);
        } catch (IOException exception) {
            System.out.println("Failed to write to file " + targetFile.getAbsolutePath());
            exception.printStackTrace();
        }
    }

    /**
     * Read json data from the given target file
     *
     */
    public static JsonObject readJSON(final File targetFile) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(targetFile.toPath(), Formats.FILE_ENCODING)) {
            return (new JsonParser()).parse(reader).getAsJsonObject();
        }
    }

    /**
     * Get a stream of files that are present in the given directory
     * @param directory directory from which the list of files is fetched
     * @return a stream containing reference to files in the given directory
     */
    public static Stream<File> getFileListStream(File directory) {
        File[] files = directory.listFiles();
        assert files != null;
        return Arrays.stream(files);
    }

    /**
     * Get the name of the file with it's extension removed.
     * @param file target file
     * @return file name without the extension
     */
    public static String getFileBaseName(File file) {
        String fileName = file.getName();
        int lastIndexOfDot = fileName.lastIndexOf(".");
        if (lastIndexOfDot == 0) {
            return fileName;
        }
        return fileName.substring(0, lastIndexOfDot);
    }
}
