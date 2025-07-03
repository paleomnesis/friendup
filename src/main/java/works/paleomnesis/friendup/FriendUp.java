package works.paleomnesis.friendup;

import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import works.paleomnesis.friendup.commands.FriendCommand;
import works.paleomnesis.friendup.managers.FriendManager;
import works.paleomnesis.friendup.managers.MessageManager;
import works.paleomnesis.friendup.listeners.PlayerConnectionListener;

import java.io.File;

public final class FriendUp extends JavaPlugin {
    private static FriendUp instance;

    private FileConfiguration messages;
    private FriendManager friendManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        MessageManager.init(this);
        messages = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "messages.yml"));

        friendManager = new FriendManager(this);
        friendManager.initializeDatabase();

        PluginCommand command = getCommand("friend");
        if (command != null) {
            FriendCommand friendCommand = new FriendCommand(this);
            command.setExecutor(friendCommand);
            command.setTabCompleter(friendCommand);
        }
        var pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerConnectionListener(this), this);
        getLogger().info("FriendUp enabled");
    }

    @Override
    public void onDisable() {
        if (friendManager != null) {
            friendManager.close();
        }
        instance = null;
        getLogger().info("FriendUp disabled");
    }

    public String getMessage(String key) {
        return messages.getString(key, key);
    }

    public FriendManager getFriendManager() {
        return friendManager;
    }
}
