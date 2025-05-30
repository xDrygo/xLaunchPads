package dev.drygo.XLaunchPads.Command;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import dev.drygo.XLaunchPads.Managers.ConfigManager;
import dev.drygo.XLaunchPads.Managers.LaunchPadsManager;
import dev.drygo.XLaunchPads.Utils.ChatUtils;
import dev.drygo.XLaunchPads.XLaunchPads;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.List;

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
        if (args.length < 1) {
            sender.sendMessage(chatUtils.getMessage("error.usage", null));
            return true;
        }

        if (sender.hasPermission("xlaunchpads.admin") || sender.isOp()) {
            String action = args[0];
            switch (action) {
                case "reload" -> {
                    if (!sender.hasPermission("xlaunchpads.command.reload") && !sender.hasPermission("xlaunchpads.admin") && !sender.isOp()) {
                        sender.sendMessage(chatUtils.getMessage("error.no_permission", null));
                        return true;
                    }
                    handleReload(sender);
                }
                case "set" -> {
                    if (!sender.hasPermission("xlaunchpads.command.set") && !sender.hasPermission("xlaunchpads.admin") && !sender.isOp()) {
                        sender.sendMessage(chatUtils.getMessage("error.no_permission", null));
                        return true;
                    }
                    handleSet(sender);
                }
                case "info" -> {
                    if (!sender.hasPermission("xlaunchpads.command.info") && !sender.hasPermission("xlaunchpads.admin") && !sender.isOp()) {
                        sender.sendMessage(chatUtils.getMessage("error.no_permission", null));
                        return true;
                    }
                    infoXLaunchPads(sender);
                }
                case "help" -> {
                    if (!sender.hasPermission("xlaunchpads.command.help") && !sender.hasPermission("xlaunchpads.admin") && !sender.isOp()) {
                        sender.sendMessage(chatUtils.getMessage("error.no_permission", null));
                        return true;
                    }
                    List<String> helpMessage = configManager.getMessageConfig().getStringList("command.help");
                    for (String line : helpMessage) {
                        sender.sendMessage(ChatUtils.formatColor(line));
                    }
                    return true;
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
            return;
        }
        if (launchPadsManager.isLaunchpadRegistered(block.getLocation())) {
            player.sendMessage(chatUtils.getMessage("command.set.already_exists", player)); // commands.set.already_exists
            return; // Salir si ya está registrada
        }

        assert block != null;
        launchPadsManager.addLaunchpadLocation(block.getLocation());
        player.sendMessage(chatUtils.getMessage("command.set.success", player)); // commands.set.success
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
    private void infoXLaunchPads(CommandSender sender) {
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("&8                          #ff902e&lx&r&f&lLaunchPads &8» &r&fInfo"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l                           ᴍᴀᴅᴇ ʙʏ"));
        sender.sendMessage(ChatUtils.formatColor("&f                           Drygo #707070» &7&o(@eldrygo)"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l                  ʀᴜɴɴɪɴɢ ᴘʟᴜɢɪɴ ᴠᴇʀꜱɪᴏɴ"));
        sender.sendMessage(ChatUtils.formatColor("&f                                    " + plugin.version));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l               ᴅʀʏɢᴏ'ꜱ ɴᴏᴛᴇ ᴏꜰ ᴛʜᴇ ᴠᴇʀꜱɪᴏɴ"));
        sender.sendMessage(ChatUtils.formatColor("&f  #FFFAAB        Welcome to xLaunchPads! I made this plugin because"));
        sender.sendMessage(ChatUtils.formatColor("&f  #FFFAAB       this was a cosmetic I added to a event lobby so I maked"));
        sender.sendMessage(ChatUtils.formatColor("&f  #FFFAAB                  it more complete so here is, enjoy."));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
    }
}
