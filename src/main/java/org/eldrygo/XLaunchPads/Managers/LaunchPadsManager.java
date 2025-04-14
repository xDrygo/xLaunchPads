package org.eldrygo.XLaunchPads.Managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.eldrygo.XLaunchPads.XLaunchPads;

import java.util.ArrayList;
import java.util.List;

public class LaunchPadsManager {
    private XLaunchPads plugin;
    private FileConfiguration config = plugin.getConfig();

    public LaunchPadsManager(XLaunchPads plugin) {
        this.plugin = plugin;
    }

    public void addLaunchpadLocation(Location location) {
        List<String> list = config.getStringList("launchpads");
        list.add(serializeLocation(location));
        config.set("launchpads", list);
        plugin.saveConfig();
    }

    public List<Location> getLaunchpadLocations() {
        List<Location> locations = new ArrayList<>();
        for (String s : config.getStringList("launchpads")) {
            locations.add(deserializeLocation(s));
        }
        return locations;
    }

    private String serializeLocation(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    private Location deserializeLocation(String s) {
        String[] parts = s.split(",");
        World world = Bukkit.getWorld(parts[0]);
        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);
        int z = Integer.parseInt(parts[3]);
        return new Location(world, x, y, z);
    }
}
