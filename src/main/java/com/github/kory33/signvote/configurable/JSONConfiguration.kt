package com.github.kory33.signvote.configurable

import com.github.kory33.signvote.utils.FileUtils
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.bukkit.Bukkit

import java.io.File
import java.io.IOException
import java.text.MessageFormat
import java.util.logging.Level

class JSONConfiguration
/**
 * Constructs the text configuration instance.
 * @param configFile file in which json object text data is stored
 * *
 * @throws IOException when failed to read data from the given file.
 */
@Throws(IOException::class)
constructor(configFile: File) {
    val jsonObject: JsonObject = FileUtils.readJSON(configFile)

    private fun fetchKeyObject(joinedKey: String): JsonElement {
        val keys = joinedKey.split("\\.".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        var element: JsonObject? = this.jsonObject

        for (index in 0..keys.size - 1 - 1) {
            element = element!!.get(keys[index]).asJsonObject

            if (element == null) {
                throw IllegalArgumentException("No element exists with the given value " + joinedKey)
            }
        }
        return element!!.get(keys[keys.size - 1])
    }

    /**
     * Get the string data with the specified json key.
     * @param jsonKey key to the string value, joint with "."(period)
     * *
     * @return String if found at the given key location,
     * * otherwise the key itself(warning will be logged in this case)
     */
    fun getString(jsonKey: String): String {
        try {
            val result = this.fetchKeyObject(jsonKey).asString
            if (result != null) {
                return result
            }
        } catch (ignored: Exception) {
        }

        Bukkit.getLogger().log(Level.WARNING, "Failed to fetch the message: $jsonKey. Returning this key instead.")
        return jsonKey
    }

    /**
     * Get a message of specified jsonKey formatted using an object
     * @param jsonKey key indicating the format template string field
     * *
     * @return string at the location of key, formatted with object
     */
    fun getFormatted(jsonKey: String, `object`: Any): String {
        val objArray = arrayOf(`object`)
        return this.getFormatted(jsonKey, *objArray)
    }

    /**
     * Get a message of specified jsonKey formatted using an object
     * @param jsonKey key indicating the format template string field
     * *
     * @return string at the location of key, formatted with several objects
     */
    fun getFormatted(jsonKey: String, vararg arguments: Any): String {
        return MessageFormat.format(this.getString(jsonKey), *arguments)
    }
}
