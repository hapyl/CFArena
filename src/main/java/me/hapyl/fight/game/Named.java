package me.hapyl.fight.game;

import me.hapyl.fight.game.color.Color;
import net.md_5.bungee.api.ChatColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An enum collection of named objects, such as abilities etc.
 */
public enum Named {

    BERSERK("💢", "Berserk", Color.DARK_RED),
    CURSE_OF_GREED("\uD83E\uDDFF", "Curse of Greed", Color.GOLD),
    SHADOW_ENERGY("🕳", "Shadow Energy", Color.PURPLE_SHADOW),
    ASTRAL_SPARK("⚡", "Astral Spark", Color.YELLOW),
    SHADOWSTRIKE("\uD83D\uDCA5", "Shadowstrike", Color.MEDIUM_STALE_BLUE),
    SPIRITUAL_BONES("🦴", "Spiritual Bones", Color.WHITE_BLUE_GRAY),
    STANCE_RANGE("🏹", "Range Stance", Color.STANCE_RANGE),
    STANCE_MELEE("⚔", "Melee Stance", Color.STANCE_MELEE),
    RIPTIDE(ChatColor.BOLD + "\uD83D\uDCA6", "Riptide", Color.RIPTIDE),
    BUG(ChatColor.BOLD + "🐜", "Disruptive Bug", Color.WHITE),
    ENERGY("※", "Energy", Color.AQUA),
    OVERHEAL(ChatColor.DARK_GREEN + "⚕", "Overheal", Color.GREEN),
    SECOND_WIND("&l\uD83E\uDD8B", "Second Wind", Color.WHITE),
    FEROCIOUS_STRIKE("\uD83C\uDF00", "Ferocious Strike", Color.DARK_RED),
    REFRACTION(ChatColor.BOLD + "⛺", "Refraction", Color.SKY_BLUE),
    WITHER_ROSE(ChatColor.DARK_GRAY + "\uD83C\uDF39", "Wither Rose", Color.WITHERS),
    MOONLIT_ENERGY(ChatColor.YELLOW + "&e☄", "Moonlit Energy", Color.MOON),
    ETHEREAL_SPIRIT(ChatColor.AQUA + "\uD83D\uDCAB", "Ethereal Spirit", Color.ETHEREAL),
    THE_CHAOS("", "The Chaos", Color.BLUE),

    ;

    private final String character;
    private final String name;
    private final Color color;

    Named(@Nonnull String character, @Nonnull String name, @Nonnull Color color) {
        this.character = character;
        this.color = color;
        this.name = name;
    }

    @Nonnull
    public String getCharacterColored() {
        return color + character;
    }

    @Nonnull
    public String getCharacter() {
        return character;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public Color getColor() {
        return color;
    }

    @Nonnull
    public String toString(@Nullable String prefix, @Nullable String suffix) {
        return prefix + this + suffix;
    }

    @Override
    public String toString() {
        return color + character + " " + color + name + "&7";
    }

    @Nonnull
    public String toStringRaw() {
        return character + " " + name;
    }

    @Nonnull
    public String getCharacterNoColor() {
        return ChatColor.stripColor(character);
    }
}
