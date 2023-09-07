package org.pl.vanishplugin;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class VanishPlugin extends JavaPlugin {
    private final Set<Player> vanishedPlayers = new HashSet<>();
    private final Set<Player> disabledPickupPlayers = new HashSet<>();
    private final Set<Player> disabledUsePlayers = new HashSet<>();
    private FileConfiguration messagesConfig;
    private Server server;

    public VanishPlugin() {
    }

    public Set<Player> getVanishedPlayers() {
        return this.vanishedPlayers;
    }

    public Set<Player> getDisabledUsePlayers() {
        return this.disabledUsePlayers;
    }

    public Set<Player> getDisabledPickupPlayers() {
        return this.disabledPickupPlayers;
    }

    public FileConfiguration getMessagesConfig() {
        return this.messagesConfig;
    }

    @Override
    public void onEnable() {
        this.server = this.getServer();
        VanishCommandHandler commandHandler = new VanishCommandHandler(this);
        this.getCommand("vanish").setExecutor(commandHandler);
        this.getCommand("vanish").setTabCompleter(new TabComplete(this));

        this.loadOrCreateMessagesConfig();
        MessageManager.initialize(this.messagesConfig);

        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new VanishListenerHandler(this), this);

    }

    public Server getBukkitServer() {
        return this.server;
    }

    private void loadOrCreateMessagesConfig() {
        File dataFolder = this.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File messagesFile = new File(this.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            this.saveResource("messages.yml", false);
        }

        this.messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }


    public void updateVisibility(Player player, boolean isVanished) {
        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
            if (isVanished && !onlinePlayer.hasPermission("vanish.see")) {
                onlinePlayer.hidePlayer(this, player);
            } else {
                onlinePlayer.showPlayer(this, player);
            }
        }
    }
}
