package com.github.kory33.signvote.io;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.Bukkit;

import com.github.kory33.signvote.core.SignVote;

public class PluginDataAutoSaver {
    private int nextAutoSaveTaskId;

    private final long autosaveIntervalTicks;
    private final boolean shouldLog;
    private final SignVote plugin;
    
    private boolean isTaskScheduled;

    private final ExecutorService saveTaskExecutor;
    
    private void scheduleNextAutoSaveTask() {
        CompletableFuture.runAsync(() -> PluginDataAutoSaver.this.plugin.saveSessionData(), PluginDataAutoSaver.this.saveTaskExecutor);
        if (this.shouldLog) {
            PluginDataAutoSaver.this.plugin.getLogger().info("Session data is being saved asynchronously...");
        }
        
        this.nextAutoSaveTaskId = Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
            @Override
            public void run() {
                PluginDataAutoSaver.this.scheduleNextAutoSaveTask();
            }
        }, this.autosaveIntervalTicks);
        
        this.isTaskScheduled = true;
    }
    
    /**
     * Schedule a repeating autosave task.
     * This method should fail silently if there already exists a task scheduled.
     */
    public void scheduleAutoSaveTask() {
        if (this.isTaskScheduled) {
            return;
        }
        this.scheduleNextAutoSaveTask();
    }

    /**
     * Stop and cancel the autosave task.
     */
    public void stopAutoSaveTask() {
        Bukkit.getServer().getScheduler().cancelTask(this.nextAutoSaveTaskId);
        this.isTaskScheduled = false;
    }
    
    public PluginDataAutoSaver(SignVote plugin, int autosaveInterval, boolean shouldLog) {
        this.plugin = plugin;
        this.autosaveIntervalTicks = autosaveInterval;
        this.saveTaskExecutor = Executors.newFixedThreadPool(1);
        this.shouldLog = shouldLog;
        
        this.scheduleNextAutoSaveTask();
    }
}
