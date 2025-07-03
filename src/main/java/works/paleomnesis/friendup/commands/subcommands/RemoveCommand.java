package works.paleomnesis.friendup.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import works.paleomnesis.friendup.FriendUp;
import works.paleomnesis.friendup.managers.FriendManager;
import works.paleomnesis.friendup.util.ColorUtil;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RemoveCommand implements SubCommand {
    private final FriendUp plugin;
    private final FriendManager manager;

    public RemoveCommand(FriendUp plugin) {
        this.plugin = plugin;
        this.manager = plugin.getFriendManager();
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "Remove a player";
    }

    @Override
    public String getSyntax() {
        return "/friend remove <player>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(ColorUtil.translate(plugin.getMessage("usage")));
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            player.sendMessage(ColorUtil.translate(plugin.getMessage("player-not-found")));
            return;
        }
        UUID self = player.getUniqueId();
        UUID other = target.getUniqueId();
        if (!manager.areFriends(self, other)) {
            String msg = plugin.getMessage("not-friends").replace("%player%", target.getName());
            player.sendMessage(ColorUtil.translate(msg));
            return;
        }
        manager.removeFriend(self, other);
        String msgSelf = plugin.getMessage("friend-removed").replace("%player%", target.getName());
        player.sendMessage(ColorUtil.translate(msgSelf));
        if (target.isOnline()) {
            Player online = target.getPlayer();
            if (online != null) {
                String msg = plugin.getMessage("friend-removed").replace("%player%", player.getName());
                online.sendMessage(ColorUtil.translate(msg));
            }
        }
    }

    @Override
    public List<String> getSubcommandArgs(Player player, String[] args) {
        return Collections.emptyList();
    }
}
