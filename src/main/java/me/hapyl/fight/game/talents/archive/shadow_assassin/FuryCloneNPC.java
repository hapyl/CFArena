package me.hapyl.fight.game.talents.archive.shadow_assassin;

import me.hapyl.fight.game.TalentReference;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class FuryCloneNPC extends CloneNPC implements TalentReference<ShadowAssassinClone> {

    protected final Location attackLocation;
    private final ShadowAssassinClone talent;

    public FuryCloneNPC(ShadowAssassinClone talent, Location location, GamePlayer player, Location attackLocation, int delay) {
        super(null, prepareLocation(location), player);

        this.talent = talent;
        this.attackLocation = attackLocation;

        GameTask.runLater(() -> {
            lookAt(attackLocation);
            swingMainHand();

            final List<LivingGameEntity> entities = Collect.nearbyEntities(attackLocation, talent.furyAoeDistance)
                    .stream()
                    .filter(entity -> !player.isSelfOrTeammate(entity))
                    .toList();

            onAttack(entities);

            // Fx
            drawParticles(attackLocation, Particle.SPELL_WITCH, 2, 0.0f);
            drawParticles(attackLocation, Particle.SMOKE_LARGE, 2, 0.015f);
        }, delay);
        GameTask.runLater(this::disappear, delay + 10);
    }

    public abstract void onAttack(@Nonnull List<LivingGameEntity> entities);

    @Nonnull
    @Override
    public ShadowAssassinClone getTalent() {
        return talent;
    }

    @Override
    public final void remove() {
        remove0();
    }

    protected void drawParticles(Location location, Particle particle, int amount, float pitch) {
        Geometry.drawCircleAnchored(location, talent.furyAoeDistance, Quality.HIGH, new WorldParticle(particle, amount, 0, 0, 0, pitch));
    }

    private static Location prepareLocation(Location location) {
        final Vector direction = location.getDirection();

        direction.multiply(-1);
        location.setDirection(direction);
        location.setPitch(0.0f);

        return GamePlayer.anchorLocation(location);
    }

}
