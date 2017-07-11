package com.github.kory33.signvote.io

import com.github.kory33.signvote.core.SignVote
import org.bukkit.Server
import org.bukkit.scheduler.BukkitScheduler

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * A class which is capable of scheduling auto-save of the plugin data with a specified time interval.
 * @author Kory
 */
class PluginDataAutoSaver(private val plugin: SignVote, autosaveInterval: Int, private val shouldLog: Boolean) {
    private var nextAutoSaveTaskId: Int = 0

    private val autosaveIntervalTicks: Long
    private val server: Server = plugin.server
    private val scheduler: BukkitScheduler
    private val saveTaskExecutor: ExecutorService

    private fun scheduleNextAutoSaveTask() {
        CompletableFuture.runAsync(Runnable { this.plugin.saveSessionData() }, this.saveTaskExecutor)
        if (this.shouldLog) {
            this.plugin.logger.info("Session data is being saved asynchronously...")
        }

        this.nextAutoSaveTaskId = scheduler.scheduleSyncDelayedTask(this.plugin,
                { this.scheduleNextAutoSaveTask() }, this.autosaveIntervalTicks)
    }

    /**
     * Stop and cancel the autosave task.
     */
    fun stopAutoSaveTask() {
        server.scheduler.cancelTask(this.nextAutoSaveTaskId)
    }

    init {
        this.scheduler = server.scheduler

        this.autosaveIntervalTicks = autosaveInterval.toLong()
        this.saveTaskExecutor = Executors.newFixedThreadPool(1)

        this.scheduleNextAutoSaveTask()
    }
}
