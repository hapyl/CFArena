package me.hapyl.fight.game.maps;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public interface Selectable {

    boolean isSelected(@Nonnull Player player);

    void select(@Nonnull Player player);

    default boolean canBeSelected() {
        return true;
    }

}
