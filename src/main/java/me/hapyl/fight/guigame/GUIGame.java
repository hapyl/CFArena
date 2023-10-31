package me.hapyl.fight.guigame;

import me.hapyl.fight.util.Described;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public abstract class GUIGame implements Described {

    private final String name;
    private final String description;

    public GUIGame(@Nonnull String name, @Nonnull String description) {
        this.name = name;
        this.description = description;
    }

    @Nonnull
    public abstract GUIGameInstance createGameInstance(@Nonnull Player[] players);

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }
}
