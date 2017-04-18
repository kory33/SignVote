package com.github.kory33.signvote.collection;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class RunnableHashTable {
    private final HashMap<Long, Runnable> runnableTable;
    private final JavaPlugin plugin;
    private final Random randomGenerator;
    private final BukkitScheduler scheduler;

    public RunnableHashTable(JavaPlugin plugin) {
        this.runnableTable = new HashMap<>();
        this.randomGenerator = new Random();
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getScheduler();
    }

    /**
     * Register a runnable object and return.
     * @return An id value which can be passed to RunnableHashTable::run to run the runnable
     */
    public long registerRunnable(Runnable runnable) {
        while (true) {
            long runnableId = this.randomGenerator.nextLong();

            // re-generate id if the generated one is already registered
            if (this.runnableTable.containsKey(runnableId)) {
                continue;
            }

            this.runnableTable.put(runnableId, runnable);
            return runnableId;
        }
    }

    /**
     * Get a runnable associated with a given id.
     * @param runnableId
     * @return a runnable associated with a given runnable id
     */
    private Runnable getRunnable(long runnableId) throws IllegalArgumentException {
        Runnable task = this.runnableTable.get(runnableId);
        if (task == null) {
            throw new IllegalArgumentException("Runnable with given id " + runnableId + " not found!");
        }

        return task;
    }

    /**
     * Remove and cancel a task associated with a given id.
     * @param runnableId
     */
    public void cancelTask(long runnableId) {
        this.runnableTable.remove(runnableId);
    }

    /**
     * Synchronously run a task associated with a given id.
     * @param runnableId
     */
    public void runSync(long runnableId) {
        this.scheduler.runTask(this.plugin, this.getRunnable(runnableId));
        this.cancelTask(runnableId);
    }

    /**
     * Asynchronously run a task associated with a given id.
     * @param runnableId
     */
    public void runAsync(long runnableId) {
        this.scheduler.runTaskAsynchronously(this.plugin, this.getRunnable(runnableId));
        this.cancelTask(runnableId);
    }
}
