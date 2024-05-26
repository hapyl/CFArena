package me.hapyl.fight.util.particle;

import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class ParticleSpellMob extends ParticleDrawer {

    private static final float BLACK = 0.0001f;

    private final Color color;

    ParticleSpellMob(int red, int green, int blue) {
        super(Particle.ENTITY_EFFECT);

        this.color = Color.fromRGB(red, green, blue);
    }

    @Override
    protected void draw0(Player player, Location location, int count, float speed) {
        PlayerLib.spawnParticle(player, location, particle, 0, 0, 0, 0, speed, color);
    }

}
