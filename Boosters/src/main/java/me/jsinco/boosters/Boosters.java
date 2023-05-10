package me.jsinco.boosters;

import me.jsinco.boosters.util.EnsureSurvivalDefaults;
import me.jsinco.boosters.util.savedBoosters;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


public final class Boosters extends JavaPlugin {
    private static Economy econ = null;
    @Override
    public void onEnable() {

        if (!setupEconomy()) {
            System.out.println("No economy plugin found. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getConfig().addDefault("BoosterPrice", 12000);
        getConfig().addDefault("BoosterAmplifier", 3);
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        savedBoosters.setup();
        savedBoosters.save();

        getServer().getPluginManager().registerEvents(new BoosterGUI(), this);
        getServer().getPluginManager().registerEvents(new boosterShop(), this);
        getServer().getPluginManager().registerEvents(new itemShop(), this);
        getServer().getPluginManager().registerEvents(new EnsureSurvivalDefaults(), this);
        getCommand("itemshop").setExecutor(new command());
        getCommand("boosters").setExecutor(new command());
        getCommand("immortal").setExecutor(new command());
        BoosterGUI.activeBoosters.clear();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

}
