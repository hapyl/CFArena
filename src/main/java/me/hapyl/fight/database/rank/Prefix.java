package me.hapyl.fight.database.rank;

import me.hapyl.fight.game.color.Color;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record Prefix(@Nullable Color color, @Nonnull String prefix) {
    
    @Nonnull
    @Override
    public String toString() {
        if (color == null) {
            return prefix;
        }
        
        return color.bold() + prefix;
    }
    
    public boolean isEmpty() {
        return prefix.isEmpty();
    }
    
    @Nonnull
    public Component toComponent() {
        return Component.text(prefix, color, TextDecoration.BOLD);
    }
}
