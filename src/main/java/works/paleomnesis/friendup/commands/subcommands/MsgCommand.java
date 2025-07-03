package works.paleomnesis.friendup.commands.subcommands;

import org.bukkit.entity.Player;
import works.paleomnesis.friendup.FriendUp;
import works.paleomnesis.friendup.util.ColorUtil;

import java.util.Collections;
import java.util.List;

public class MsgCommand implements SubCommand {
    private final FriendUp plugin;

    public MsgCommand(FriendUp plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "msg";
    }

    @Override
    public String getDescription() {
        return "Message a friend";
    }

    @Override
    public String getSyntax() {
        return "/friend msg <player> <message>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ColorUtil.translate(plugin.getMessage("usage")));
            return;
        }
        String target = args[0];
        String message = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        player.sendMessage(ColorUtil.translate(plugin.getMessage("prefix") + "To " + target + ": " + message));
    }

    @Override
    public List<String> getSubcommandArgs(Player player, String[] args) {
        return Collections.emptyList();
    }
}
