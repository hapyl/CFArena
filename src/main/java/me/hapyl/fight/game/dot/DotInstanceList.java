package me.hapyl.fight.game.dot;

import com.google.common.collect.Lists;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.entity.LivingGameEntity;

import javax.annotation.Nonnull;
import java.util.List;

public class DotInstanceList {

    private final Dots enumDot;
    private final LivingGameEntity entity;
    private final List<DotInstance> instances;

    public DotInstanceList(@Nonnull Dots enumDot, @Nonnull LivingGameEntity entity) {
        this.enumDot = enumDot;
        this.entity = entity;
        this.instances = Lists.newArrayList();
    }

    public void tickIfShould(int tick) {
        if (tick > 0 && tick % enumDot.get().getPeriod() == 0) {
            tick();
        }
    }

    private void tick() {
        final Dot dot = enumDot.get();

        // Affect
        dot.affect(entity);

        // Remove done instances
        instances.removeIf(DotInstance::isDone);

        DotInstance latestInstance = null;
        double damage = 0.0d;

        int index = 0;
        for (DotInstance instance : instances) {
            // Tick down
            instance.tick();

            // Affect

            // Calculate damage
            if (index++ <= dot.getMaxStacks()) {
                damage += dot.getDamage();
            }

            // Find latest instance
            if (latestInstance == null || instance.getStartedAt() > latestInstance.getStartedAt()) {
                latestInstance = instance;
            }
        }

        // Damage if needed
        if (damage > 0) {
            final DamageInstance damageInstance = new DamageInstance(entity, damage);
            // FIXME (hapyl): 021, Nov 21: I'm 99.9% sure intelliJ is wrong
            damageInstance.damager = latestInstance != null ? latestInstance.getApplier() : null;

            entity.decreaseHealth(damageInstance);
        }
    }
}
