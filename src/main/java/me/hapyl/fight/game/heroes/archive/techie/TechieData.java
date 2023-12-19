package me.hapyl.fight.game.heroes.archive.techie;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.PlayerData;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class TechieData extends PlayerData implements Iterable<LivingGameEntity> {

    private final Set<LivingGameEntity> bugged;

    public TechieData(GamePlayer player) {
        super(player);

        bugged = Sets.newHashSet();
    }

    public boolean isBugged(@Nonnull LivingGameEntity entity) {
        return bugged.contains(entity);
    }

    public void setBugged(@Nonnull LivingGameEntity entity, boolean flag) {
        if (flag) {
            bugged.add(entity);
        }
        else {
            bugged.remove(entity);
        }
    }

    @Override
    public void remove() {
        bugged.clear();
    }

    @Override
    public void remove(@Nonnull LivingGameEntity entity) {
        bugged.remove(entity);
    }

    public void forEachAndRemove(@Nonnull Function<LivingGameEntity, Boolean> predicate) {
        bugged.removeIf(entity -> {
            return predicate.apply(entity);
        });
    }

    public void removeIf(@Nonnull Predicate<LivingGameEntity> predicate) {
        bugged.removeIf(predicate);
    }

    @Nonnull
    @Override
    public Iterator<LivingGameEntity> iterator() {
        return bugged.iterator();
    }

    public int buggedSize() {
        return bugged.size();
    }

    public void removeDead() {
        bugged.removeIf(LivingGameEntity::isDead);
    }
}
