package com.github.kory33.signvote.configurable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.json.JSONObject;

import lombok.Getter;

public class JSONConfiguration {
    @Getter private JSONObject jsonObject;
    
    /**
     * Constructs the text configuration instance.
     * @param configFile
     * @throws IOException when failed to read data from the given file.
     */
    public JSONConfiguration(File configFile) throws IOException {
        String fileContent = String.join("", Files.readAllLines(configFile.toPath()));
        this.jsonObject = new JSONObject(fileContent);
    }
    
    /**
     * Get the string data with the specified json key.
     * @param jsonKey
     * @return
     */
    public String getString(String jsonKey) {
        return this.jsonObject.getString(jsonKey);
    }
}
