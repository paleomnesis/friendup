package works.paleomnesis.friendup.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import works.paleomnesis.friendup.FriendUp;
import works.paleomnesis.friendup.managers.FriendManager;
import works.paleomnesis.friendup.util.ColorUtil;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ListCommand implements SubCommand {
    private final FriendUp plugin;
    private final FriendManager manager;

    public ListCommand(FriendUp plugin) {
        this.plugin = plugin;
        this.manager = plugin.getFriendManager();
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "List friends";
    }

    @Override
    public String getSyntax() {
        return "/friend list";
    }

    @Override
    public void perform(Player player, String[] args) {
        List<UUID> friends = manager.getFriends(player.getUniqueId());
        if (friends.isEmpty()) {
            player.sendMessage(ColorUtil.translate(plugin.getMessage("friend-list-empty")));
            return;
        }
        for (UUID id : friends) {
            String name = Bukkit.getOfflinePlayer(id).getName();
            String msg = plugin.getMessage("friend-list-format").replace("%player%", name == null ? id.toString() : name);
            player.sendMessage(ColorUtil.translate(msg));
        }
    }

    @Override
    public List<String> getSubcommandArgs(Player player, String[] args) {
        return Collections.emptyList();
    }
}
