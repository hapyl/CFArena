package me.hapyl.fight.anticheat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class AntiListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerQuitEvent(PlayerQuitEvent ev) {
        AntiCheat.getInstance().removeData(ev.getPlayer());
    }

}
