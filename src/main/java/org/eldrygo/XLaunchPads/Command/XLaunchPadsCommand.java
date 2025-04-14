package org.eldrygo.XLaunchPads.Command;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.eldrygo.XLaunchPads.Managers.ConfigManager;
import org.eldrygo.XLaunchPads.Managers.LaunchPadsManager;
import org.eldrygo.XLaunchPads.Utils.ChatUtils;
import org.eldrygo.XLaunchPads.XLaunchPads;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class XLaunchPadsCommand implements CommandExecutor {
    private final XLaunchPads plugin;
    private final ChatUtils chatUtils;
    private final ConfigManager configManager;
    private final LaunchPadsManager launchPadsManager;

    public XLaunchPadsCommand(XLaunchPads plugin, ChatUtils chatUtils, ConfigManager configManager, LaunchPadsManager launchPadsManager) {
        this.chatUtils = chatUtils;
        this.plugin = plugin;
        this.configManager = configManager;
        this.launchPadsManager = launchPadsManager;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String action = args[0];
        if (sender.hasPermission("xlaunchpads.admin")) {
            switch (action) {
                case "reload" -> {
                    handleReload(sender);
                }
                case "set" -> {
                    handleSet(sender);
                }
            }
        }
        return false;
    }

    private void handleSet(CommandSender sender) {
        Player player = (Player) sender;

        Block block = player.getTargetBlockExact(5);
        if (block == null || !block.getType().name().endsWith("_PRESSURE_PLATE")) {
            player.sendMessage(chatUtils.getMessage("command.set.not_a_pressure_plate", player)); // commands.set.not_a_pressure_plate
        }

        launchPadsManager.addLaunchpadLocation(block.getLocation());
        player.sendMessage(chatUtils.getMessage("command.set.sucesss", player)); // commands.set.success
    }

    private void handleReload(CommandSender sender) {
        try {
            try {
                plugin.reloadConfig();
                plugin.config = plugin.getConfig();
                plugin.getLogger().info("✅ The config.yml file has been reloaded successfully.");
            } catch (Exception e) {
                plugin.getLogger().severe("❌ Failed to reload plugin configuration due to an unexpected error: " + e.getMessage());
                e.printStackTrace();
            }
            try {
                File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
                if (!messagesFile.exists()) {
                    plugin.saveResource("messages.yml", false);
                    plugin.getLogger().info("✅ The messages.yml file did not exist, it has been created.");
                } else {
                    plugin.getLogger().info("✅ The messages.yml file has been reloaded successfully.");
                }
                configManager.messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
                configManager.setPrefix(ChatUtils.formatColor(configManager.getMessageConfig().getString("prefix", "#ff902e&lx&r&lLaunchPads &cDefault Prefix &8»&r")));
            } catch (Exception e) {
                plugin.getLogger().severe("❌ Failed to load messages configuration due to an unexpected error: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            sender.sendMessage(chatUtils.getMessage("command.reload.error", (Player) sender));
            return;
        }
        sender.sendMessage(chatUtils.getMessage("command.reload.success", (Player) sender));
    }
}
