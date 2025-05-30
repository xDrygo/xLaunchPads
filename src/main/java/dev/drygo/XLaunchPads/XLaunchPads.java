package dev.drygo.XLaunchPads;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import dev.drygo.XLaunchPads.Managers.ConfigManager;
import dev.drygo.XLaunchPads.Managers.LaunchPadsManager;
import dev.drygo.XLaunchPads.Utils.ChatUtils;
import dev.drygo.XLaunchPads.Utils.LoadUtils;
import dev.drygo.XLaunchPads.Utils.LogsUtils;

public class XLaunchPads extends JavaPlugin {
    public FileConfiguration config;
    public String prefix;
    private LogsUtils logsUtils;
    public String version;

    public void onEnable() {
        version = getDescription().getVersion();
        this.logsUtils = new LogsUtils(this);
        ConfigManager configManager = new ConfigManager(this);
        ChatUtils chatUtils = new ChatUtils(configManager, this);
        LaunchPadsManager launchPadsManager = new LaunchPadsManager(this);
        LoadUtils loadUtils = new LoadUtils(this, configManager, chatUtils, launchPadsManager);
        loadUtils.loadFeatures();
        logsUtils.sendStartupMessage();
    }
    public void onDisable() {
        logsUtils.sendShutdownMessage();
    }
}
