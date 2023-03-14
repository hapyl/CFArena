package me.hapyl.fight.game.talents.storage.vampire;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;

public class BatSwarm extends Talent {

    private final int BAT_COUNT = 15; // each hit removes 1 bat

    public BatSwarm() {
        super("Swarm");

        setItem(Material.FLINT);
        setDescription(
                "Launch a swarm of bats at your enemies, rapidly dealing damage and blinding them for a short duration."
        );
        setDurationSec(5);
        setCdSec(BAT_COUNT);
    }

    @Override
    public Response execute(Player player) {
        final Set<Bat> bats = Sets.newConcurrentHashSet();

        for (int i = 0; i < BAT_COUNT; i++) {
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

                    Utils.getEntitiesInRange(bat.getLocation(), 1.0d).forEach(entity -> {
                        if (entity == player || entity instanceof Bat || entity.getNoDamageTicks() > 0) {
                            return;
                        }

                        GamePlayer.damageEntityTick(entity, 2.0d, player, EnumDamageCause.SWARM, 1);
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 1));

                        bats.remove(bat);
                        bat.remove();
                    });
                }

                duration--;
            }
        }.runTaskTimer(0, 1);

        return Response.OK;
    }

    public Bat createBat(Player player) {
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
