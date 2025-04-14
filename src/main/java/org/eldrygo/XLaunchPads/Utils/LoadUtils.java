package org.eldrygo.XLaunchPads.Utils;

import org.eldrygo.XLaunchPads.Command.XLaunchPadsCommand;
import org.eldrygo.XLaunchPads.Command.XLaunchPadsTabCompleter;
import org.eldrygo.XLaunchPads.Listeners.LaunchPadListener;
import org.eldrygo.XLaunchPads.Managers.ConfigManager;
import org.eldrygo.XLaunchPads.Managers.LaunchPadsManager;
import org.eldrygo.XLaunchPads.XLaunchPads;

public class LoadUtils {
    private final XLaunchPads plugin;
    private final ConfigManager configManager;
    private final ChatUtils chatUtils;
    private final LaunchPadsManager launchPadsManager;

    public LoadUtils(XLaunchPads plugin, ConfigManager configManager, ChatUtils chatUtils, LaunchPadsManager launchPadsManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.chatUtils = chatUtils;
        this.launchPadsManager = launchPadsManager;
    }

    public void loadFeatures() {
        loadConfigFiles();
        registerListeners();
        loadCommands();
    }
    public void registerListeners() {
        plugin.getServer().getPluginManager().registerEvents(new LaunchPadListener(plugin, launchPadsManager), plugin);
    }
    public void loadConfigFiles() {
        plugin.getLogger().info("Loading config files...");
        try {
            plugin.reloadConfig();
            plugin.config = plugin.getConfig();
            plugin.getLogger().info("✅ The config.yml file has been loaded successfully.");
        } catch (Exception e) {
            plugin.getLogger().severe("❌ Failed to reload plugin configuration due to an unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
        configManager.reloadMessages();
        configManager.setPrefix(ChatUtils.formatColor(configManager.getMessageConfig().getString("prefix", "#ff902e&lx&r&lLaunchPads &cDefault Prefix &8»&r")));
    }
    public void loadCommands() {
        plugin.getCommand("xlaunchpads").setExecutor(new XLaunchPadsCommand(plugin, chatUtils, configManager, launchPadsManager));
        plugin.getCommand("xlaunchpads").setTabCompleter(new XLaunchPadsTabCompleter());
    }
}
