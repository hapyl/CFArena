package me.hapyl.fight.trigger;

import org.bukkit.entity.Player;

public class PlayerTrigger implements Trigger {

    public final Player player;

    public PlayerTrigger(Player player) {
        this.player = player;
    }
}
