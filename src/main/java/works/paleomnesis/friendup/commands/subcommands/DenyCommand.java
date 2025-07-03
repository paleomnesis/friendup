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

public class DenyCommand implements SubCommand {
    private final FriendUp plugin;
    private final FriendManager manager;

    public DenyCommand(FriendUp plugin) {
        this.plugin = plugin;
        this.manager = plugin.getFriendManager();
    }

    @Override
    public String getName() {
        return "deny";
    }

    @Override
    public String getDescription() {
        return "Deny a friend request";
    }

    @Override
    public String getSyntax() {
        return "/friend deny <player>";
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
        UUID from = target.getUniqueId();
        UUID to = player.getUniqueId();
        if (!manager.hasPendingRequest(from, to)) {
            String msg = plugin.getMessage("friend-request-none").replace("%player%", target.getName());
            player.sendMessage(ColorUtil.translate(msg));
            return;
        }
        manager.denyRequest(from, to);
        String msgSelf = plugin.getMessage("friend-request-denied").replace("%player%", target.getName());
        player.sendMessage(ColorUtil.translate(msgSelf));
    }

    @Override
    public List<String> getSubcommandArgs(Player player, String[] args) {
        return Collections.emptyList();
    }
}
