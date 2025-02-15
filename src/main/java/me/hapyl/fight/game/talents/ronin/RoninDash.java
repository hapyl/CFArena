package me.hapyl.fight.game.talents.ronin;

import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingStepGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class RoninDash extends Talent {

    @DisplayField private final double strength = 1.25d;
    @DisplayField private final double maxDistance = 6.0d;
    @DisplayField private final double radius = 0.5d;
    @DisplayField private final double damage = 2.0d;

    @DisplayField private final double speedDecrease = 20;
    @DisplayField private final int speedDecreaseDuration = 50;

    @DisplayField private final int maxDuration = 30;

    public RoninDash(@Nonnull Key key) {
        super(key, "Ronin Dash");

        setDescription("""
                Dash a short distance &nforward&7 with your katana, gaining &binvulnerability&7 for a short period.
                &8&o;;Enemies you dash through take damage and are slowed.
                """);

        setType(TalentType.MOVEMENT);
        setItem(Material.RABBIT_FOOT);

        setCooldownSec(6);
        setDuration(5);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        if (!player.isOnGround()) {
            return Response.error("Must be grounded!");
        }

        final Location location = player.getLocation();
        final Vector vector = location.getDirection();
        vector.multiply(strength);
        vector.setY(-BukkitUtils.GRAVITY);

        player.setInvulnerable(true);

        new TickingStepGameTask(10) {

            @Override
            public boolean tick(int tick, int step) {
                final double distance = LocationHelper.distanceSquared(player.getLocation(), location, Axis.X, Axis.Z);

                if (tick >= maxDuration || distance >= maxDistance) {
                    player.setInvulnerable(false);
                    return true;
                }

                player.setVelocity(vector);

                // Affect
                Collect.nearbyEntities(player.getLocation(), 1.5d, player::isNotSelfOrTeammate)
                        .forEach(entity -> {
                            entity.damage(damage, player, DamageCause.ENTITY_ATTACK);

                            final EntityAttributes attributes = entity.getAttributes();
                            attributes.decreaseTemporary(
                                    Temper.RONIN,
                                    AttributeType.SPEED,
                                    AttributeType.SPEED.scaleDown(speedDecrease),
                                    speedDecreaseDuration,
                                    player
                            );

                            // Fx
                        });

                return false;
            }

        }.runTaskTimer(0, 1);

        // Fx
        player.playWorldSound(Sound.ENTITY_BREEZE_HURT, 1.25f);
        player.playWorldSound(Sound.ENTITY_BREEZE_IDLE_AIR, 1.75f);
        player.playWorldSound(Sound.ENTITY_BREEZE_SHOOT, 0.75f);

        return Response.OK;
    }

}
