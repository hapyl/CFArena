package me.hapyl.fight.util;

import me.hapyl.eterna.module.chat.Chat;
import org.bukkit.Keyed;

import javax.annotation.Nonnull;

public class KeyedToString {

    private final Keyed keyed;

    private boolean stripMinecraft;
    private byte stringCase;

    KeyedToString(Keyed keyed) {
        this.keyed = keyed;
        this.stripMinecraft = false;
        this.stringCase = 0;
    }

    public KeyedToString stripMinecraft() {
        this.stripMinecraft = true;
        return this;
    }

    public KeyedToString lowercase() {
        this.stringCase = 0;
        return this;
    }

    public KeyedToString uppercase() {
        this.stringCase = 1;
        return this;
    }

    public KeyedToString capitalize() {
        this.stringCase = 2;
        return this;
    }

    @Override
    public String toString() {
        String key = keyed.key().key().toString();

        if (stripMinecraft) {
            key = key.replace("minecraft:", "");
        }

        key = switch (stringCase) {
            case 0 -> key.toLowerCase();
            case 1 -> key.toUpperCase();
            case 2 -> Chat.capitalize(key);
            default -> key;
        };

        return key;
    }

    @Nonnull
    public static KeyedToString of(@Nonnull Keyed keyed) {
        return new KeyedToString(keyed);
    }

}
