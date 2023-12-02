package me.hapyl.fight.game.talents.archive.taker;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.taker.Taker;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class FatalReap extends Talent {

    @DisplayField private final short spiritualBoneGeneration = 2;
    @DisplayField(suffix = "blocks") private final double length = 2.0d;
    @DisplayField(suffix = "%", suffixSpace = false) private final double damagePercent = 20.0d;

    public FatalReap() {
        super("Fatal Reap");

        setDescription("""
                Instantly swipe your scythe to unleash a &8devastating attack&7 that shatters your opponents' &fbones&7, dealing &c{damagePercent}&7 of their &c&ncurrent health&7 as &4damage&7.
                                
                Convert &b{spiritualBoneGeneration}&7 broken bones directly into %s.
                """, Named.SPIRITUAL_BONES);

        setItem(Material.NETHERITE_HOE);
        setCooldownSec(12);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        for (double d = length; d >= -length; d -= 0.2d) {
            final Location location = calculateLocation(player.getEyeLocation(), d);

            Collect.nearbyEntities(location, 1.0d).forEach(victim -> {
                if (player.isSelfOrTeammate(victim)) {
                    return;
                }

                final double health = victim.getHealth();
                final double damage = Math.min(health * (damagePercent / 100), victim.getMaxHealth() / 2);
                victim.damage(damage, player, EnumDamageCause.RIP_BONES);
            });

            player.spawnWorldParticle(location, Particle.SWEEP_ATTACK, 1, 0.0f, 0.0f, 0.0f, 0.0f);
        }

        Heroes.TAKER.getHero(Taker.class).getBones(player).add(spiritualBoneGeneration, true);

        // Fx
        player.playWorldSound(Sound.ENTITY_CAT_HISS, 2.0f);

        return Response.OK;
    }

    private Location calculateLocation(Location location, double d) {
        location.setPitch(0.0f);

        final Vector vector = location.getDirection().normalize();

        location.add(0.0d, d / 2.0d, 0.0d);
        location.add(new Vector(-vector.getZ(), 0.0d, vector.getX()).multiply(Math.sin(d) * 1.5d));
        location.add(vector.multiply(length + (d / 2.0d)));

        return location;
    }

}
