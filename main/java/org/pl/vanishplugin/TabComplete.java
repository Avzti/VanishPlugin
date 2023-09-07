package org.pl.vanishplugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.plugin.Plugin;

public class TabComplete implements Listener, TabCompleter {
    private final Plugin plugin;

    public TabComplete(Plugin plugin) {
        this.plugin = plugin;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completions = new ArrayList();
        if (cmd.getName().equalsIgnoreCase("vanish")) {
            if (args.length == 1) {
                if (sender.hasPermission("vanish.reload")) {
                    completions.add("reload");
                }
                if (sender.hasPermission("vanish.vanish")) {
                    completions.add("help");
                }
                if (sender.hasPermission("vanish.use")) {
                    completions.add("interact");
                }
                if (sender.hasPermission("vanish.pickup")) {
                    completions.add("pickup");
                }
            }
        }

        completions.removeIf((completion) -> {
            Player player = Bukkit.getPlayerExact(completion);
            return player != null && this.plugin instanceof VanishPlugin && ((VanishPlugin)this.plugin).getVanishedPlayers().contains(player);
        });
        return completions;
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        String buffer = event.getBuffer();
        if (buffer.startsWith("/")) {
            String[] args = buffer.substring(1).split(" ");
            if (args.length != 0) {
                String cmdName = args[0].toLowerCase();
                CommandMap commandMap = this.getCommandMap();
                if (commandMap != null) {
                    Command command = commandMap.getCommand(cmdName);
                    if (command != null) {
                        List<String> completions = event.getCompletions();
                        completions.removeIf((completion) -> {
                            Player player = Bukkit.getPlayerExact(completion);
                            return player != null && this.plugin instanceof VanishPlugin && ((VanishPlugin)this.plugin).getVanishedPlayers().contains(player);
                        });
                    }
                }
            }
        }
    }

    private CommandMap getCommandMap() {
        try {
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            return (CommandMap)bukkitCommandMap.get(Bukkit.getServer());
        } catch (IllegalAccessException | NoSuchFieldException var2) {
            var2.printStackTrace();
            return null;
        }
    }
}
