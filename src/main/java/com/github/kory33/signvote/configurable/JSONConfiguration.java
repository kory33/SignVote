package com.github.kory33.signvote.configurable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.Getter;

public class JSONConfiguration {
    @Getter private JsonObject jsonObject;
    
    /**
     * Constructs the text configuration instance.
     * @param configFile
     * @throws IOException when failed to read data from the given file.
     */
    public JSONConfiguration(File configFile) throws IOException {
        this.jsonObject = (new JsonParser()).parse(Files.newBufferedReader(configFile.toPath())).getAsJsonObject();
    }
    
    public Object fetchKeyObject(String joinedKey, String delimeter) {
        String[] keys = joinedKey.split(delimeter);
        JsonObject element = this.jsonObject;
        
        for (int index = 0; index < keys.length - 1; index++) {
            element = element.get(keys[index]).getAsJsonObject();
            
            if (element == null) {
                return null;
            }
        }
        return element.get(keys[keys.length - 1]).getAsJsonObject();
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
            Bukkit.getLogger().log(Level.WARNING, e.getMessage());
        }
        
        Bukkit.getLogger().log(Level.WARNING, "Failed to fetch the message: " + jsonKey + ". Returning this key instead.");
        return jsonKey;
    }
    
    /**
     * Get a message of specified jsonKey formatted using an object
     * @param jsonKey
     * @return
     */
    public String getFormatted(String jsonKey, Object object) {
        Object[] objArray = {object};
        return this.getFormatted(jsonKey, objArray);
    }

    /**
     * Get a message of specified jsonKey formatted using an object
     * @param jsonKey
     * @return
     */
    public String getFormatted(String jsonKey, Object... arguments) {
        return MessageFormat.format(this.getString(jsonKey), arguments);
    }
}
