package me.hapyl.fight.game.effect;

import me.hapyl.fight.game.ui.display.StringDisplay;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public abstract class GameEffect {

    private final String name;

    private String description;
    private EffectParticle effectParticle;
    private boolean isPositive;

    private StringDisplay display;

    public GameEffect(String name) {
        this.name = name;
        this.description = "";
        this.isPositive = true;
    }

    public StringDisplay getDisplay() {
        return display;
    }

    public void setDisplay(StringDisplay display) {
        this.display = display;
    }

    public void setEffectParticle(EffectParticle effectParticle) {
        this.effectParticle = effectParticle;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String about, Object... objects) {
        this.setDescription(about.formatted(objects));
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPositive() {
        return isPositive;
    }

    public void setPositive(boolean positive) {
        isPositive = positive;
    }

    public abstract void onStart(LivingEntity entity);

    public abstract void onStop(LivingEntity entity);

    public abstract void onTick(LivingEntity entity, int tick);

    public void onUpdate(LivingEntity entity) {
    }

    public String getExtra() {
        return "";
    }

    public void displayParticles(Location location, LivingEntity ignore) {
        displayParticles(location, ignore, this.effectParticle);
    }

    public void displayParticles(Location location, LivingEntity ignore, EffectParticle particle) {
        if (particle == null || !(ignore instanceof Player player)) {
            return;
        }

        particle.display(location, player);
    }

}
