package com.github.kory33.signvote.io;

import com.github.kory33.signvote.core.SignVote;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A class which is capable of scheduling auto-save of the plugin data with a specified time interval.
 * @author Kory
 */
public class PluginDataAutoSaver {
    private int nextAutoSaveTaskId;

    private final long autosaveIntervalTicks;
    private final boolean shouldLog;

    private final SignVote plugin;
    private final Server server;
    private final BukkitScheduler scheduler;
    private final ExecutorService saveTaskExecutor;
    
    private void scheduleNextAutoSaveTask() {
        CompletableFuture.runAsync(PluginDataAutoSaver.this.plugin::saveSessionData, PluginDataAutoSaver.this.saveTaskExecutor);
        if (this.shouldLog) {
            PluginDataAutoSaver.this.plugin.getLogger().info("Session data is being saved asynchronously...");
        }
        
        this.nextAutoSaveTaskId = scheduler.scheduleSyncDelayedTask(this.plugin,
                        PluginDataAutoSaver.this::scheduleNextAutoSaveTask, this.autosaveIntervalTicks);
    }

    /**
     * Stop and cancel the autosave task.
     */
    public void stopAutoSaveTask() {
        server.getScheduler().cancelTask(this.nextAutoSaveTaskId);
    }
    
    public PluginDataAutoSaver(SignVote plugin, int autosaveInterval, boolean shouldLog) {
        this.plugin = plugin;
        this.server = plugin.getServer();
        this.scheduler = server.getScheduler();

        this.autosaveIntervalTicks = autosaveInterval;
        this.saveTaskExecutor = Executors.newFixedThreadPool(1);
        this.shouldLog = shouldLog;
        
        this.scheduleNextAutoSaveTask();
    }
}
