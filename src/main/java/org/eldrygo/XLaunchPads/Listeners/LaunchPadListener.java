package org.eldrygo.XLaunchPads.Listeners;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.eldrygo.XLaunchPads.Managers.LaunchPadsManager;
import org.eldrygo.XLaunchPads.XLaunchPads;

import java.util.List;
import java.util.Map;

public class LaunchPadListener implements Listener {

    private final XLaunchPads plugin;
    private final LaunchPadsManager launchPadsManager;

    public LaunchPadListener(XLaunchPads plugin, LaunchPadsManager launchPadsManager) {
        this.plugin = plugin;
        this.launchPadsManager = launchPadsManager;
    }

    @EventHandler
    public void onPlayerStep(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) return;
        if (event.getClickedBlock() == null) return;

        Location stepped = event.getClickedBlock().getLocation();
        for (Location loc : launchPadsManager.getLaunchpadLocations()) {
            if (stepped.equals(loc)) {
                Player player = event.getPlayer();

                Vector dir = player.getLocation().getDirection().normalize();
                double horizontal = plugin.getConfig().getDouble("launch-power.horizontal", 2.0);
                double vertical = plugin.getConfig().getDouble("launch-power.vertical", 1.0);

                Vector velocity = dir.multiply(horizontal);
                velocity.setY(vertical);
                player.setVelocity(velocity);

                // 🔊 Múltiples sonidos
                if (plugin.getConfig().getBoolean("effects.sound.enabled", true)) {
                    List<Map<?, ?>> soundConfigs = plugin.getConfig().getMapList("effects.sound.sounds");

                    for (Map<?, ?> soundConfig : soundConfigs) {
                        String soundName = String.valueOf(soundConfig.get("type")).toUpperCase();
                        double volume = soundConfig.containsKey("volume") ? ((Number) soundConfig.get("volume")).doubleValue() : 1.0;
                        double pitch = soundConfig.containsKey("pitch") ? ((Number) soundConfig.get("pitch")).doubleValue() : 1.0;

                        try {
                            Sound sound = Sound.valueOf(soundName);
                            player.playSound(player.getLocation(), sound, (float) volume, (float) pitch);
                        } catch (IllegalArgumentException e) {
                            plugin.getLogger().warning("Invalid sound from config: " + soundName);
                        }
                    }
                }

                // 🌟 Múltiples partículas
                if (plugin.getConfig().getBoolean("effects.particles.enabled", true)) {
                    List<String> particles = plugin.getConfig().getStringList("effects.particles.types");
                    int count = plugin.getConfig().getInt("effects.particles.count", 20);
                    double offsetX = plugin.getConfig().getDouble("effects.particles.offset.x", 0.3);
                    double offsetY = plugin.getConfig().getDouble("effects.particles.offset.y", 0.1);
                    double offsetZ = plugin.getConfig().getDouble("effects.particles.offset.z", 0.3);
                    double speed = plugin.getConfig().getDouble("effects.particles.speed", 0.01);

                    for (String particleName : particles) {
                        try {
                            Particle particle = Particle.valueOf(particleName.toUpperCase());
                            player.getWorld().spawnParticle(
                                    particle,
                                    player.getLocation(),
                                    count,
                                    offsetX,
                                    offsetY,
                                    offsetZ,
                                    speed
                            );
                        } catch (IllegalArgumentException e) {
                            plugin.getLogger().warning("Invalid particle from config: " + particleName);
                        }
                    }
                }

                break;
            }
        }
    }
}
