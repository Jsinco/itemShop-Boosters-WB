package me.jsinco.boosters;

import me.jsinco.boosters.util.savedBoosters;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoosterGUI implements Listener {
    private static Inventory inv = Bukkit.createInventory(null,45, "§a§lBooster");
    static int boosterAmount = 0;

    static List<String> activeBoosters = new ArrayList<>();


    public static void createGui() {
        // Create a new inventory, with no owner (as this isn't a real inventory)
        savedBoosters.reload();
        boosterAmount = savedBoosters.get().getInt(command.playerUUID);
        initializeItems(); // Put items into GUI
    }

    public static void initializeItems() {
        for (int i = 0; i < 45; i++){
            inv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        int[] glassSlots = {10, 19, 28, 12, 21, 30, 14, 23, 32, 16, 25, 34};
        for (int i = 0; i < glassSlots.length; i++) {
            inv.setItem(glassSlots[i], new ItemStack(Material.AIR));
        }

        //Booster types
        inv.setItem(10,createGuiItem(new ItemStack(Material.GOLDEN_PICKAXE, 1), "§e§lBreak Booster", "§7Klick zum auswählen", "break"));
        //hide attributes
        ItemMeta meta = inv.getItem(10).getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        inv.getItem(10).setItemMeta(meta);
        inv.setItem(19,createGuiItem(new ItemStack(Material.FEATHER, 1), "§a§lFly Booster", "§7Klick zum auswählen", "fly"));
        inv.setItem(28,createGuiItem(new ItemStack(Material.WATER_BUCKET, 1), "§b§lWasseratmung Booster", "§7Klick zum auswählen", "wasseratmung"));
        inv.setItem(12,createGuiItem(new ItemStack(Material.SHIELD, 1), "§c§lStärke Booster ", "§7Klick zum auswählen", "stärke"));
        inv.setItem(21,createGuiItem(new ItemStack(Material.TOTEM_OF_UNDYING, 1), "§d§lUnsterblickeit Booster", "§7Klick zum auswählen", "unsterblichkeit"));
        //Booster amounts
        inv.setItem(40, createGuiItem(new ItemStack(Material.BOOK, 1), "§7Deine Booster: §a" + boosterAmount, ""));
        ItemMeta meta2 = inv.getItem(40).getItemMeta();
        meta2.getLore().remove(0);
        inv.getItem(40).setItemMeta(meta2);
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

    ItemStack clickedItem;
    Player activator;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getInventory().equals(inv)) return;
        e.setCancelled(true);


        clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) return; // not null

        if (clickedItem.getType().equals(Material.BOOK) || clickedItem.getType().equals(Material.GRAY_STAINED_GLASS_PANE)) return; // not book or glass pane

        activator = (Player) e.getWhoClicked();
        String PlayerUUID = activator.getUniqueId().toString();
        int boosterTotal = savedBoosters.get().getInt(PlayerUUID);
        if (boosterTotal > 0){
            if (PotionBoosterActivate()){
                boosterTotal -= 1;
                savedBoosters.get().set(PlayerUUID, boosterTotal);
                savedBoosters.save();
                activator.closeInventory();
            }
        } else {
            activator.sendMessage("§cYou don't have any boosters!");
        }
    }
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) { // Cancel dragging in inventory
        if (e.getInventory().equals(inv)) {
            e.setCancelled(true);
        }
    }


    public boolean PotionBoosterActivate(){
        int AMP = Boosters.getPlugin(Boosters.class).getConfig().getInt("BoosterAmplifier");
        String[] PotionBoosters = new String[]{"BREAK","WASSERATMUNG","STÄRKE"};
        PotionEffectType[] PotionTypes = new PotionEffectType[]{PotionEffectType.FAST_DIGGING, PotionEffectType.WATER_BREATHING, PotionEffectType.INCREASE_DAMAGE};
        for (int i = 0; i < PotionBoosters.length; i++){
            if (!clickedItem.getItemMeta().hasLore()) return false;
            if (clickedItem.getItemMeta().getLore().get(1).equals(PotionBoosters[i].toLowerCase()) && !activeBoosters.contains(PotionBoosters[i])){
                activeBoosters.add(PotionBoosters[i]);
                int finalI = i;
                boosterMsg(PotionBoosters[finalI]);

                int task;
                task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Boosters.getPlugin(Boosters.class), () -> {
                    Bukkit.getServer().getOnlinePlayers().forEach(player -> player.addPotionEffect(new PotionEffect(PotionTypes[finalI], 365, AMP)));
                }, 0L, 100L);

                Bukkit.getScheduler().scheduleSyncDelayedTask(Boosters.getPlugin(Boosters.class), () -> {
                    activeBoosters.remove(PotionBoosters[finalI]);
                    boosterEndMsg(PotionBoosters[finalI]);
                    Bukkit.getScheduler().cancelTask(task);
                }, 12000L);
                return true;
            }
        }
        String[] NonPotionBoosters = new String[]{"FLY","UNSTERBLICHKEIT"};
        for (int i = 0; i < NonPotionBoosters.length; i++){
            if (clickedItem.getItemMeta().getLore().get(1).equals(NonPotionBoosters[i].toLowerCase()) && !activeBoosters.contains(NonPotionBoosters[i])) {
                activeBoosters.add(NonPotionBoosters[i]);
                int finalI = i;
                boosterMsg(NonPotionBoosters[finalI]);

                int task;
                if (NonPotionBoosters[finalI].equals("FLY")) {
                    task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Boosters.getPlugin(Boosters.class), () -> {
                        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                            if (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR))
                                return;
                            if (!player.getAllowFlight()) {
                                player.setAllowFlight(true);
                                player.sendMessage("§6Flight §aenabled");
                            }
                        });
                    }, 0L, 100L);
                } else {
                    task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Boosters.getPlugin(Boosters.class), () -> {
                        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                            if (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR))
                                return;
                            if (!player.isInvulnerable()) {
                                player.setInvulnerable(true);
                                player.sendMessage("§6Immortality §aenabled");
                            }
                        });
                    }, 0L, 100L);
                }




                Bukkit.getScheduler().scheduleSyncDelayedTask(Boosters.getPlugin(Boosters.class), () -> {
                    Bukkit.getScheduler().cancelTask(task);
                    activeBoosters.remove(NonPotionBoosters[finalI]);
                    boosterEndMsg(NonPotionBoosters[finalI]);
                    Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                        if (NonPotionBoosters[finalI].equals("FLY")){
                            if (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR)) return;
                            if (player.getAllowFlight()){
                                player.setAllowFlight(false);
                                player.sendMessage("§6Flight §cdisabled");
                            }
                        } else {
                            if (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR)) return;
                            if (player.isInvulnerable()){
                                player.setInvulnerable(false);
                                player.sendMessage("§6Immortality §cdisabled");
                            }
                        }
                    });
                }, 12000L);
                return true;
            }
        }

        return false;
    }

    public void boosterMsg(String boosterName) {
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§a§l" + boosterName + " BOOSTER WURDE AKTIVIERT! ");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§7Von: §e" + activator.getName());
        Bukkit.broadcastMessage("§7Dauer: §e10 minuten");
        Bukkit.broadcastMessage("");
        Bukkit.getServer().getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1));
    }
    public static void boosterEndMsg(String boosterName) {
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§c§lDER " + boosterName + " BOOSTER IST ZU ENDE!");
        Bukkit.broadcastMessage("");
        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ENTITY_PIGLIN_JEALOUS, 1, 1));
    }

}
