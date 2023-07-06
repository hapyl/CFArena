package me.hapyl.fight.game.delivery;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public abstract class Delivery {

    private final String name;
    private final String message;

    public Delivery(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public abstract void deliver(@Nonnull Player player);
}
