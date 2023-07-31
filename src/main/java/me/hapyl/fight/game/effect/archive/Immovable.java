package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.GameEntity;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class Immovable extends GameEffect {

    private final Map<LivingEntity, Double> oldValue = new HashMap<>();

    public Immovable() {
        super("Immovable");
        setDescription("Players are not affected by knockback.");
        setPositive(false); // I mean kinda positive but kinda not you know but there is only one character that can do this, and it's their ability that you know kinda to make enemies like bad, so you can like easier hit them and deal damaje :|
    }

    @Override
    public void onTick(GameEntity entity, int tick) {

    }

    @Override
    public void onStart(GameEntity entity) {
        oldValue.put(entity.getEntity(), entity.getAttributeValue(Attribute.GENERIC_KNOCKBACK_RESISTANCE));
        entity.setAttributeValue(Attribute.GENERIC_KNOCKBACK_RESISTANCE, 1.0d);
    }

    @Override
    public void onStop(GameEntity gameEntity) {
        final LivingEntity entity = gameEntity.getEntity();

        final Double value = oldValue.remove(entity);
        gameEntity.setAttributeValue(Attribute.GENERIC_KNOCKBACK_RESISTANCE, value == null ? 0.0d : value);
    }
}
