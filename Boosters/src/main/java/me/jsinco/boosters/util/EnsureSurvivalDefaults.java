package me.jsinco.boosters.util;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EnsureSurvivalDefaults implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE) || event.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) return;
        if (event.getPlayer().isInvulnerable()){event.getPlayer().setInvulnerable(false);}
        if (event.getPlayer().getAllowFlight()){event.getPlayer().setAllowFlight(false);}
    }
}
