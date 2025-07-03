package works.paleomnesis.friendup.managers;

import org.bukkit.entity.Player;
import works.paleomnesis.friendup.FriendUp;
import works.paleomnesis.friendup.commands.subcommands.*;
import works.paleomnesis.friendup.util.ColorUtil;

import java.util.*;

public class CommandManager {
    private final FriendUp plugin;
    private final Map<String, SubCommand> commands = new HashMap<>();

    public CommandManager(FriendUp plugin) {
        this.plugin = plugin;
        register(new AddCommand(plugin));
        register(new AcceptCommand(plugin));
        register(new DenyCommand(plugin));
        register(new RemoveCommand(plugin));
        register(new ListCommand(plugin));
        register(new MsgCommand(plugin));
    }

    private void register(SubCommand subCommand) {
        commands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    public void perform(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(ColorUtil.translate(plugin.getMessage("usage")));
            return;
        }
        SubCommand sub = commands.get(args[0].toLowerCase());
        if (sub == null) {
            player.sendMessage(ColorUtil.translate(plugin.getMessage("usage")));
            return;
        }
        sub.perform(player, Arrays.copyOfRange(args, 1, args.length));
    }

    public List<String> getSubcommandArgs(Player player, String[] args) {
        if (args.length == 1) {
            return commands.keySet().stream()
                    .filter(name -> name.startsWith(args[0].toLowerCase()))
                    .sorted()
                    .toList();
        }
        SubCommand sub = commands.get(args[0].toLowerCase());
        if (sub != null) {
            return sub.getSubcommandArgs(player, Arrays.copyOfRange(args, 1, args.length));
        }
        return Collections.emptyList();
    }
}
