package me.jsinco.boosters;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class command implements CommandExecutor {
    public static String playerUUID;
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        playerUUID = player.getUniqueId().toString();
        if (command.getName().equalsIgnoreCase("itemshop")){
            itemShop.createGui();
            itemShop.openInventory(player);
            return true;
        } else if (command.getName().equalsIgnoreCase("boosters")){
            BoosterGUI.createGui();
            BoosterGUI.openInventory(player);
            return true;
        } else if (command.getName().equalsIgnoreCase("immortal")) {
            if (strings.length > 0) {
                String playerName = strings[0];
                Player target = Bukkit.getServer().getPlayerExact(playerName);
                target.setInvulnerable(!target.isInvulnerable());
                target.sendMessage("toggled immortality");
                player.sendMessage("toggled immortality");
            }
        }
        return true;
    }
}
