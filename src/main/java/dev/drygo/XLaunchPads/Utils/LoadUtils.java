package dev.drygo.XLaunchPads.Utils;

import dev.drygo.XLaunchPads.Command.XLaunchPadsCommand;
import dev.drygo.XLaunchPads.Command.XLaunchPadsTabCompleter;
import dev.drygo.XLaunchPads.Listeners.LaunchPadListener;
import dev.drygo.XLaunchPads.Managers.ConfigManager;
import dev.drygo.XLaunchPads.Managers.LaunchPadsManager;
import dev.drygo.XLaunchPads.XLaunchPads;

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
        configManager.loadConfig();
        configManager.reloadMessages();
        configManager.setPrefix(ChatUtils.formatColor(configManager.getMessageConfig().getString("prefix", "#ff902e&lx&r&lLaunchPads &cDefault Prefix &8»&r")));
    }
    public void loadCommands() {
        plugin.getCommand("xlaunchpads").setExecutor(new XLaunchPadsCommand(plugin, chatUtils, configManager, launchPadsManager));
        plugin.getCommand("xlaunchpads").setTabCompleter(new XLaunchPadsTabCompleter());
    }
}
