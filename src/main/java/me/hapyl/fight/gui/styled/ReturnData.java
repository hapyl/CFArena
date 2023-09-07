package me.hapyl.fight.gui.styled;

import me.hapyl.spigotutils.module.util.Action;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public interface ReturnData {

    @Nonnull
    String getName();

    @Nonnull
    Action<Player> getAction();

    static ReturnData of(String name, Action<Player> action) {
        return new ReturnData() {
            @Nonnull
            @Override
            public String getName() {
                return name;
            }

            @Nonnull
            @Override
            public Action<Player> getAction() {
                return action;
            }
        };
    }

}
