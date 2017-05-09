package com.github.kory33.signvote.model;

import com.github.kory33.signvote.constants.Formats;
import com.github.kory33.signvote.constants.VotePointDataFileKeys;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * A class representing a sign of a vote point
 */
public class VotePoint {
    @Setter @Getter private String name;
    @Getter private Sign voteSign;

    public VotePoint(String name, Sign voteSign) {
        this.name = name;
        this.voteSign = voteSign;
    }

    public VotePoint(File votePointFIle) throws IllegalArgumentException, IOException {
        BufferedReader reader = Files.newBufferedReader(votePointFIle.toPath(), Formats.FILE_ENCODING);
        JsonObject jsonObject = (new JsonParser()).parse(reader).getAsJsonObject();
        reader.close();

        this.name = jsonObject.get(VotePointDataFileKeys.NAME).getAsString();

        String worldName = jsonObject.get(VotePointDataFileKeys.VOTE_SIGN_WORLD).getAsString();
        World world = Bukkit.getWorld(worldName);
        int signX = jsonObject.get(VotePointDataFileKeys.VOTE_SIGN_LOC_X).getAsInt();
        int signY = jsonObject.get(VotePointDataFileKeys.VOTE_SIGN_LOC_Y).getAsInt();
        int signZ = jsonObject.get(VotePointDataFileKeys.VOTE_SIGN_LOC_Z).getAsInt();

        if (world == null) {
            throw new IllegalArgumentException("Invalid file given! (Invalid world name " + worldName + ")");
        }

        BlockState state = world.getBlockAt(signX, signY, signZ).getState();
        if (!(state instanceof Sign)) {
            throw new IllegalStateException("Sign does not exist at the location in the saved data!");
        }

        this.voteSign = (Sign) state;
    }

    /**
     * Get the json-serialized data of this object.
     * @return a json file that contains
     */
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(VotePointDataFileKeys.NAME, this.name);
        jsonObject.addProperty(VotePointDataFileKeys.VOTE_SIGN_WORLD, this.voteSign.getWorld().getName());
        jsonObject.addProperty(VotePointDataFileKeys.VOTE_SIGN_LOC_X, this.voteSign.getX());
        jsonObject.addProperty(VotePointDataFileKeys.VOTE_SIGN_LOC_Y, this.voteSign.getY());
        jsonObject.addProperty(VotePointDataFileKeys.VOTE_SIGN_LOC_Z, this.voteSign.getZ());

        return jsonObject;
    }
}
