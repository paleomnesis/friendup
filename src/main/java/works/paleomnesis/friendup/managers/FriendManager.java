package works.paleomnesis.friendup.managers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FriendManager {
    private static final String DB_NAME = "friendup.db";

    private final JavaPlugin plugin;
    private Connection connection;

    public FriendManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("Failed to close database: " + e.getMessage());
            }
        }
    }

    public void initializeDatabase() {
        try {
            plugin.getDataFolder().mkdirs();
            File dbFile = new File(plugin.getDataFolder(), DB_NAME);
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS players (" +
                        "uuid TEXT PRIMARY KEY, " +
                        "name TEXT NOT NULL, " +
                        "first_seen INTEGER NOT NULL, " +
                        "last_seen INTEGER NOT NULL)");
                stmt.execute("CREATE TABLE IF NOT EXISTS friendships (" +
                        "player_a TEXT NOT NULL, " +
                        "player_b TEXT NOT NULL, " +
                        "UNIQUE(player_a, player_b))");
                stmt.execute("CREATE TABLE IF NOT EXISTS friend_requests (" +
                        "sender_uuid TEXT NOT NULL, " +
                        "receiver_uuid TEXT NOT NULL, " +
                        "sent_at INTEGER NOT NULL, " +
                        "expires_at INTEGER, " +
                        "UNIQUE(sender_uuid, receiver_uuid))");
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to initialize database: " + e.getMessage());
        }
    }

    public void registerPlayer(UUID uuid, String name) {
        String sql = "INSERT INTO players(uuid, name, first_seen, last_seen) " +
                "VALUES(?, ?, strftime('%s','now'), strftime('%s','now')) " +
                "ON CONFLICT(uuid) DO UPDATE SET name=excluded.name";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to register player: " + e.getMessage());
        }
    }

    public void updateLastSeen(UUID uuid) {
        String sql = "UPDATE players SET last_seen=strftime('%s','now') WHERE uuid=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to update last seen: " + e.getMessage());
        }
    }

    public boolean areFriends(UUID a, UUID b) {
        String first = a.toString();
        String second = b.toString();
        if (first.compareTo(second) > 0) {
            String tmp = first;
            first = second;
            second = tmp;
        }
        String sql = "SELECT 1 FROM friendships WHERE player_a=? AND player_b=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, first);
            ps.setString(2, second);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to check friendship: " + e.getMessage());
            return false;
        }
    }

    public void addFriend(UUID a, UUID b) {
        String first = a.toString();
        String second = b.toString();
        if (first.compareTo(second) > 0) {
            String tmp = first;
            first = second;
            second = tmp;
        }
        String sql = "INSERT OR IGNORE INTO friendships(player_a, player_b) VALUES(?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, first);
            ps.setString(2, second);
            ps.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to add friend: " + e.getMessage());
        }
    }

    public void removeFriend(UUID a, UUID b) {
        String first = a.toString();
        String second = b.toString();
        if (first.compareTo(second) > 0) {
            String tmp = first;
            first = second;
            second = tmp;
        }
        String sql = "DELETE FROM friendships WHERE player_a=? AND player_b=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, first);
            ps.setString(2, second);
            ps.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to remove friend: " + e.getMessage());
        }
    }

    public List<UUID> getFriends(UUID uuid) {
        List<UUID> friends = new ArrayList<>();
        String sql = "SELECT player_a, player_b FROM friendships WHERE player_a=? OR player_b=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String id = uuid.toString();
            ps.setString(1, id);
            ps.setString(2, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String a = rs.getString("player_a");
                    String b = rs.getString("player_b");
                    String other = id.equals(a) ? b : a;
                    friends.add(UUID.fromString(other));
                }
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to get friends: " + e.getMessage());
        }
        return friends;
    }

    public void sendRequest(UUID from, UUID to) {
        String sql = "INSERT OR IGNORE INTO friend_requests(sender_uuid, receiver_uuid, sent_at, expires_at) " +
                "VALUES(?, ?, strftime('%s','now'), NULL)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, from.toString());
            ps.setString(2, to.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to send friend request: " + e.getMessage());
        }
    }

    public boolean hasPendingRequest(UUID from, UUID to) {
        String sql = "SELECT 1 FROM friend_requests WHERE sender_uuid=? AND receiver_uuid=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, from.toString());
            ps.setString(2, to.toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to check pending request: " + e.getMessage());
            return false;
        }
    }

    public void acceptRequest(UUID from, UUID to) {
        String delete = "DELETE FROM friend_requests WHERE sender_uuid=? AND receiver_uuid=?";
        String first = from.toString();
        String second = to.toString();
        if (first.compareTo(second) > 0) {
            String tmp = first;
            first = second;
            second = tmp;
        }
        String insert = "INSERT OR IGNORE INTO friendships(player_a, player_b) VALUES(?, ?)";
        try (PreparedStatement del = connection.prepareStatement(delete);
             PreparedStatement ins = connection.prepareStatement(insert)) {
            del.setString(1, from.toString());
            del.setString(2, to.toString());
            del.executeUpdate();

            ins.setString(1, first);
            ins.setString(2, second);
            ins.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to accept request: " + e.getMessage());
        }
    }

    public void denyRequest(UUID from, UUID to) {
        String sql = "DELETE FROM friend_requests WHERE sender_uuid=? AND receiver_uuid=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, from.toString());
            ps.setString(2, to.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to deny request: " + e.getMessage());
        }
    }
}
