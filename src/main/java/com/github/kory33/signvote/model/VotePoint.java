package com.github.kory33.signvote.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import com.github.kory33.signvote.constants.VotePointDataFileKeys;
import com.github.kory33.signvote.session.VoteSession;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.Getter;

public class VotePoint {
    @Getter private String name;
    @Getter private Sign voteSign;

    public VotePoint(String name, Sign voteSign, VoteSession parentSession) {
        this.name = name;
        this.voteSign = voteSign;
    }
    
    public VotePoint(File votePointFIle) throws IllegalArgumentException, IOException {
        JsonObject jsonObject = (new JsonParser()).parse(Files.newBufferedReader(votePointFIle.toPath())).getAsJsonObject();
        
        this.name = jsonObject.get(VotePointDataFileKeys.NAME).getAsString();
        
        World world = Bukkit.getWorld(jsonObject.get(VotePointDataFileKeys.VOTE_SIGN_WORLD).getAsString());
        int signX = jsonObject.get(VotePointDataFileKeys.VOTE_SIGN_LOC_X).getAsInt();
        int signY = jsonObject.get(VotePointDataFileKeys.VOTE_SIGN_LOC_Y).getAsInt();
        int signZ = jsonObject.get(VotePointDataFileKeys.VOTE_SIGN_LOC_Z).getAsInt();
        
        if (world == null) {
            throw new IllegalArgumentException("Invalid file given! (world name missing)");
        }
        
        BlockState state = world.getBlockAt(signX, signY, signZ).getState();
        if (!(state instanceof Sign)) {
            throw new IllegalStateException("Sign does not exist at the location in the saved data!");
        }
        
        this.voteSign = (Sign) state;
    }
    
    /**
     * Get the json-serialized data of this object.
     * @return
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
