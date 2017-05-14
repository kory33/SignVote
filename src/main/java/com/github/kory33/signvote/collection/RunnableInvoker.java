package com.github.kory33.signvote.collection;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Random;

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

    private String getCommandString(long runnableId, boolean async) {
        return "/" + this.getName() + " " + runnableId + (async ? " " + ASYNC_MODIFIER : "");
    }

    /**
     * Register a runnable object and return.
     * @return An id value which can be passed to RunnableInvokerCommand::run to run the runnable
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

    public static RunnableInvoker getRegisteredInstance(JavaPlugin plugin, String runCommandRoot) {
        RunnableInvoker commandExecutor = new RunnableInvoker(plugin, runCommandRoot);

        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            commandMap.register(commandExecutor.getName(), commandExecutor);

            return commandExecutor;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static RunnableInvoker getRegisteredInstance(JavaPlugin plugin) {
        return getRegisteredInstance(plugin, DEFAULT_COMMAND_ROOT);
    }
}
