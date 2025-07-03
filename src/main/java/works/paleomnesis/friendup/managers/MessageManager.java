package works.paleomnesis.friendup.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class MessageManager {
    private static FileConfiguration messages;

    private MessageManager() {
    }

    public static void init(JavaPlugin plugin) {
        plugin.saveResource("messages.yml", false);
        messages = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));
    }

    public static String get(String key) {
        return messages.getString(key, key);
    }
}
