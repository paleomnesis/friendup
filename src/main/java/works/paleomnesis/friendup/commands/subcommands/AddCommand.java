package works.paleomnesis.friendup.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import works.paleomnesis.friendup.FriendUp;
import works.paleomnesis.friendup.managers.FriendManager;
import works.paleomnesis.friendup.util.ColorUtil;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class AddCommand implements SubCommand {
    private final FriendUp plugin;
    private final FriendManager manager;

    public AddCommand(FriendUp plugin) {
        this.plugin = plugin;
        this.manager = plugin.getFriendManager();
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "Add a player";
    }

    @Override
    public String getSyntax() {
        return "/friend add <player>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(ColorUtil.translate(plugin.getMessage("usage")));
            return;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            player.sendMessage(ColorUtil.translate(plugin.getMessage("player-not-found")));
            return;
        }
        UUID self = player.getUniqueId();
        UUID other = target.getUniqueId();
        if (self.equals(other)) {
            player.sendMessage(ColorUtil.translate(plugin.getMessage("cannot-add-self")));
            return;
        }
        if (manager.areFriends(self, other)) {
            String msg = plugin.getMessage("already-friends").replace("%player%", target.getName());
            player.sendMessage(ColorUtil.translate(msg));
            return;
        }
        if (manager.hasPendingRequest(self, other)) {
            String msg = plugin.getMessage("friend-request-already").replace("%player%", target.getName());
            player.sendMessage(ColorUtil.translate(msg));
            return;
        }
        manager.sendRequest(self, other);
        String msg = plugin.getMessage("friend-request-sent").replace("%player%", target.getName());
        player.sendMessage(ColorUtil.translate(msg));
    }

    @Override
    public List<String> getSubcommandArgs(Player player, String[] args) {
        return Collections.emptyList();
    }
}
