package me.hapyl.fight.game.talents.archive.vortex;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.Ticking;
import me.hapyl.fight.util.Collect;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nullable;
import java.util.Set;

public class AstralStars implements Ticking {

    private final double pickupDistance = 3.0d;
    private final double dotThreshold = 0.963d;

    private final GamePlayer player;
    private final Set<AstralStar> stars;
    private AstralStar targetStar;

    public AstralStars(GamePlayer player) {
        this.player = player;
        this.stars = Sets.newHashSet();
    }

    public int getStarAmount() {
        return stars.size();
    }

    public void summonStar(Location location) {
        stars.add(new AstralStar(player, location));
    }

    @Nullable
    public AstralStar getFirstStarToPickup() {
        for (AstralStar star : stars) {
            if (star.getLocation().distance(player.getLocation()) <= pickupDistance) {
                return star;
            }
        }

        return null;
    }

    @Nullable
    public AstralStar getTargetStar() {
        return targetStar;
    }

    @Override
    public void tick() {
        targetStar = null;
        double closestDot = 0.0d;

        for (AstralStar star : stars) {
            star.setColor(ChatColor.WHITE);

            final double dot = star.dot();

            if (dot >= dotThreshold) {
                final double distance = star.distance();

                if ((targetStar != null && targetStar.distance() < distance) || (targetStar == null || dot >= closestDot)) {
                    targetStar = star;
                    closestDot = dot;
                }
            }

            // Display star particles for other players
            final Location location = star.getLocation();

            Collect.aliveGamePlayers().forEach(other -> {
                if (player.equals(other)) {
                    return;
                }

                other.spawnParticle(location, Particle.CRIT, 3, 0.1d, 0.1d, 0.1d, 0.01f);
            });
        }

        if (targetStar != null) {
            targetStar.setColor(ChatColor.GREEN);
        }
    }

    public void removeStar(AstralStar entity) {
        stars.remove(entity);
        entity.remove();
    }

    public void clear() {
        stars.forEach(AstralStar::remove);
        stars.clear();
    }

}

