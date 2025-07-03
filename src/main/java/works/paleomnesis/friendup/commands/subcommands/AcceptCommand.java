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

public class AcceptCommand implements SubCommand {
    private final FriendUp plugin;
    private final FriendManager manager;

    public AcceptCommand(FriendUp plugin) {
        this.plugin = plugin;
        this.manager = plugin.getFriendManager();
    }

    @Override
    public String getName() {
        return "accept";
    }

    @Override
    public String getDescription() {
        return "Accept a friend request";
    }

    @Override
    public String getSyntax() {
        return "/friend accept <player>";
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
        manager.acceptRequest(from, to);
        manager.addFriend(from, to);
        String msgSelf = plugin.getMessage("friend-request-accepted").replace("%player%", target.getName());
        player.sendMessage(ColorUtil.translate(msgSelf));
        if (target.isOnline()) {
            Player online = target.getPlayer();
            if (online != null) {
                String msg = plugin.getMessage("friend-request-accepted").replace("%player%", player.getName());
                online.sendMessage(ColorUtil.translate(msg));
            }
        }
    }

    @Override
    public List<String> getSubcommandArgs(Player player, String[] args) {
        return Collections.emptyList();
    }
}
