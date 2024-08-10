package me.hapyl.fight.game.entity;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.eterna.module.util.Wrap;
import me.hapyl.fight.util.Ticking;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;

public class EntityTicker implements Ticking {

    /**
     * Number of ticks where entity cannot be affected by effects.
     */
    protected final Tick noCCTicks;

    /**
     * Number of ticks where entity cannot be damaged.
     */
    protected final Tick noDamageTicks;

    /**
     * Number of ticks entity has been alive for.
     */
    protected final Tick aliveTicks;

    /**
     * Number of ticks entity has been in water for.
     */
    protected final Tick inWaterTicks;

    /**
     * Numbers of ticks player has been sneaking for.
     * <br>
     * Only applicable to {@link GamePlayer}.
     */
    protected final Tick sneakTicks;

    private final LivingGameEntity entity;
    private final List<Tick> tickerList;

    EntityTicker(@Nonnull LivingGameEntity entity) {
        this.entity = entity;
        this.tickerList = Lists.newArrayList();

        // Add ticks
        this.noCCTicks = add("cc", TickDirection.DOWN);
        this.noDamageTicks = add("no_damage", TickDirection.DOWN);
        this.aliveTicks = add("alive", TickDirection.UP);
        this.inWaterTicks = add("in_water", TickDirection.UP, LivingGameEntity::isInWater);
        this.sneakTicks = add("sneak", TickDirection.UP, predicate -> {
            return predicate instanceof GamePlayer player && player.isSneaking();
        });
    }

    @Override
    public void tick() {
        tickerList.forEach(Tick::tick);
    }

    @Override
    public String toString() {
        return CollectionUtils.wrapToString(tickerList, Wrap.DEFAULT);
    }

    private Tick add(String name, TickDirection direction, Predicate<LivingGameEntity> predicate) {
        final EntityTick tick = new EntityTick(entity, name, direction, predicate);

        tickerList.add(tick);
        return tick;
    }

    private Tick add(String name, TickDirection direction) {
        return add(name, direction, t -> true);
    }
}
