package me.hapyl.fight.game.effect.storage;

import me.hapyl.fight.game.effect.GameEffect;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Immovable extends GameEffect {

    private final Map<Player, Double> oldValue = new HashMap<>();

    public Immovable() {
        super("Immovable");
        setDescription("Players are not be affected by knockback.");
        setPositive(false); // I mean kinda positive but kinda not you know but there is only one character that can do this, and it's their ability that you know kinda to make enemies like bad, so you can like easier hit them and deal damaje :|
    }

    @Override
    public void onTick(Player player, int tick) {

    }

    @Override
    public void onStart(Player player) {
        final AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        if (attribute == null) {
            return;
        }

        oldValue.put(player, attribute.getBaseValue());
        attribute.setBaseValue(1.0d);
    }

    @Override
    public void onStop(Player player) {
        final AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        if (attribute == null) {
            return;
        }

        attribute.setBaseValue(oldValue.getOrDefault(player, 0.0d));
        oldValue.remove(player);
    }
}
