package me.hapyl.fight.game.talents.tamer;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.fight.fx.Riptide;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.TickingGameTask;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

public class EntityLevitate<T extends LivingGameEntity> extends TickingGameTask {

    private static final int effectLiftDuration = 15;

    public final T entity;
    public final Location initialLocation;
    public final Riptide riptide;

    private final int duration;

    public EntityLevitate(T entity, int duration) {
        this.entity = entity;
        this.initialLocation = entity.getLocation().clone();
        this.riptide = new Riptide(entity.getLocation());
        this.duration = duration;

        onStart();
        runTaskTimer(0, 1);
    }

    @Override
    public final void onTaskStop() {
        riptide.remove();
    }

    @EventLike
    public void onStart() {
    }

    @EventLike
    public void onTick() {
    }

    @Override
    public void run(int tick) {
        if (tick > duration || entity.isDeadOrRespawning()) {
            cancel();
            return;
        }

        final Location location = entity.getLocation();

        // Fx
        entity.spawnWorldParticle(location, Particle.POOF, 1, 0.1d, 0.0d, 0.1d, 0.025f);
        entity.playWorldSound(initialLocation, Sound.ENTITY_EGG_THROW, 0.5f + (1.5f / duration * tick));

        onTick();
    }

}