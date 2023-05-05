package me.hapyl.fight.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;

public class SnowFormHandler implements Listener {

    @EventHandler()
    public void handleSnowForm(BlockFormEvent ev) {
        ev.setCancelled(true);
    }

}
