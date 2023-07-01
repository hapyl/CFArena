package me.hapyl.fight.game.color;

import me.hapyl.spigotutils.module.chat.Gradient;
import net.md_5.bungee.api.ChatColor;

import javax.annotation.Nonnull;

public enum ColorFlag {

    BOLD(ChatColor.BOLD) {
        @Override
        public void gradient(@Nonnull Gradient gradient) {
            gradient.makeBold();
        }
    },
    ITALIC(ChatColor.ITALIC) {
        @Override
        public void gradient(@Nonnull Gradient gradient) {
            gradient.makeItalic();
        }
    },
    UNDERLINE(ChatColor.UNDERLINE) {
        @Override
        public void gradient(@Nonnull Gradient gradient) {
            gradient.makeUnderline();
        }
    },
    STRIKETHROUGH(ChatColor.STRIKETHROUGH) {
        @Override
        public void gradient(@Nonnull Gradient gradient) {
            gradient.makeStrikethrough();
        }
    },
    MAGIC(ChatColor.MAGIC),
    RESET(ChatColor.RESET);

    public final ChatColor color;

    ColorFlag(ChatColor color) {
        this.color = color;
    }

    @Nonnull
    public void gradient(@Nonnull Gradient gradient) {
    }
}
