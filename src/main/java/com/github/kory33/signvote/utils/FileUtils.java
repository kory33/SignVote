package com.github.kory33.signvote.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.stream.Stream;

import com.github.kory33.signvote.constants.Formats;
import com.google.gson.JsonObject;

public class FileUtils {
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

    public static void writeJSON(final File targetFile, JsonObject jsonObject) {
        try {
            if (!targetFile.exists()) {
                File parent = targetFile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                targetFile.createNewFile();
            }

            byte[] writeData = jsonObject.toString().getBytes(Formats.FILE_ENCODING);
            Files.newOutputStream(targetFile.toPath()).write(writeData);
        } catch (IOException exception) {
            System.out.println("Failed to write to file " + targetFile.getAbsolutePath());
            exception.printStackTrace();
        }
    }

    public static Stream<File> getFileListStream(File directory) {
        return Arrays.stream(directory.listFiles());
    }
}
