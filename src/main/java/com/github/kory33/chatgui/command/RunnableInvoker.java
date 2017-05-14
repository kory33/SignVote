package com.github.kory33.chatgui.command;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.github.kory33.chatgui.io.CommandFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Class which handles registrations and invocations of runnable objects through Bukkit's command interface.
 * @author Kory
 */
public class RunnableInvoker extends BukkitCommand {
    private static final String DEFAULT_COMMAND_ROOT = "run";
    private static final String ASYNC_MODIFIER = "async";
    private static final String FALLBACK_PREFIX = "runnableinvoker";

    private static Set<JavaPlugin> invocationSuppressedPlugins = new HashSet<>();

    private final HashMap<Long, Runnable> runnableTable;
    private final JavaPlugin plugin;
    private final Random randomGenerator;
    private final BukkitScheduler scheduler;

    private RunnableInvoker(JavaPlugin plugin, String commandRoot) {
        super(plugin.getName().toLowerCase() + ":" + commandRoot);

        this.runnableTable = new HashMap<>();
        this.randomGenerator = new Random();
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getScheduler();

    }

    private String getRootCommandString() {
        return "/" + FALLBACK_PREFIX + ":" + this.getName();
    }

    private String getCommandString(long runnableId, boolean async) {
        return this.getRootCommandString() + " " + runnableId + (async ? " " + ASYNC_MODIFIER : "");
    }

    /**
     * Get a command that is able to invoke the given runnable object
     * @param runnable target runnable object
     * @param isAsync specify whether or not the runnable should be invoked asynchronously
     * @return {@link RunnableCommand} object containing runnable id and command to cancel/invoke the runnable
     */
    public RunnableCommand registerRunnable(Runnable runnable, boolean isAsync) {
        while (true) {
            long runnableId = this.randomGenerator.nextLong();

            // re-generate id if the generated one is already registered
            if (this.runnableTable.containsKey(runnableId)) {
                continue;
            }

            this.runnableTable.put(runnableId, runnable);
            return new RunnableCommand(this.getCommandString(runnableId, isAsync), runnableId);
        }
    }

    /**
     * Remove and cancel a task associated with a given id.
     * @param runnableId id of the target runnable
     */
    public void cancelTask(long runnableId) {
        this.runnableTable.remove(runnableId);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length < 1) {
            return true;
        }

        try {
            long runnableId = Long.parseLong(args[0]);
            Runnable runnable = this.runnableTable.get(runnableId);
            if (runnable == null) {
                return true;
            }

            if (args.length > 1 && args[1].equalsIgnoreCase(ASYNC_MODIFIER)) {
                this.scheduler.runTaskAsynchronously(this.plugin, runnable);
            } else {
                this.scheduler.runTask(this.plugin, runnable);
            }
        } catch (NumberFormatException exception) {
            return true;
        }

        return true;
    }

    private void addInvocationSuppressFilter() {
        if (invocationSuppressedPlugins.contains(this.plugin)) {
            return;
        }
        ((Logger) LogManager.getRootLogger()).addFilter(new CommandFilter(this.getRootCommandString()));
        invocationSuppressedPlugins.add(this.plugin);
    }

    public static RunnableInvoker getRegisteredInstance(JavaPlugin plugin, String runCommandRoot, boolean suppressCommand) {
        RunnableInvoker commandExecutor = new RunnableInvoker(plugin, runCommandRoot);

        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            commandMap.register(commandExecutor.getName(), FALLBACK_PREFIX, commandExecutor);

            if (suppressCommand) {
                commandExecutor.addInvocationSuppressFilter();
            }

            return commandExecutor;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static RunnableInvoker getRegisteredInstance(JavaPlugin plugin) {
        return getRegisteredInstance(plugin, DEFAULT_COMMAND_ROOT, true);
    }
}
