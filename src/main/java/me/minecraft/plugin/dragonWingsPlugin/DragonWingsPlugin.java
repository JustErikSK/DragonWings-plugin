package me.minecraft.plugin.dragonWingsPlugin;

import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Objects;
import java.util.Random;

public final class DragonWingsPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();

        getLogger().info("Dragon Wings is loading drop percentages and amounts...");
        getLogger().info("Elytra's drop placement is being loaded...");

        FileConfiguration config = this.getConfig();
        config.addDefault("elytra_drop_percentage", 100);
        config.addDefault("elytra_drop_amount", 1);
        config.addDefault("elytra_drop_placement", "GROUND");

        getLogger().info("Config loaded!");
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "Dragon Drops >> Plugin has been enabled!");
    }

    @EventHandler
    public void dragonDefeat(EntityDeathEvent e) {
        LivingEntity ent = e.getEntity();
        Random ran = new Random();
        int num = ran.nextInt(100);
        Player killer = ent.getKiller();

        int elytra_pe = this.getConfig().getInt("elytra_drop_percentage", 100);
        int elytra_am = this.getConfig().getInt("elytra_drop_amount", 1);
        String elytra_pla = this.getConfig().getString("elytra_drop_placement", "GROUND");

        if (elytra_pe > 100 || elytra_pe < 1) {
            elytra_pe = 100;
        }
        if (elytra_am > 3 || elytra_am < 1) {
            elytra_am = 1;
        }

        final int finalElytraAm = elytra_am;

        int x = 0;
        int z = 0;
        World world = ent.getWorld();

        if (num <= elytra_pe) {
            if (ent.getType() == EntityType.ENDER_DRAGON) {
                if (elytra_pla.equalsIgnoreCase("GROUND")) { // GROUND option for placement
                    Location targetXZ = new Location(world, x, 0, z);

                    Bukkit.getRegionScheduler().execute(this, targetXZ, () -> {
                        int highestY = world.getHighestBlockYAt(targetXZ);
                        int minY = world.getMinHeight();
                        if (highestY <= minY + 1) {
                            highestY = ent.getLocation().getBlockY();
                        }

                        Location dropLoc = new Location(world, x + 0.5, highestY + 2, z + 0.5);
                        Item item = world.dropItem(dropLoc, new ItemStack(Material.ELYTRA, finalElytraAm));
                        item.setGravity(false);
                        item.setGlowing(true);
                        item.setVelocity(new Vector(0, 0, 0));
                        item.setPickupDelay(40);
                    });
                    if (killer != null) killer.sendMessage(ChatColor.GOLD + "The Dragon's Elytra spawned above the portal!");
                }
                else if (elytra_pla.equalsIgnoreCase("CHEST")) { // CHEST option for placement
                    Location targetXZ = new Location(world, x, 0, z);

                    Bukkit.getRegionScheduler().execute(this, targetXZ, () -> {
                        int highestY = world.getHighestBlockYAt(targetXZ);
                        int minY = world.getMinHeight();
                        if (highestY <= minY + 1) highestY = ent.getLocation().getBlockY();

                        Location chestLoc = new Location(world, x, highestY + 2, z);
                        chestLoc.getBlock().setType(Material.CHEST, false);

                        Bukkit.getRegionScheduler().runDelayed(this, chestLoc, task -> {
                            BlockState state = chestLoc.getBlock().getState(false);
                            if (!(state instanceof Container container)) {
                                getLogger().warning("Expected Container at " + chestLoc + " but got " + state.getType());
                                return;
                            }

                            Inventory inv = container.getInventory();
                            getLogger().info("Chest size=" + inv.getSize() + ", finalElytraAm=" + finalElytraAm);

                            for (int i = 0; i < finalElytraAm; i++) {
                                Map<Integer, ItemStack> leftover = inv.addItem(new ItemStack(Material.ELYTRA, 1));
                                if (!leftover.isEmpty()) {
                                    getLogger().warning("Could not insert elytra into chest, leftovers=" + leftover);
                                    break;
                                }
                            }
                            state.update(true, false);
                            getLogger().info("Chest contents now: " + java.util.Arrays.toString(inv.getContents()));
                        }, 2L);
                    });
                    if (killer != null) killer.sendMessage(ChatColor.GOLD + "A chest spawned above the portal with the Dragon's Elytra!");
                }
                else if (elytra_pla.equalsIgnoreCase("INVENTORY")) { // INVENTORY option for placement
                    if (killer instanceof Player player) {
                        player.getScheduler().run(this, task -> {
                            player.getInventory().addItem(new ItemStack(Material.ELYTRA, finalElytraAm));
                            player.sendMessage(ChatColor.GOLD + "The Dragon's Elytra has spawned in your inventory!");
                        }, null);
                    }
                }
            }
        }
    }
}
