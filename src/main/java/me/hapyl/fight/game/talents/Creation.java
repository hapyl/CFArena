package me.hapyl.fight.game.talents;

import me.hapyl.fight.annotate.SelfCallable;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;

public interface Creation extends Removable {

    @SelfCallable(false)
    void create(@Nonnull GamePlayer player);

    @Nonnull
    Location getLocation();

    default boolean isCreation(@Nonnull Entity entity) {
        return false;
    }

}
