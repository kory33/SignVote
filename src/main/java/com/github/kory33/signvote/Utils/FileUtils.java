package com.github.kory33.signvote.Utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.kory33.signvote.constants.Formats;

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
    
    public static void writeJSON(final File targetFile, JSONObject jsonObject) throws UnsupportedEncodingException, JSONException, IOException {
        if (!targetFile.exists()) {
            targetFile.createNewFile();
        }
        Files.write(targetFile.toPath(), jsonObject.toString(4).getBytes(Formats.FILE_ENCODING));
    }
}
