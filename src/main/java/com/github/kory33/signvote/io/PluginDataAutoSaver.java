package com.github.kory33.signvote.io;

import org.bukkit.Bukkit;

import com.github.kory33.signvote.core.SignVote;

public class PluginDataAutoSaver {
    private int nextAutoSaveTaskId;

    private final long autosaveIntervalTicks;
    private final SignVote plugin;
    
    private boolean isTaskScheduled;

    private void scheduleNextAutoSaveTask() {
        this.nextAutoSaveTaskId = Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
            @Override
            public void run() {
                PluginDataAutoSaver.this.plugin.saveSessionData();
                PluginDataAutoSaver.this.plugin.getLogger().info("Session data has been saved.");
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
    
    public PluginDataAutoSaver(SignVote plugin, int autosaveInterval) {
        this.plugin = plugin;
        this.autosaveIntervalTicks = autosaveInterval;
        
        this.scheduleNextAutoSaveTask();
    }
}
