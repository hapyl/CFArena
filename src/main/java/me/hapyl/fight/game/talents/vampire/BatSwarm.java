package me.hapyl.fight.game.talents.vampire;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.player.PlayerTickingGameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Bat;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BatSwarm extends Talent {

    @DisplayField private final short batCount = 15; // each hit removes 1 bat

    @DisplayField private final double batSpeed = 0.6d;
    @DisplayField private final double damage = 2.0d;
    @DisplayField private final double hitboxSize = 1.5d;

    @DisplayField private final double healthDecrease = 10;
    @DisplayField private final int impairDuration = Tick.fromSecond(8);
    @DisplayField private final int affectPeriod = 2;

    private final TemperInstance temperInstance = Temper.SWARM.newInstance()
            .decrease(AttributeType.MAX_HEALTH, healthDecrease)
            .message("&4\uD83C\uDF36 Ouch! &8(&c-%s ❤&8)".formatted(healthDecrease));

    public BatSwarm(@Nonnull Key key) {
        super(key, "Swarm");

        setDescription("""
                Launch a swarm of &8bats&7 forward, that deals &erapid &cdamage&7, &8blinds&7, and &edecreases&7 %s.
                """.formatted(AttributeType.MAX_HEALTH)
        );

        setItem(Material.FLINT);
        setType(TalentType.IMPAIR);

        setDurationSec(5);
        setCooldownSec(batCount);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Set<Bat> bats = ConcurrentHashMap.newKeySet();

        // Create bats
        for (int i = 0; i < batCount; ++i) {
            bats.add(createBat(player));
        }

        new PlayerTickingGameTask(player) {
            @Override
            public void onTaskStop() {
                CFUtils.clearCollection(bats);
            }

            @Override
            public void onTaskStopBecauseOfDeath() {
                onTaskStop();
            }

            @Override
            public void run(int tick) {
                if (bats.isEmpty() || tick >= getDuration()) {
                    cancel();
                    return;
                }

                if (!modulo(affectPeriod)) {
                    return;
                }

                bats.forEach(bat -> {

                    // Entity collision
                    Collect.nearbyEntities(bat.getLocation(), hitboxSize, player::isNotSelfOrTeammate).forEach(entity -> {

                        entity.damage(damage, player, DamageCause.SWARM);
                        entity.addEffect(Effects.BLINDNESS, 1, 20);

                        // Decrease health
                        temperInstance.temper(entity, impairDuration, player);

                        removeBat(bat);
                    });

                    // Block collision
                    if (bat.getLocation().getBlock().getType().isOccluding() || bat.isDead()) {
                        removeBat(bat);
                        return;
                    }

                    // Move forward
                    final Location location = bat.getLocation();
                    final Vector direction = location.getDirection();

                    bat.teleport(location.add(direction.multiply(batSpeed)));
                });
            }

            private void removeBat(Bat bat) {
                final Location location = bat.getLocation();

                bats.remove(bat); // concurrent set, removing is fine
                bat.remove();

                // Fx
                player.spawnWorldParticle(location, Particle.SMOKE, 10, 0.25d, 0.1d, 0.25d, 0.0f);
            }
        }.runTaskTimer(0, 1);

        // Fx
        final Location location = player.getLocation();

        player.playWorldSound(location, Sound.ENTITY_BAT_TAKEOFF, 0.75f);
        player.playWorldSound(location, Sound.ENTITY_BAT_TAKEOFF, 1.25f);
        player.playWorldSound(location, Sound.ENTITY_BAT_TAKEOFF, 1.75f);
        player.playWorldSound(location, Sound.ENTITY_BAT_TAKEOFF, 0.25f);

        return Response.OK;
    }

    public Bat createBat(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        location.add(location.getDirection().setY(0.0d).multiply(2.0d));

        // Randomize location
        final double randomX = player.random.nextDoubleBool(1d);
        final double randomY = player.random.nextDoubleBool(1d);
        final double randomZ = player.random.nextDoubleBool(1d);

        location.add(randomX, randomY, randomZ);

        return Entities.BAT.spawn(location, bat -> {
            bat.setInvulnerable(true);
            bat.setAI(false);
            bat.setGravity(false);
            bat.setAwake(true);
            bat.setPersistent(true);
        });
    }
}
