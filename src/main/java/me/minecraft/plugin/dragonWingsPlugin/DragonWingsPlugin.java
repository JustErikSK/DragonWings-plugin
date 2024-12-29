package me.minecraft.plugin.dragonWingsPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

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
    }

    @EventHandler
    public void dragonDefeat(EntityDeathEvent e) {
        LivingEntity ent = e.getEntity();
        Random ran = new Random();
        int num = ran.nextInt(100);

        int elytra_pe = this.getConfig().getInt("elytra_drop_percentage", 100);
        int elytra_am = this.getConfig().getInt("elytra_drop_amount", 1);

        if (elytra_pe > 100 || elytra_pe < 1) {
            elytra_pe = 10;
        }
        if (elytra_am > 10 || elytra_am < 1) {
            elytra_am = 1;
        }
        if (num <= elytra_pe) {
            if (ent.getType() == EntityType.ENDER_DRAGON) {
                e.getDrops().add(new ItemStack(Material.ELYTRA, elytra_am));
            }
        }
    }
}
