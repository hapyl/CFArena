package me.hapyl.fight.util.particle;

import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class ParticleDrawer {

    protected final Particle particle;

    ParticleDrawer(Particle particle) {
        this.particle = particle;
    }

    public void draw(@Nonnull Location location) {
        draw(location, 1);
    }

    public void draw(@Nonnull Location location, int count) {
        draw(location, count, 1.0f);
    }

    public void draw(@Nonnull Location location, int count, float speed) {
        Bukkit.getOnlinePlayers().forEach(player -> draw0(player, location, count, speed));
    }

    public void draw(@Nonnull Player player, @Nonnull Location location, int count) {
        draw(player, location, count, 1.0f);
    }

    public void draw(@Nonnull Player player, @Nonnull Location location, int count, float speed) {
        draw0(player, location, count, speed);
    }

    protected void draw0(Player player, Location location, int count, float speed) {
        PlayerLib.spawnParticle(player, location, particle, count, 0, 0, 0, speed);
    }

}
