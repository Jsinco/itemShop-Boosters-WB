package me.jsinco.boosters;

import me.jsinco.boosters.util.savedBoosters;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class boosterShop implements Listener {
    private static Inventory inv = Bukkit.createInventory(null,45, "§a§lBooster Shop");
    static int boosterAmount = 0;

    public static void createGui() {
        // Create a new inventory, with no owner (as this isn't a real inventory)
        savedBoosters.reload();
        boosterAmount = savedBoosters.get().getInt(command.playerUUID);
        initializeItems(); // Put items into GUI
    }

    public static void initializeItems() {
        for (int i = 0; i < 45; i++){
            inv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
            if (i > 10 && i < 17) {
                inv.setItem(i, new ItemStack(Material.AIR));
            } else if (i > 18 && i < 26) {
                inv.setItem(i, new ItemStack(Material.AIR));
            } else if (i > 27 && i < 35) {
                inv.setItem(i, new ItemStack(Material.AIR));
            }
        }
        //Types
        inv.setItem(10,createGuiItem(new ItemStack(Material.AMETHYST_SHARD, 1), "§b§lBooster", "§7§oKlicke um den Booster für §a§o" +
                Boosters.getPlugin(Boosters.class).getConfig().getInt("BoosterPrice") +" Coins " + "§7§ozu kaufen"));
        //Booster amounts
        inv.setItem(40, createGuiItem(new ItemStack(Material.BOOK, 1), "§7Deine Booster: §a" + boosterAmount,  ""));
        ItemMeta meta = inv.getItem(40).getItemMeta();
        meta.getLore().remove(0);
        inv.getItem(40).setItemMeta(meta);


    }

    // method to create a gui item with a custom name, and description
    protected static ItemStack createGuiItem(ItemStack itemStack, String name, String... lore) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        itemStack.setItemMeta(meta);
        return itemStack;
    }


    public static void openInventory(final HumanEntity ent) {
        ent.openInventory(inv);
    } //open GUI


    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getInventory().equals(inv)) return;
        e.setCancelled(true);

        Player player = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) return; // not null

        if (clickedItem.getType().equals(Material.BOOK) || clickedItem.getType().equals(Material.GRAY_STAINED_GLASS_PANE)) return;
        Economy economy = Boosters.getEconomy();
        String PlayerUUID = player.getUniqueId().toString();
        if(clickedItem.getType().equals(Material.AMETHYST_SHARD)){
            if (economy.getBalance(player) >= Boosters.getPlugin(Boosters.class).getConfig().getInt("BoosterPrice")){
                economy.withdrawPlayer(player, Boosters.getPlugin(Boosters.class).getConfig().getInt("BoosterPrice"));
                int boosterTotal = savedBoosters.get().getInt(PlayerUUID);
                boosterTotal += 1;
                savedBoosters.get().set(PlayerUUID, boosterTotal);
                savedBoosters.save();
                player.sendMessage("§a§lWONDERBUILD §8• §7Du hast einen Booster gekauft!");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                player.closeInventory();
            } else {
                player.sendMessage("§a§lWONDERBUILD §8• §cDafür hast du nicht genug Geld!");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                player.closeInventory();
            }
        }

    }


    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inv)) {
            e.setCancelled(true);
        }
    }
}
