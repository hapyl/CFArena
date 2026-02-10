package me.hapyl.fight.game.maps.features.japan;

import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class JapanBooster {

    private final Location location;
    private final Vector vector;
    private final double horizontalMagnitude;
    private final int boostDelay;

    public JapanBooster(double x, double y, double z, double yMagnitude, double xzMagnitude, int delay) {
        this.location = BukkitUtils.defLocation(x + 0.5, y, z + 0.5);
        this.vector = new Vector(0, yMagnitude, 0);
        this.horizontalMagnitude = xzMagnitude;
        this.boostDelay = delay;
    }

    public void tick() {
        PlayerLib.spawnParticle(location, Particle.FIREWORK, 1, 0, 0, 0, 0);
        PlayerLib.spawnParticle(location, Particle.FLAME, 1, 0, 0, 0, 0.025f);
    }

    public void boost(@Nonnull GamePlayer player) {
        player.setVelocity(vector);
        player.playWorldSound(Sound.ENTITY_WITHER_SHOOT, 0.75f);

        player.schedule(() -> {
            final Vector vector = player.getLocation().getDirection().normalize().setY(0.0d).multiply(horizontalMagnitude);

            player.setVelocity(vector);
            player.playWorldSound(Sound.ENTITY_WITHER_SHOOT, 1.5f);
        }, boostDelay);

        player.addEffect(EffectType.FALL_DAMAGE_RESISTANCE, boostDelay * 3);
    }

    @Nonnull
    public Location getLocation() {
        return location;
    }

}
