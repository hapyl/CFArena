package me.hapyl.fight.game.ui.display;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import javax.annotation.Nonnull;

public class DamageDisplay extends StringDisplay {

    private final DamageInstance instance;
    private final LivingGameEntity damager;

    public DamageDisplay(@Nonnull DamageInstance instance) {
        super(
                instance.getDamageFormatted(),
                instance.isCrit() ? 30 : 20
        );

        this.instance = instance;
        this.damager = instance.getDamager();
        this.animation = AscendingDisplay.ANIMATION;
        this.initTransformation = transformationScale(0.0f);
    }

    @Override
    public void onPrepare(@Nonnull TextDisplay display) {
        if (damager != null) {
            display.setVisibleByDefault(false);
        }
    }

    @Override
    public void onStart(@Nonnull TextDisplay display) {
        // Show only for the players who can see the damager
        if (damager != null) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (damager.is(player) || damager.isVisibleTo(player)) {
                    player.showEntity(CF.getPlugin(), display);
                }
            });
        }

        display.setDefaultBackground(false);
        transformScale(display, instance.isCrit() ? 1.25f : 1.0f, stay);
    }

    private void transformScale(TextDisplay display, float scale, int duration) {
        final Transformation transformation = display.getTransformation();

        display.setInterpolationDuration(duration);
        display.setTransformation(new Transformation(
                transformation.getTranslation(),
                transformation.getLeftRotation(),
                new Vector3f(scale, scale, scale),
                transformation.getRightRotation()
        ));
    }

}
