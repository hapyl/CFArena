package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.util.Vectors;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class MovementContainment extends Effect {
    private final double speedConstant = 100;

    public MovementContainment(@Nonnull String string) {
        super(string, EffectType.NEGATIVE);
    }

    public MovementContainment() {
        this("Movement Containment");
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity, int amplifier, int duration) {
        final EntityAttributes attributes = entity.getAttributes();

        attributes.subtractSilent(AttributeType.SPEED, speedConstant);

        // Maybe one day mojang will fix this stupid bug from TWENTY FUCKING THIRTEEN
        // with high levels of enchants/levels, but for now, if someone who is very smart,
        // like DiDenPro, reading this, I'll leave Jeb's comment on this bug:

        // Jeb added a comment - 04/Mar/13 11:22 AM
        // `I dunno why Dinnerbone removed the limit to the effect command I don't want to chase "bugs" like these..`

        if (entity instanceof GamePlayer) {
            entity.addPotionEffect(PotionEffectType.JUMP, 128, 100000);
        }
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {
        final EntityAttributes attributes = entity.getAttributes();

        attributes.addSilent(AttributeType.SPEED, speedConstant);

        if (entity instanceof GamePlayer) {
            entity.removePotionEffect(PotionEffectType.JUMP);
        }
    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {
        entity.setVelocity(Vectors.DOWN);
    }
}
