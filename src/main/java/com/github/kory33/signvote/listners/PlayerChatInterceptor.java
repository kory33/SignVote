package com.github.kory33.signvote.listners;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.kory33.signvote.exception.ChatInterceptionCancelledException;

public class PlayerChatInterceptor implements Listener {
    private final Map<Player, CompletableFuture<String>> interceptionFutureMap;

    public PlayerChatInterceptor(JavaPlugin hostPlugin) {
        hostPlugin.getServer().getPluginManager().registerEvents(this, hostPlugin);
        this.interceptionFutureMap = new HashMap<>();
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();

        CompletableFuture<String> future = this.interceptionFutureMap.remove(sender);
        if (future == null) {
            return;
        }

        future.complete(event.getMessage());
    }

    @EventHandler
    public void onPlayerExit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CompletableFuture<String> future = this.interceptionFutureMap.remove(player);

        if (future == null) {
            return;
        }

        future.completeExceptionally(new ChatInterceptionCancelledException("Player has quit."));
    }

    /**
     * Intercept the first message sent by the given player.
     * @param sourcePlayer
     * @return
     */
    public CompletableFuture<String> interceptFirstMessageFrom(Player sourcePlayer) {
        CompletableFuture<String> future = new CompletableFuture<>();

        if (this.interceptionFutureMap.containsKey(sourcePlayer)) {
            CompletableFuture<String> oldFuture = this.interceptionFutureMap.remove(sourcePlayer);
            oldFuture.completeExceptionally(new ChatInterceptionCancelledException("New interception scheduled."));
        }

        this.interceptionFutureMap.put(sourcePlayer, future);
        return future;
    }
}
