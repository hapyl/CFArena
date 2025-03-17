package me.hapyl.fight.game.heroes.spark;

import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.weapons.range.RangeWeapon;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SparkData extends PlayerData {

    @Nullable protected RunItBackData runItBack;

    public SparkData(@Nonnull GamePlayer player) {
        super(player);
    }

    @Override
    public void remove() {
        runItBack = null;
    }

    @Nonnull
    public Location markerLocation() {
        return LocationHelper.anchor(player.getLocation());
    }

    public void rebirth() {
        if (runItBack == null) {
            return;
        }

        final double health = runItBack.health();

        player.setVisualFire(false);
        player.setHealth(health);
        player.addEffect(EffectType.RESPAWN_RESISTANCE, 20);

        final Location teleportLocation = runItBack.location();
        teleportLocation.setYaw(player.getYaw());
        teleportLocation.setPitch(player.getPitch());

        player.teleport(teleportLocation);

        // Reload
        if (player.getHero().getWeapon() instanceof RangeWeapon rangeWeapon) {
            rangeWeapon.forceReload(player);
        }

        // Fx
        player.addEffect(EffectType.SLOW, 50, 20);
        player.playWorldSound(Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 1.5f);

        player.spawnWorldParticle(Particle.FIREWORK, 50, 0.1d, 0.5d, 0.1d, 0.2f);
        player.spawnWorldParticle(Particle.LAVA, 10, 0.1d, 0.5d, 0.1d, 0.2f);

        player.sendTitle("&6🔥", "&eOn Rebirth...", 5, 10, 5);

        // Reset
        remove();
    }
}
