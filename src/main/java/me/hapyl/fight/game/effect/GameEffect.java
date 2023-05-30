package me.hapyl.fight.game.effect;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public abstract class GameEffect {

    private final String name;

    private String description;
    private EffectParticle effectParticle;
    private boolean isPositive;

    // TODO (hapyl): 030, May 30: Display

    public GameEffect(String name) {
        this.name = name;
        this.description = "";
        this.isPositive = true;
    }

    public void setEffectParticle(EffectParticle effectParticle) {
        this.effectParticle = effectParticle;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDescription(String about, Object... objects) {
        this.setDescription(about.formatted(objects));
    }

    public void setPositive(boolean positive) {
        isPositive = positive;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPositive() {
        return isPositive;
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
