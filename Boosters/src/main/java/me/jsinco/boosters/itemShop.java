package me.jsinco.boosters;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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

public class itemShop implements Listener {
    private static Inventory inv = Bukkit.createInventory(null,45, "§a§lItem Shop");
    public static void createGui() {
        initializeItems(); // Put items into GUI
    }

    public static void initializeItems() {
        for (int i = 0; i < 45; i++){
            inv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        int[] glassSlots = {11, 20, 29, 13, 22, 31, 15, 24, 33};
        for (int i = 0; i < glassSlots.length; i++) {
            inv.setItem(glassSlots[i], new ItemStack(Material.AIR));
        }
        //Types
        inv.setItem(20,createGuiItem(new ItemStack(Material.AMETHYST_SHARD, 1), "§b§lBooster", "§7§oKlick zum öffnen"));
        inv.setItem(22,createGuiItem(new ItemStack(Material.SUGAR, 1), "§a§lPerks", "§7§oKommt Bald"));
        inv.setItem(24,createGuiItem(new ItemStack(Material.ECHO_SHARD, 1), "§e§lNamensfarben", "§7§oKommt Bald"));


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

        if (clickedItem.getType().equals(Material.GRAY_STAINED_GLASS_PANE)) return;
        if (clickedItem.getType().equals(Material.AMETHYST_SHARD)){
            boosterShop.createGui();
            boosterShop.openInventory(player);
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
