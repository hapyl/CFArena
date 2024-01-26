package me.hapyl.fight.game.talents;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.spigotutils.module.block.display.DisplayData;
import me.hapyl.spigotutils.module.block.display.DisplayEntity;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class PlayerDisplayCreation extends PlayerCreation {

    private final DisplayData displayData;
    public DisplayEntity entity;

    public PlayerDisplayCreation(@Nonnull CreationTalent talent, @Nonnull GamePlayer player, @Nonnull DisplayData displayData) {
        super(talent, player);

        this.displayData = displayData;
    }

    @Nonnull
    public abstract Location getLocation();

    @OverridingMethodsMustInvokeSuper
    @Override
    public void create(@Nonnull GamePlayer player) {
        entity = displayData.spawn(getLocation());
    }

    /**
     * {@inheritDoc}
     */
    @OverridingMethodsMustInvokeSuper
    @Override
    public void remove() {
        super.remove();
        entity.remove();
    }

}
