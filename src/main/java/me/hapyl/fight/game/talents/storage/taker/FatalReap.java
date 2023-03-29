package me.hapyl.fight.game.talents.storage.taker;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.util.Utils;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class FatalReap extends Talent {

    @DisplayField private final short spiritualBoneGeneration = 2;
    @DisplayField(suffix = "blocks") private final double length = 2.0d;
    @DisplayField(suffix = "%") private final double damagePercent = 20.0d;

    public FatalReap() {
        super("Fatal Reap");

        setDescription(
                "Instantly charge opponents' bones with a powerful scythe swipe and unleash a devastating attack that shatters their bones, dealing %s%% of their current health as damage.",
                damagePercent
        );

        setItem(Material.NETHERITE_HOE);
        setCdSec(12);
    }

    @Override
    public Response execute(Player player) {
        for (double d = length; d >= -length; d -= 0.2d) {
            final Location location = calculateLocation(player.getEyeLocation(), d);

            Utils.getEntitiesInRange(location, 0.75d).forEach(victim -> {
                if (GameTeam.isSelfOrTeammate(player, victim)) {
                    return;
                }

                double health = victim.getHealth();

                if (victim instanceof Player) {
                    health = GamePlayer.getPlayer((Player) victim).getHealth();
                }

                final double damage = Math.min(health * 0.2d, 100.0d);
                GamePlayer.damageEntity(victim, damage, player, EnumDamageCause.RIP_BONES);
            });

            PlayerLib.spawnParticle(location, Particle.CRIT, 1, 0.0f, 0.0f, 0.0f, 0.0f);
        }

        Heroes.Handle.TAKER.getBones(player).add(spiritualBoneGeneration, true);

        // Fx
        PlayerLib.playSound(player.getLocation(), Sound.ENTITY_CAT_HISS, 2.0f);

        return Response.OK;
    }

    private Location calculateLocation(Location location, double d) {
        location.setPitch(0.0f); // Don't calculate pitch

        final Vector vector = location.getDirection().normalize();

        location.add(0.0d, d / 1.5d, 0.0d); // Calculate y offset
        location.add(new Vector(-vector.getZ(), 0.0d, vector.getX()).multiply(Math.sin(d) * 1.5d)); // calculate x and z offset
        location.add(vector.multiply(length + (d / 2.0d))); // calculate direction

        return location;
    }

    @Override
    public FatalReap getHandle() {
        return (FatalReap) this;
    }
}
