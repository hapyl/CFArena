package me.hapyl.fight.util.particle;

import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class ParticleSpellMob extends ParticleDrawer {

    private static final float BLACK = 0.0001f;

    private final float r;
    private final float g;
    private final float b;

    ParticleSpellMob(int red, int green, int blue) {
        super(Particle.SPELL_MOB);

        this.r = (red == 0) ? BLACK : red / 255f;
        this.g = (green == 0) ? BLACK : green / 255f;
        this.b = (blue == 0) ? BLACK : blue / 255f;
    }

    @Override
    protected void draw0(Player player, Location location, int count, float speed) {
        PlayerLib.spawnParticle(player, location, particle, 0, r, g, b, speed);
    }

}
