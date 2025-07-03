package works.paleomnesis.friendup.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import works.paleomnesis.friendup.FriendUp;
import works.paleomnesis.friendup.managers.CommandManager;
import works.paleomnesis.friendup.util.ColorUtil;

import java.util.Collections;
import java.util.List;

public class FriendCommand implements CommandExecutor, TabCompleter {
    private final FriendUp plugin;
    private final CommandManager manager;

    public FriendCommand(FriendUp plugin) {
        this.plugin = plugin;
        this.manager = new CommandManager(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtil.translate(plugin.getMessage("player-only")));
            return true;
        }
        manager.perform(player, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player)) {
            return Collections.emptyList();
        }
        return manager.getSubcommandArgs(player, args);
    }
}
