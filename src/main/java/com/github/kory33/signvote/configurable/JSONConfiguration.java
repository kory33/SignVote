package com.github.kory33.signvote.configurable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.json.JSONException;
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
    
    public Object fetchKeyObject(String joinedKey, String delimeter) throws JSONException {
        String[] keys = joinedKey.split(delimeter);
        JSONObject object = this.jsonObject;
        
        for (int index = 0; index < keys.length - 1; index++) {
            object = object.getJSONObject(keys[index]);
        }
        return object.get(keys[keys.length - 1]);
    }
    
    /**
     * Get the string data with the specified json key.
     * @param jsonKey
     * @return
     */
    public String getString(String jsonKey) {
        try {
            String result = this.fetchKeyObject(jsonKey, "\\.").toString();
            if (result != null) {
                return result;
            }
        } catch (Exception e) {
        }
        
        Bukkit.getLogger().log(Level.SEVERE, "Failed to fetch the message: " + jsonKey + ". Returning this key instead.");
        return jsonKey;
    }
}
