package me.hapyl.fight.game.collectible.relic;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;

public class RelicRunnable implements Runnable {

    private final RelicHunt relicHunt;

    public RelicRunnable(RelicHunt relicHunt) {
        this.relicHunt = relicHunt;
    }

    @Override
    public void run() {
        relicHunt.forEach((id, relic) -> {
            final Location location = relic.getLocation().toLocation().add(0.0d, 0.5d, 0.0d);
            final Collection<Entity> nearby = relic.getLocation().getWorld().getNearbyEntities(location, 10, 10, 10, filter -> filter instanceof Player);

            for (Entity entity : nearby) {
                if (!(entity instanceof Player player)) {
                    continue;
                }

                if (relic.hasFound(player)) {
                    player.spawnParticle(Particle.CRIT, location, 3, 0.25d, 0.15d, 0.25d, 0.03f);
                } else {
                    player.spawnParticle(Particle.ENCHANTED_HIT, location, 2, 0.25d, 0.15d, 0.25d, 0.05f);
                    player.spawnParticle(Particle.WITCH, location, 3, 0.25d, 0.15d, 0.25d, 0.03f);
                }
            }
        });
    }
}
