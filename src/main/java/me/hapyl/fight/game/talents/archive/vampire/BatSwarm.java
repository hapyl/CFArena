package me.hapyl.fight.game.talents.archive.vampire;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Bat;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.Set;

public class BatSwarm extends Talent {

    @DisplayField private final short batCount = 15; // each hit removes 1 bat

    public BatSwarm() {
        super("Swarm", "Launch a swarm of bats at your enemies, rapidly dealing damage and blinding them for a short duration.");

        setItem(Material.FLINT);
        setDurationSec(5);
        setCooldownSec(batCount);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Set<Bat> bats = Sets.newConcurrentHashSet();

        for (int i = 0; i < batCount; i++) {
            bats.add(createBat(player));
        }

        new GameTask() {
            private int duration = getDuration();

            @Override
            public void run() {
                if (bats.isEmpty() || duration <= 0) {
                    this.cancel();
                    return;
                }

                for (Bat bat : bats) {
                    final Location location = bat.getLocation();
                    bat.teleport(location.add(location.getDirection().multiply(0.5d)));

                    if (bat.getLocation().getBlock().getType().isOccluding() || bat.isDead()) {
                        bats.remove(bat);
                        bat.remove();
                        continue;
                    }

                    Collect.nearbyEntities(bat.getLocation(), 1.0d).forEach(entity -> {
                        if (entity.equals(player) || entity instanceof Bat || entity.getNoDamageTicks() > 0) {
                            return;
                        }

                        entity.damageTick(2.0d, player, EnumDamageCause.SWARM, 1);
                        entity.addPotionEffect(PotionEffectType.BLINDNESS, 20, 1);

                        bats.remove(bat);
                        bat.remove();
                    });
                }

                duration--;
            }
        }.runTaskTimer(0, 1);

        return Response.OK;
    }

    public Bat createBat(GamePlayer player) {
        final Location location = player.getLocation().add(0.0d, 0.5d, 0.0d);
        location.add(location.getDirection().setY(0.0d).multiply(2.0d));

        // Randomize location
        final double randomX = ThreadRandom.nextDouble(-0.5d, 0.5d);
        final double randomY = ThreadRandom.nextDouble(-0.5d, 0.5d);
        final double randomZ = ThreadRandom.nextDouble(-0.5d, 0.5d);

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
