package me.hapyl.fight.game.talents.archive.librarian;

import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class EntityDarkness extends LibrarianTalent {
    public EntityDarkness() {
        super("Darkness Spell");

        addDescription("Launches a sprite of darkness, dealing <scaled> damage and applying paranoia. This spell has 3 charges.");
        setCooldown(10);
        setItem(Material.WITHER_ROSE);
    }

    @Override
    public Response executeGrimoire(@Nonnull GamePlayer player) {
        final Location location = player.getEyeLocation();
        final Vector direction = location.getDirection();

        for (double i = 0.0d; i < 5.0d; i += 0.5d) {
            final double x = direction.getX() * i;
            final double y = direction.getY() * i;
            final double z = direction.getZ() * i;

            location.add(x, y, z);
            if (location.getBlock().getType().isOccluding()) {
                PlayerLib.playSound(location, Sound.BLOCK_STONE_STEP, 0.0f);
                break;
            }

            PlayerLib.spawnParticle(location, Particle.SQUID_INK, 1, 0.1, 0.05, 0.1, 0);
            Collect.nearbyEntities(location, 1.25d).forEach(victim -> {
                if (victim.equals(player)) {
                    return;
                }

                victim.damage(getCurrentValue(player), player, EnumDamageCause.DARKNESS);
                victim.addEffect(Effects.PARANOIA, 20);

                // Fx
                PlayerLib.playSound(location, Sound.BLOCK_STONE_STEP, 0.0f);
            });
        }

        // Fx
        PlayerLib.playSound(player.getLocation(), Sound.ENTITY_SQUID_SQUIRT, 1.25f);

        return Response.OK;
    }

    @Override
    public double[] getValues() {
        return new double[] { 6, 8, 10, 12 };
    }

    @Override
    public int getGrimoireCd() {
        return 25;
    }
}
