package me.hapyl.fight.game.weapons;

import me.hapyl.eterna.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class PackedParticle {

    public static final PackedParticle EMPTY = new PackedParticle(Particle.CRIT) {
        @Override
        public void display(@Nonnull Location location) {
        }

        @Override
        public void display(@Nonnull Player player, @Nonnull Location location) {
        }
    };

    private final Particle particle;
    private int amount;
    private double oX, oY, oZ;
    private float speed;

    public PackedParticle(Particle particle) {
        this(particle, 1, 0.0d, 0.0d, 0.0d, 0.0f);
    }

    public PackedParticle(Particle particle, int amount, double oX, double oY, double oZ, float speed) {
        this.particle = particle;
        this.amount = amount;
        this.oX = oX;
        this.oY = oY;
        this.oZ = oZ;
        this.speed = speed;
    }

    public int getAmount() {
        return amount;
    }

    public PackedParticle setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public double getOffsetX() {
        return oX;
    }

    public PackedParticle setOffsetX(double oX) {
        this.oX = oX;
        return this;
    }

    public double getOffsetY() {
        return oY;
    }

    public PackedParticle setOffsetY(double oY) {
        this.oY = oY;
        return this;
    }

    public double getOffsetZ() {
        return oZ;
    }

    public PackedParticle setOffsetZ(double oZ) {
        this.oZ = oZ;
        return this;
    }

    public float getSpeed() {
        return speed;
    }

    public PackedParticle setSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    @Nonnull
    public Particle getParticle() {
        return particle;
    }

    public void display(@Nonnull Location location) {
        PlayerLib.spawnParticle(location, particle, amount, oX, oY, oZ, speed);
    }

    public void display(@Nonnull Player player, @Nonnull Location location) {
        PlayerLib.spawnParticle(player, location, particle, amount, oX, oY, oZ, speed);
    }

}
