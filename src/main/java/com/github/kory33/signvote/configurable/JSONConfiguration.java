package com.github.kory33.signvote.configurable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.logging.Level;

public class JSONConfiguration {
    @Getter private JsonObject jsonObject;

    /**
     * Constructs the text configuration instance.
     * @param configFile file in which json object text data is stored
     * @throws IOException when failed to read data from the given file.
     */
    public JSONConfiguration(File configFile) throws IOException {
        BufferedReader reader = Files.newBufferedReader(configFile.toPath());
        this.jsonObject = (new JsonParser()).parse(reader).getAsJsonObject();
        reader.close();
    }

    private JsonElement fetchKeyObject(String joinedKey) {
        String[] keys = joinedKey.split("\\.");
        JsonObject element = this.jsonObject;

        for (int index = 0; index < keys.length - 1; index++) {
            element = element.get(keys[index]).getAsJsonObject();

            if (element == null) {
                throw new IllegalArgumentException("No element exists with the given value " + joinedKey);
            }
        }
        return element.get(keys[keys.length - 1]);
    }

    /**
     * Get the string data with the specified json key.
     * @param jsonKey key to the string value, joint with "."(period)
     * @return String if found at the given key location,
     * otherwise the key itself(warning will be logged in this case)
     */
    public String getString(String jsonKey) {
        try {
            String result = this.fetchKeyObject(jsonKey).getAsString();
            if (result != null) {
                return result;
            }
        } catch (Exception ignored) {}

        Bukkit.getLogger().log(Level.WARNING, "Failed to fetch the message: " + jsonKey + ". Returning this key instead.");
        return jsonKey;
    }

    /**
     * Get a message of specified jsonKey formatted using an object
     * @param jsonKey key indicating the format template string field
     * @return string at the location of key, formatted with object
     */
    public String getFormatted(String jsonKey, Object object) {
        Object[] objArray = {object};
        return this.getFormatted(jsonKey, objArray);
    }

    /**
     * Get a message of specified jsonKey formatted using an object
     * @param jsonKey key indicating the format template string field
     * @return string at the location of key, formatted with several objects
     */
    public String getFormatted(String jsonKey, Object... arguments) {
        return MessageFormat.format(this.getString(jsonKey), arguments);
    }
}
