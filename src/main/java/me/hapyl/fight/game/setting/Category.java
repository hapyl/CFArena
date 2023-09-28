package me.hapyl.fight.game.setting;

import javax.annotation.Nonnull;

public enum Category {

    GAMEPLAY("Gameplay", "Gameplay related settings."),
    CHAT("Chat", "Chat related settings.");

    private final String name;
    private final String description;

    Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public String getDescription() {
        return description;
    }
}
