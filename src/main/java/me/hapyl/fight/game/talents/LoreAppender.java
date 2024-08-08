package me.hapyl.fight.game.talents;

import me.hapyl.eterna.module.inventory.ItemBuilder;

import javax.annotation.Nonnull;

public interface LoreAppender {

    void appendLore(@Nonnull ItemBuilder builder);

}
