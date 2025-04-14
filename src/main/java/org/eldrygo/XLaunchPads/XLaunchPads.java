package org.eldrygo.XLaunchPads;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.eldrygo.XLaunchPads.Managers.ConfigManager;
import org.eldrygo.XLaunchPads.Managers.LaunchPadsManager;
import org.eldrygo.XLaunchPads.Utils.ChatUtils;
import org.eldrygo.XLaunchPads.Utils.LoadUtils;
import org.eldrygo.XLaunchPads.Utils.LogsUtils;

public class XLaunchPads extends JavaPlugin {
    public FileConfiguration config;
    public String prefix;
    private LogsUtils logsUtils;

    public void onEnable() {
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
