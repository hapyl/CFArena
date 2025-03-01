package me.hapyl.fight.game.effect.effects;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.Type;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.ui.display.DebuffDisplay;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class OrcGrowlEffect extends Effect {
    private final double attackReduction = 0.2d;

    public OrcGrowlEffect() {
        super("Orc Growl", Type.NEGATIVE);

        setDescription("""
                Slows and weakens enemies with Orc's fear.
                """);

        setDisplay(new DebuffDisplay("&f&lsᴄᴀʀᴇᴅ", 30) {

            // Don't randomize the location
            @Nonnull
            @Override
            public Location getLocation(Location location) {
                return location;
            }
        });
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity, int amplifier, int duration) {
        final EntityAttributes attributes = entity.getAttributes();

        entity.addPotionEffectIndefinitely(PotionEffectType.SLOWNESS, 4);
        attributes.subtractSilent(AttributeType.ATTACK, attackReduction);
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {
        final EntityAttributes attributes = entity.getAttributes();

        entity.removePotionEffect(PotionEffectType.SLOWNESS);
        attributes.addSilent(AttributeType.ATTACK, attackReduction);
    }

}
