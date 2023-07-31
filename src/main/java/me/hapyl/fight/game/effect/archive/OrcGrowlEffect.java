package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.ui.display.DebuffDisplay;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class OrcGrowlEffect extends GameEffect {
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
    public void onStart(GameEntity entity) {
        entity.addPotionEffect(PotionEffectType.SLOW, 10000, 4);
        entity.addPotionEffect(PotionEffectType.WEAKNESS, 10000, 0);
    }

    @Override
    public void onStop(GameEntity entity) {
        entity.removePotionEffect(PotionEffectType.SLOW);
        entity.removePotionEffect(PotionEffectType.WEAKNESS);
    }

    @Override
    public void onTick(GameEntity entity, int tick) {

    }
}
