package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.ui.display.DebuffDisplay;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class OrcGrowlEffect extends GameEffect {
    private final double attackReduction = 0.2d;

    public OrcGrowlEffect() {
        super("Orc Growl");

        setDescription("Slows and weakens enemies with Orc's fear.");
        setPositive(false);

        setDisplay(new DebuffDisplay("&f&lSCARED", 30) {

            // Don't randomize the location
            @Nonnull
            @Override
            public Location getLocation(Location location) {
                return location;
            }
        });
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity) {
        final EntityAttributes attributes = entity.getAttributes();

        entity.addPotionEffect(PotionEffectType.SLOW, 10000, 4);
        attributes.subtractSilent(AttributeType.ATTACK, attackReduction);
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity) {
        final EntityAttributes attributes = entity.getAttributes();

        entity.removePotionEffect(PotionEffectType.SLOW);
        attributes.addSilent(AttributeType.ATTACK, attackReduction);
    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {

    }
}
