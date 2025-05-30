package dev.drygo.XLaunchPads.Listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import dev.drygo.XLaunchPads.Managers.LaunchPadsManager;
import dev.drygo.XLaunchPads.XLaunchPads;

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
    public void onItemUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (handItem == null || handItem.getType().isAir()) return;

        // Verifica si la lista existe y es válida
        if (!plugin.getConfig().isList("item-launch.items")) return;

        List<Map<?, ?>> itemsConfig = plugin.getConfig().getMapList("item-launch.items");
        if (itemsConfig.isEmpty()) return;

        for (Map<?, ?> itemConfig : itemsConfig) {
            if (!itemConfig.containsKey("material")) {
                plugin.getLogger().warning("Item in 'item-launch.items' is missing 'material' key.");
                continue;
            }

            // Material
            String materialName = String.valueOf(itemConfig.get("material")).toUpperCase();
            Material material;
            try {
                material = Material.valueOf(materialName);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid material in item-launch.items: " + materialName);
                continue;
            }

            if (handItem.getType() != material) continue;

            // Custom Model Data (opcional)
            if (itemConfig.containsKey("custom-model-data")) {
                int cmd = ((Number) itemConfig.get("custom-model-data")).intValue();
                if (handItem.getItemMeta() == null || !handItem.getItemMeta().hasCustomModelData()) continue;
                if (handItem.getItemMeta().getCustomModelData() != cmd) continue;
            }

            // Impulso individual
            double horizontal = itemConfig.containsKey("horizontal")
                    ? ((Number) itemConfig.get("horizontal")).doubleValue()
                    : plugin.getConfig().getDouble("launch-power.horizontal", 2.0);

            double vertical = itemConfig.containsKey("vertical")
                    ? ((Number) itemConfig.get("vertical")).doubleValue()
                    : plugin.getConfig().getDouble("launch-power.vertical", 1.0);

            Vector dir = player.getLocation().getDirection().normalize();
            Vector velocity = dir.multiply(horizontal);
            velocity.setY(vertical);
            player.setVelocity(velocity);

            playEffects(player);
            break;
        }
    }



    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!player.isOnGround()) return;
        if (!plugin.getConfig().getBoolean("block-launch.enabled")) return;

        Location loc = player.getLocation().clone().subtract(0, 1, 0);
        Block blockBelow = loc.getBlock();
        Material material = blockBelow.getType();

        // Comprobar si hay una sección de bloques configurados
        if (!plugin.getConfig().isConfigurationSection("block-launch.blocks")) return;

        String matName = material.name();
        if (!plugin.getConfig().isConfigurationSection("block-launch.blocks." + matName)) return;

        double horizontal = plugin.getConfig().getDouble("block-launch.blocks." + matName + ".horizontal", 0.0);
        double vertical = plugin.getConfig().getDouble("block-launch.blocks." + matName + ".vertical", 1.0);

        Vector velocity;
        if (horizontal > 0) {
            Vector dir = player.getLocation().getDirection().normalize();
            velocity = dir.multiply(horizontal);
            velocity.setY(vertical);
        } else {
            velocity = new Vector(0, vertical, 0);
        }

        player.setVelocity(velocity);

        playEffects(player);
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

                playEffects(player);

                break;
            }
        }
    }

    private void playEffects(Player player) {
        // 🔊 Sonidos
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

        // 🌟 Partículas
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
    }
}
