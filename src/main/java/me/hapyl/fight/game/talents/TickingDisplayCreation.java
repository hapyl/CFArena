package me.hapyl.fight.game.talents;

import me.hapyl.fight.annotate.MustCallSuper;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.spigotutils.module.block.display.DisplayData;
import me.hapyl.spigotutils.module.block.display.DisplayEntity;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public abstract class TickingDisplayCreation extends TickingCreation {

    private final DisplayData displayData;
    public DisplayEntity entity;

    public TickingDisplayCreation(@Nonnull DisplayData displayData) {
        this.displayData = displayData;
    }

    @Nonnull
    public abstract Location getLocation();

    @MustCallSuper
    @Override
    public void create(@Nonnull GamePlayer player) {
        entity = displayData.spawn(getLocation());
    }

    /**
     * {@inheritDoc}
     */
    @MustCallSuper
    @Override
    public void remove() {
        entity.remove();
    }

}
