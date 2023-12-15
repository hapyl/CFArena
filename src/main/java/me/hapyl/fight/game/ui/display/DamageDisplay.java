package me.hapyl.fight.game.ui.display;

import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import javax.annotation.Nonnull;

public class DamageDisplay extends StringDisplay {

    public static final String FORMAT = "&b&l%.0f";
    public static final String FORMAT_CRIT = "&e&l%.0f&c✷";

    private final boolean isCrit;

    public DamageDisplay(double damage, boolean isCrit) {
        super(isCrit ? FORMAT_CRIT.formatted(damage) : FORMAT.formatted(damage), isCrit ? 30 : 20);

        this.isCrit = isCrit;
        this.animation = AscendingDisplay.ANIMATION;
        this.initTransformation = transformationScale(0.0f);
    }

    @Override
    public void onStart(@Nonnull TextDisplay display) {
        display.setDefaultBackground(false);
        transformScale(display, isCrit ? 1.25f : 1.0f, stay);
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
