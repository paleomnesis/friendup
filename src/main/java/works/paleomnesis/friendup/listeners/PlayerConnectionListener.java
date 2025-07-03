package works.paleomnesis.friendup.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import works.paleomnesis.friendup.FriendUp;
import works.paleomnesis.friendup.managers.FriendManager;
import works.paleomnesis.friendup.util.ColorUtil;

import java.util.List;
import java.util.UUID;

public class PlayerConnectionListener implements Listener {
    private final FriendUp plugin;
    private final FriendManager manager;

    public PlayerConnectionListener(FriendUp plugin) {
        this.plugin = plugin;
        this.manager = plugin.getFriendManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        String name = event.getPlayer().getName();

        manager.registerPlayer(uuid, name);
        manager.updateLastSeen(uuid);

        notifyFriends(uuid, plugin.getMessage("friend-join-notify"), name);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        String name = event.getPlayer().getName();

        manager.updateLastSeen(uuid);

        notifyFriends(uuid, plugin.getMessage("friend-quit-notify"), name);
    }

    private void notifyFriends(UUID uuid, String messageTemplate, String playerName) {
        List<UUID> friends = manager.getFriends(uuid);
        String message = ColorUtil.translate(messageTemplate.replace("%player%", playerName));
        for (UUID id : friends) {
            var player = Bukkit.getPlayer(id);
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }
}
