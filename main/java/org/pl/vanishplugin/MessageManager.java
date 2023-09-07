package org.pl.vanishplugin;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;

public class MessageManager {
    private static MessageManager instance;
    private FileConfiguration messagesConfig;

    private MessageManager(FileConfiguration messagesConfig) {
        this.messagesConfig = messagesConfig;
    }

    public static void initialize(FileConfiguration messagesConfig) {
        if (instance == null) {
            instance = new MessageManager(messagesConfig);
        }
    }

    public static MessageManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MessageManager must be initialized first.");
        } else {
            return instance;
        }
    }

    public void setMessagesConfig(FileConfiguration config) {
        this.messagesConfig = config;
    }

    public FileConfiguration getMessagesConfig() {
        return this.messagesConfig;
    }

    public String getMessage(String key) {
        String message = this.messagesConfig.getString("messages." + key);
        if (message != null) {
            // Coloring all messages
            return ChatColor.translateAlternateColorCodes('&', message);
        }
        return null;
    }

    public void saveMessagesConfig(File dataFolder) {
        File messagesFile = new File(dataFolder, "messages.yml");
        try {
            this.messagesConfig.save(messagesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadMessagesConfig(File dataFolder) throws IOException, InvalidConfigurationException {
        File messagesFile = new File(dataFolder, "messages.yml");
        if (!messagesFile.exists()) {
            // Create new file messages.yml
            messagesFile.getParentFile().mkdirs();
            messagesFile.createNewFile();
        }
        this.messagesConfig.load(messagesFile);
    }
}