package me.hapyl.fight.game.effect.storage;

import me.hapyl.fight.game.effect.GameEffect;
import org.bukkit.entity.Player;

public class FallDamageResistance extends GameEffect {

    public FallDamageResistance() {
        super("Fall Damage Resistance");
        this.setDescription("Negates all fall damage.");
    }

    @Override
    public void onTick(Player player, int tick) {

    }

    @Override
    public void onStart(Player player) {

    }

    @Override
    public void onStop(Player player) {

    }
}
