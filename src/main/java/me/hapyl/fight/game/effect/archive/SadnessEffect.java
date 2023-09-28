package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Sound;

public class SadnessEffect extends GameEffect {

    private final double defenseDecrease = 0.3;
    private final double attackDecrease = 0.15d;

    public SadnessEffect() {
        super("Radiating Sadness");

        setDescription(
                "You feel sad, losing the ability to heal, in addition to %s and %s decrease.",
                AttributeType.DEFENSE,
                AttributeType.ATTACK
        );
        setPositive(false);
    }

    @Override
    public void onStart(LivingGameEntity entity) {
        final EntityAttributes attributes = entity.getAttributes();

        attributes.subtract(AttributeType.DEFENSE, defenseDecrease);
        attributes.subtract(AttributeType.ATTACK, attackDecrease);

        entity.sendMessage("&5&l➰ &dYou feel sad and lonely!");
        entity.playSound(Sound.ENTITY_CAT_AMBIENT, 0.0f);
    }

    @Override
    public void onStop(LivingGameEntity entity) {
        final EntityAttributes attributes = entity.getAttributes();

        attributes.add(AttributeType.DEFENSE, defenseDecrease);
        attributes.add(AttributeType.ATTACK, attackDecrease);

        entity.sendMessage("&5&l➰ &dThe loneliness is gone!");
        entity.playSound(Sound.ENTITY_CAT_AMBIENT, 1.75f);
        entity.playSound(Sound.ENTITY_CAT_PURREOW, 1.25f);
    }

    @Override
    public void onTick(LivingGameEntity entity, int tick) {

    }
}
