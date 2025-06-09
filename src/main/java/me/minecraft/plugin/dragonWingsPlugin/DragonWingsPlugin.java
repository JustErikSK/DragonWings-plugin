package me.minecraft.plugin.dragonWingsPlugin;

import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Random;

public final class DragonWingsPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "DragonDrops >> Plugin has been enabled!");
        this.getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();

        FileConfiguration config = this.getConfig();
        config.addDefault("elytra_drop_percentage", 100);
        config.addDefault("elytra_drop_amount", 1);
        config.addDefault("elytra_drop_placement", "GROUND");
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
        if (num <= elytra_pe) {
            if (ent.getType() == EntityType.ENDER_DRAGON) {
                ItemStack elytra = new ItemStack(Material.ELYTRA, elytra_am);
                ItemMeta itemStackMeta = elytra.getItemMeta();
                itemStackMeta.setDisplayName(ChatColor.GOLD + "Dragon's Wings");
                elytra.setItemMeta(itemStackMeta);
                e.getDrops().add(elytra);
            }
        }

        int x = 0;
        int z = 0;
        World world = ent.getWorld();
        int y = world.getHighestBlockYAt(x, z);

        if (elytra_pla.equals("GROUND")) { // GROUND option for drop placement
            ent.getWorld().dropItem(new Location(world, x+0.5, y+1, z+0.5), new ItemStack(Material.ELYTRA));
        } else if (elytra_pla.equals("CHEST")) { // CHEST option for drop placement
            Location chestLocation = new Location(world, x+0.5, y+1, z+0.5);
            chestLocation.getBlock().setType(Material.CHEST);
            Chest chest = (Chest) chestLocation.getBlock().getState();
            chest.getInventory().addItem(new ItemStack(Material.ELYTRA));
        } else if (elytra_pla.equals("INVENTORY")) { // INVENTORY option for drop placement
            if (killer instanceof Player player) {
                player.getInventory().addItem(new ItemStack(Material.ELYTRA));
            }
        }
    }
}
