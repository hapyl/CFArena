package me.hapyl.fight.game.heroes.techie;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.PlayerData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class TechieData extends PlayerData implements Iterable<LivingGameEntity> {

    private final Map<LivingGameEntity, Set<BugType>> bugged;

    public TechieData(GamePlayer player) {
        super(player);

        bugged = Maps.newHashMap();
    }

    @Nonnull
    public Set<BugType> getBugs(@Nonnull LivingGameEntity entity) {
        return bugged.computeIfAbsent(entity, fn -> Sets.newHashSet());
    }

    public boolean isBugged(@Nonnull LivingGameEntity entity) {
        return bugged.containsKey(entity);
    }

    @Nullable
    public BugType bugRandomly(@Nonnull LivingGameEntity entity) {
        final BugType randomBug = BugType.random(getBugs(entity));

        if (randomBug == null) {
            return null;
        }

        addBug(entity, randomBug);
        return randomBug;
    }

    public void addBug(@Nonnull LivingGameEntity entity, @Nonnull BugType type) {
        getBugs(entity).add(type);

        // Apply temper
        type.getTemper().temper(entity, Constants.INFINITE_DURATION, player);
    }

    @Override
    public void remove() {
        bugged.keySet().forEach(entity -> entity.getAttributes().resetTemper(Temper.SABOTEUR));
        bugged.clear();
    }

    @Override
    public void remove(@Nonnull LivingGameEntity entity) {
        entity.getAttributes().resetTemper(Temper.SABOTEUR);
        bugged.remove(entity);
    }

    public void forEachAndRemove(@Nonnull Function<LivingGameEntity, Boolean> predicate) {
        bugged.keySet().removeIf(entity -> {
            final boolean removed = predicate.apply(entity);

            if (removed) {
                entity.getAttributes().resetTemper(Temper.SABOTEUR);
            }

            return removed;
        });
    }

    @Nullable
    public LivingGameEntity getClosestEntity() {
        LivingGameEntity closestEntity = null;
        double closestDistance = Double.MAX_VALUE;

        for (LivingGameEntity entity : this) {
            final double distance = player.getLocation().distance(entity.getLocation());

            if (closestEntity == null || closestDistance < distance) {
                closestEntity = entity;
                closestDistance = distance;
            }
        }

        return closestEntity;
    }

    @Nonnull
    @Override
    public Iterator<LivingGameEntity> iterator() {
        return bugged.keySet().iterator();
    }

    public int buggedSize() {
        return bugged.size();
    }

    public void removeDead() {
        bugged.keySet().removeIf(LivingGameEntity::isDead);
    }
}
