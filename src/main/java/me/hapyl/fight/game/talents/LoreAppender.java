package me.hapyl.fight.game.talents;

import me.hapyl.spigotutils.module.inventory.ItemBuilder;

import javax.annotation.Nonnull;

public interface LoreAppender {

    void appendLore(@Nonnull ItemBuilder builder);

}
