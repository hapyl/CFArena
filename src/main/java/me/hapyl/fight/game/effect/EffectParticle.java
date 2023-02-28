package me.hapyl.fight.game.effect;

import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

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

    public void display(Location location, @Nullable Player ignored) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (ignored != null && player == ignored) {
                return;
            }
            PlayerLib.spawnParticle(player, location, particle, amount, oX, oY, oZ, speed);
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
