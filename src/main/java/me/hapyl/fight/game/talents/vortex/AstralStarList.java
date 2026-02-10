package me.hapyl.fight.game.talents.vortex;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.reflect.glowing.GlowingColor;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Location;
import org.bukkit.Sound;

import javax.annotation.Nullable;
import java.util.Set;

public class AstralStarList implements Ticking {

    private final double dotThreshold = 0.963d;

    private final GamePlayer player;
    private final Set<AstralStar> stars;
    
    private AstralStar targetStar;

    public AstralStarList(GamePlayer player) {
        this.player = player;
        this.stars = Sets.newHashSet();
    }

    public int getStarAmount() {
        return stars.size();
    }

    public void summonStar(Location location, VortexStarTalent talent, double healthSacrifice) {
        stars.add(new AstralStar(player, location, talent, healthSacrifice));
    }

    @Nullable
    public AstralStar getTargetStar() {
        return targetStar;
    }

    @Override
    public void tick() {
        AstralStar previousTarget = targetStar;
        targetStar = null;
        double closestDot = 0.0d;

        // Remove dead stars and call onDeath
        // Note that onDeath only called when the star is killed by a player
        CFUtils.removeIf(stars, AstralStar::isDead, star -> {
            star.remove();
            star.onDeath();
        });

        for (AstralStar star : stars) {
            star.tick();
            star.setColor(GlowingColor.WHITE);

            final StarState state = star.getState();

            // Don't allow stated stars be targeted unless it's BEING ATTACKED
            if (state != null && state != StarState.BEING_ATTACKED) {
                continue;
            }

            final double dot = star.dot();

            if (dot >= dotThreshold) {
                final double distance = star.distance();

                if ((targetStar != null && targetStar.distance() < distance) || (targetStar == null || dot >= closestDot)) {
                    targetStar = star;
                    closestDot = dot;
                }
            }
        }

        if (targetStar != null) {
            targetStar.setColor(GlowingColor.GREEN);

            if (previousTarget != targetStar) {
                player.playSound(Sound.ITEM_FLINTANDSTEEL_USE, 0.5f);
            }
        }

    }

    public void removeStar(AstralStar star) {
        if (targetStar == star) {
            targetStar = null;
        }

        stars.remove(star);
        star.remove();
    }

    public void clear() {
        stars.forEach(AstralStar::remove);
        stars.clear();
    }

}

