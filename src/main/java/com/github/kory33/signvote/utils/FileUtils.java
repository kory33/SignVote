package com.github.kory33.signvote.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

import com.github.kory33.signvote.constants.Formats;
import com.google.gson.JsonObject;

public class FileUtils {
    public static void deleteFolderRecursively(final File targetDirectory) throws IOException {
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
    }
    
    public static void writeJSON(final File targetFile, JsonObject jsonObject) throws UnsupportedEncodingException, IOException {
        byte[] writeData = jsonObject.toString().getBytes(Formats.FILE_ENCODING);
        long start = System.nanoTime();
        Files.newOutputStream(targetFile.toPath(), StandardOpenOption.CREATE).write(writeData);
        long end = System.nanoTime();
        System.out.println("File wrote! Took " + (end - start) + "ns.");
    }
}
