package org.pl.vanishplugin;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommandHandler implements CommandExecutor {
    private final VanishPlugin plugin;
    private final Set<Player> vanishedPlayers = new HashSet<>();
    private static final double ROTATION_SPEED = 0.7853981633974483;
    private static final double RADIUS_STEP = 0.1;



    public VanishCommandHandler(VanishPlugin plugin) {
        this.plugin = plugin;
        //Bukkit.getPluginManager().registerEvents(new VanishListenerHandler(plugin), plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageManager.getInstance().getMessage("console-use"));
            return true;
        } else {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("vanish")) {
                if (args.length == 0) {
                    toggleInvisibility(player);
                } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                    reloadConfig(player);
                } else if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
                    sendHelpMessage(player);
                } else if (args.length == 1 && args[0].equalsIgnoreCase("interact")) {
                    toggleUse(player);
                } else if (args.length == 1 && args[0].equalsIgnoreCase("pickup")) {
                    togglePickup(player);
                } else {
                    player.sendMessage(MessageManager.getInstance().getMessage("invalid-command"));
                }
            }

            return true;
        }
    }
    //
    //TOGGLE SECTION
    //
    private void toggleInvisibility(Player player) {
        if (!player.hasPermission("vanish.vanish")) {
            player.sendMessage(MessageManager.getInstance().getMessage("no-permission"));
            return;
        }

        boolean isVanished = vanishedPlayers.contains(player);
        if (!isVanished) {
            player.sendMessage(MessageManager.getInstance().getMessage("vanish-enabled"));
            vanishedPlayers.add(player);
            plugin.getDisabledPickupPlayers().add(player);
            player.sendMessage(MessageManager.getInstance().getMessage("pickup-disabled"));
            plugin.getDisabledUsePlayers().add(player);
            player.sendMessage(MessageManager.getInstance().getMessage("interact-disabled"));

            player.setInvulnerable(true);

            plugin.updateVisibility(player, true);

        } else {
            player.sendMessage(MessageManager.getInstance().getMessage("vanish-disabled"));
            vanishedPlayers.remove(player);
            plugin.getDisabledPickupPlayers().remove(player);
            player.sendMessage(MessageManager.getInstance().getMessage("pickup-enabled"));
            plugin.getDisabledUsePlayers().remove(player);
            player.sendMessage(MessageManager.getInstance().getMessage("interact-enabled"));

            player.setInvulnerable(false);

            plugin.updateVisibility(player, false);
        }

        createSpiralParticle(player);
    }

    private void togglePickup(Player player) {
        if (!player.hasPermission("vanish.pickup")) {
            player.sendMessage(MessageManager.getInstance().getMessage("no-permission"));
            return;
        }

        if (vanishedPlayers.contains(player)) {
            if (plugin.getDisabledPickupPlayers().contains(player)) {
                plugin.getDisabledPickupPlayers().remove(player);
                player.sendMessage(MessageManager.getInstance().getMessage("pickup-enabled"));
            } else {
                plugin.getDisabledPickupPlayers().add(player);
                player.sendMessage(MessageManager.getInstance().getMessage("pickup-disabled"));
            }
        } else {
            player.sendMessage(MessageManager.getInstance().getMessage("vanish-not-enabled"));
        }
    }

    private void toggleUse(Player player) {
        if (!player.hasPermission("vanish.interact")) {
            player.sendMessage(MessageManager.getInstance().getMessage("no-permission"));
            return;
        }

        if (vanishedPlayers.contains(player)) {
            if (plugin.getDisabledUsePlayers().contains(player)) {
                plugin.getDisabledUsePlayers().remove(player);
                player.sendMessage(MessageManager.getInstance().getMessage("interact-enabled"));
            } else {
                plugin.getDisabledUsePlayers().add(player);
                player.sendMessage(MessageManager.getInstance().getMessage("interact-disabled"));
            }
        } else {
            player.sendMessage(MessageManager.getInstance().getMessage("vanish-not-enabled"));
        }
    }
    //
    //
    //RELOAD MESSAGES.YML
    private void reloadConfig(Player player) {
        if (player.hasPermission("vanish.reload")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9Reloading messages.yml"));
            try {
                MessageManager.getInstance().reloadMessagesConfig(plugin.getDataFolder());
                MessageManager.getInstance().saveMessagesConfig(plugin.getDataFolder());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9messages.yml reloaded and saved."));
            } catch (Exception e) {
                player.sendMessage(MessageManager.getInstance().getMessage("error-loading-config"));
                e.printStackTrace();
            }
        } else {
            player.sendMessage(MessageManager.getInstance().getMessage("no-permission"));
        }
    }

    //
    //
    private void sendHelpMessage(Player player) {
        String[] helpMessageKeys = new String[]{"prefix", "info1", "info2", "info3", "prefix-footer"};
        for (String messageKey : helpMessageKeys) {
            String message = MessageManager.getInstance().getMessage(messageKey);
            if (message != null) {
                player.sendMessage(message);
            } else {
                player.sendMessage("Error: messageKey sendHelpMessage ( " + messageKey + " )");
                player.sendMessage("If you got this error, please report it on SpigotMC: [_Avzti]");
            }
        }
    }
    //
    //USELESS
    private void createSpiralParticle(Player player) {
        double radius = 0.0;
        double height = 0.0;
        double angle = 0.0;

        for (int i = 0; i < 100; ++i) {
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            player.spawnParticle(Particle.CLOUD, player.getLocation().getX() + x, player.getLocation().getY() + height, player.getLocation().getZ() + z, 1);
            radius += RADIUS_STEP;
            height += 0.05;
            angle += ROTATION_SPEED;
        }
    }
}
