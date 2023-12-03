package me.hapyl.fight.game.talents.archive.engineer;

import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.Response;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class EngineerSentry extends EngineerTalent {

    public EngineerSentry() {
        super("Sentry", 5);

        setDescription("""
                Create a sentry.
                """);
    }

    @Override
    public Construct create(Player player, Location location) {
        return new Construct(player, location) {
            @Override
            public void onCreate() {
                Debug.info("create " + getName());
            }

            @Override
            public ImmutableArray<Double> healthScaled() {
                return ImmutableArray.of(20d, 40d, 60d, 80d);
            }

            @Override
            public ImmutableArray<Integer> durationScaled() {
                return ImmutableArray.of(10, 20, 30, 40);
            }

            @Override
            public void onDestroy() {
                Debug.info("destroy " + getName());
            }

            @Override
            public void onTick() {
                PlayerLib.spawnParticle(stand.getLocation(), Particle.CRIT, 1);
            }
        };
    }

    @Nonnull
    @Override
    public Response predicate(Player player, Location location) {
        return Response.OK;
    }
}
