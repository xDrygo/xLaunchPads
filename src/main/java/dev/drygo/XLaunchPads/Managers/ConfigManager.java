package dev.drygo.XLaunchPads.Managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import dev.drygo.XLaunchPads.XLaunchPads;

import java.io.File;

public class ConfigManager {
    private final XLaunchPads plugin;
    public YamlConfiguration messagesConfig;

    public ConfigManager(XLaunchPads plugin) {
        this.plugin = plugin;
    }
    public void loadConfig() {
        try {
            plugin.saveDefaultConfig();
            plugin.reloadConfig();
            plugin.getLogger().info("✅ The config.yml file successfully loaded.");
        } catch (Exception e) {
            plugin.getLogger().severe("❌ Failed on loading config.yml: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void reloadMessages() {
        try {
            File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
            if (!messagesFile.exists()) {
                plugin.saveResource("messages.yml", false);
                plugin.getLogger().info("✅ The messages.yml file did not exist, it has been created.");
            } else {
                plugin.getLogger().info("✅ The messages.yml file has been loaded successfully.");
            }
            messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
            plugin.prefix = "#ff902e&lx&r&lLaunchPads &8»&r &cDefault Prefix &8»&r";
        } catch (Exception e) {
            plugin.getLogger().severe("❌ Failed to load messages configuration due to an unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void setPrefix(String prefix) {
        plugin.prefix = prefix;
    }
    public String getPrefix() {
        return plugin.prefix;
    }
    public FileConfiguration getMessageConfig() { return messagesConfig; }
}
