package me.hapyl.fight.game.dot;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.util.Ticking;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

public class DotInstanceList implements Ticking, Iterable<DotInstance> {

    private final DamageOverTime enumDot;
    private final LivingGameEntity entity;
    private final List<DotInstance> instances;

    public DotInstanceList(@Nonnull DamageOverTime enumDot, @Nonnull LivingGameEntity entity) {
        this.enumDot = enumDot;
        this.entity = entity;
        this.instances = Lists.newArrayList();
    }

    public void add(@Nonnull DotInstance instance) {
        instances.add(instance);
    }

    public int getStacks() {
        return instances.size();
    }

    @Override
    public void tick() {
        final Dot dot = enumDot.get();

        // Remove done instances
        instances.forEach(DotInstance::tick);
        instances.removeIf(DotInstance::isDone);

        // Don't affect anything if not valid period
        if (entity.aliveTicks() % dot.getPeriod() != 0) {
            return;
        }

        // Affect
        dot.affect(entity);

        // Do the ticking
        DotInstance latestInstance = null;
        double damage = 0.0d;

        int index = 0;
        for (DotInstance instance : instances) {
            // Calculate damage
            // Only add damage up to max stacks of this dot
            if (index++ < dot.getMaxStacks()) {
                damage += dot.getDamage();
            }

            // Find the latest instance
            // The latest instance damager will be used as a damager for this dot
            if (latestInstance == null || instance.getStartedAt() > latestInstance.getStartedAt()) {
                latestInstance = instance;
            }
        }

        // Deal damage
        if (damage > 0) {
            entity.setLastDamager(latestInstance.getEntity());
            entity.damageTick(damage, dot.getCause(), dot.getPeriod());
        }
    }

    @Nonnull
    @Override
    public Iterator<DotInstance> iterator() {
        return instances.iterator();
    }

    public void clear() {
        entity.getData().getDotMap().clear();
    }
}
