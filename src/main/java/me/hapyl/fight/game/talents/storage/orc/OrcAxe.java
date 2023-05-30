package me.hapyl.fight.game.talents.storage.orc;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.EntityData;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class OrcAxe extends InputTalent {
    public OrcAxe() {
        super("Axe");

        setDescription("Equip and prepare your axe for action.");

        leftData.setAction("Spin")
                .setDescription("""
                        Spin constantly for {duration}.
                        """)
                .setDurationSec(3)
                .setCooldownSec(10);

        rightData.setAction("Dash")
                .setDescription("""
                        Dash forward up to 6 blocks, damaging the first enemy hit.
                        """)
                .setCooldownSec(15);

        setItem(Material.NETHERITE_AXE);
    }

    @Nonnull
    @Override
    public Response onLeftClick(Player player) {
        return Response.OK;
    }

    @Nonnull
    @Override
    public Response onRightClick(Player player) {
        final Location startLocation = player.getLocation();
        final Vector vector = startLocation.getDirection();

        vector.setY(0.0d);
        vector.normalize();

        new GameTask() {

            @Override
            public void run() {
                final Location location = player.getLocation();

                if (startLocation.distance(location) >= 6.0d) {
                    executeHit(location);
                    cancel();
                    return;
                }

                final LivingEntity hitEntity = Utils.getNearestLivingEntity(location, 0.5d, living -> living != player);

                if (hitEntity != null) {
                    executeHit(hitEntity.getLocation());
                    cancel();
                    return;
                }

                // Travel
                player.setVelocity(player.getLocation().getDirection().normalize().setY(-1.0d).multiply(0.75d));
            }

            private void executeHit(@Nonnull Location location) {
                EntityData.damageAoE(location, 1.0d, 10.0d, player, EnumDamageCause.ORC_DASH, living -> living != player);

                // Fx
                PlayerLib.spawnParticle(location, Particle.SWEEP_ATTACK, 1, 0.1d, 0.1d, 0.1d, 10);
                PlayerLib.playSound(location, Sound.BLOCK_ANVIL_HIT, 0.75f);
            }
        }.runTaskTimer(0, 1);

        return Response.OK;
    }
}
