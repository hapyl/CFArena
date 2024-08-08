package me.hapyl.fight.game.color;

import me.hapyl.eterna.module.chat.Gradient;
import net.md_5.bungee.api.ChatColor;

import javax.annotation.Nonnull;

/**
 * Represents a color flag that a {@link Color} may, or may not have.
 */
public enum ColorFlag {

    /**
     * The color has bold text.
     */
    BOLD(ChatColor.BOLD) {
        @Override
        public void gradient(@Nonnull Gradient gradient) {
            gradient.makeBold();
        }
    },
    /**
     * The color has italic text.
     */
    ITALIC(ChatColor.ITALIC) {
        @Override
        public void gradient(@Nonnull Gradient gradient) {
            gradient.makeItalic();
        }
    },
    /**
     * The color text has an underline.
     */
    UNDERLINE(ChatColor.UNDERLINE) {
        @Override
        public void gradient(@Nonnull Gradient gradient) {
            gradient.makeUnderline();
        }
    },
    /**
     * The color text is strikethrough.
     */
    STRIKETHROUGH(ChatColor.STRIKETHROUGH) {
        @Override
        public void gradient(@Nonnull Gradient gradient) {
            gradient.makeStrikethrough();
        }
    },
    /**
     * The color is obfuscated.
     */
    MAGIC(ChatColor.MAGIC),
    /**
     * The color is reset.
     */
    RESET(ChatColor.RESET);

    public final ChatColor color;

    ColorFlag(ChatColor color) {
        this.color = color;
    }

    /**
     * Denotes how to modify a {@link Gradient} with this flag.
     *
     * @param gradient - Gradient to modify.
     */
    public void gradient(@Nonnull Gradient gradient) {
    }
}
