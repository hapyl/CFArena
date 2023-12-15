package me.hapyl.fight.game.talents.archive.witcher;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class Aard extends Talent {

    @DisplayField private final double radius = 4.0d;

    public Aard() {
        super("Aard", "Creates a small explosion in front of you that pushes enemies away.");

        setItem(Material.HEART_OF_THE_SEA);
        setCooldownSec(5);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Vector vector = player.getLocation().getDirection().setY(0.125d).multiply(2.0d);
        final Location inFront = player.getLocation().add(vector);
        final World world = inFront.getWorld();

        if (world == null) {
            return Response.error("world is null");
        }

        Collect.nearbyEntities(inFront, radius).forEach(entity -> {
            if (player.isSelfOrTeammateOrHasEffectResistance(entity)) {
                return;
            }

            entity.setVelocity(vector);
        });

        // fx
        PlayerLib.playSound(inFront, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.25f);
        PlayerLib.spawnParticle(inFront, Particle.EXPLOSION_LARGE, 1, 0, 0, 0, 0);

        return Response.OK;
    }
}
