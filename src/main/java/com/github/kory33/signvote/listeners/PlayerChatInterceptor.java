package com.github.kory33.signvote.listeners;

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
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerExit(PlayerQuitEvent event) {
        this.cancelAnyInterception(event.getPlayer(), "Player has quit.");
    }

    /**
     * Cancel chat interception task associated with a given player, if there exists any.
     * @param player
     * @param cancelReason
     */
    public void cancelAnyInterception(Player player, String cancelReason) {
        CompletableFuture<String> future = this.interceptionFutureMap.remove(player);

        if (future == null) {
            return;
        }

        future.completeExceptionally(new ChatInterceptionCancelledException(cancelReason));
    }

    /**
     * Intercept the first message sent by the given player.
     * @param sourcePlayer
     * @return
     */
    public CompletableFuture<String> interceptFirstMessageFrom(Player sourcePlayer) {
        CompletableFuture<String> future = new CompletableFuture<>();

        // cancel previous interception
        this.cancelAnyInterception(sourcePlayer, "New interception scheduled.");

        this.interceptionFutureMap.put(sourcePlayer, future);
        return future;
    }
}