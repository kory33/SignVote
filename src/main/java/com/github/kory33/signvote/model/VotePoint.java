package com.github.kory33.signvote.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.json.JSONObject;
import com.github.kory33.signvote.constants.VotePointDataFileKeys;
import com.github.kory33.signvote.session.VoteSession;

import lombok.Getter;

public class VotePoint {
    @Getter private String name;
    @Getter private Sign voteSign;

    public VotePoint(String name, Sign voteSign, VoteSession parentSession) {
        this.name = name;
        this.voteSign = voteSign;
    }
    
    public VotePoint(File votePointFIle) throws IllegalArgumentException, IOException {
        String fileContent = String.join("", Files.readAllLines(votePointFIle.toPath()));
        JSONObject jsonObject = new JSONObject(fileContent);
        
        this.name = jsonObject.getString(VotePointDataFileKeys.NAME);
        
        World world = Bukkit.getWorld(jsonObject.getString(VotePointDataFileKeys.VOTE_SIGN_WORLD));
        int signX = new Integer(jsonObject.getString(VotePointDataFileKeys.VOTE_SIGN_LOC_X));
        int signY = new Integer(jsonObject.getString(VotePointDataFileKeys.VOTE_SIGN_LOC_Y));
        int signZ = new Integer(jsonObject.getString(VotePointDataFileKeys.VOTE_SIGN_LOC_Z));
        
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
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        
        jsonObject.put(VotePointDataFileKeys.NAME, this.name);
        jsonObject.put(VotePointDataFileKeys.VOTE_SIGN_WORLD, this.voteSign.getWorld());
        jsonObject.put(VotePointDataFileKeys.VOTE_SIGN_LOC_X, this.voteSign.getX());
        jsonObject.put(VotePointDataFileKeys.VOTE_SIGN_LOC_Y, this.voteSign.getY());
        jsonObject.put(VotePointDataFileKeys.VOTE_SIGN_LOC_Z, this.voteSign.getZ());

        return jsonObject;
    }
}
