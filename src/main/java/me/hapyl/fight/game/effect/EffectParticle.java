package me.hapyl.fight.game.effect;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EffectParticle {

    private final Particle particle;
    private final int amount;
    private final double oX, oY, oZ;
    private final float speed;

    public EffectParticle(Particle particle, int amount) {
        this(particle, amount, 0.0d, 0.0d, 0.0d, 0.0f);
    }

    public EffectParticle(Particle particle, int amount, double oX, double oY, double oZ, float speed) {
        this.particle = particle;
        this.amount = amount;
        this.oX = oX;
        this.oY = oY;
        this.oZ = oZ;
        this.speed = speed;
    }

    public void display(@Nonnull Location location, @Nullable GamePlayer ignored) {
        CF.getPlayers().forEach(player -> {
            if (player.equals(ignored)) {
                return;
            }

            player.spawnParticle(location, particle, amount, oX, oY, oZ, speed);
        });
    }

    public Particle getParticle() {
        return particle;
    }

    public int getAmount() {
        return amount;
    }

    public double getOffsetX() {
        return oX;
    }

    public double getOffsetY() {
        return oY;
    }

    public double getOffsetZ() {
        return oZ;
    }

    public float getSpeed() {
        return speed;
    }
}
