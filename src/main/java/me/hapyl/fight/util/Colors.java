package me.hapyl.fight.util;

import org.bukkit.DyeColor;

import javax.annotation.Nonnull;
import java.awt.*;

// todo: this is a cool module for Eterna
public interface Colors {

    ColorConvert<org.bukkit.ChatColor> CHAT_COLOR = new ColorConvert<>() {
        @Nonnull
        @Override
        public Color toJava(@Nonnull org.bukkit.ChatColor chatColor) {
            return chatColor.asBungee().getColor();
        }

        @Nonnull
        @Override
        public org.bukkit.ChatColor toBukkitColor(@Nonnull org.bukkit.ChatColor chatColor) {
            return chatColor;
        }
    };

    interface ColorConvert<F> {

        @Nonnull
        Color toJava(@Nonnull F f);

        @Nonnull
        default org.bukkit.Color toBukkit(@Nonnull F f) {
            final Color javaColor = toJava(f);

            return org.bukkit.Color.fromRGB(javaColor.getRed(), javaColor.getGreen(), javaColor.getBlue());
        }

        @Nonnull
        default DyeColor toDyeColor(@Nonnull F f) {
            final DyeColor dyeColor = DyeColor.getByColor(toBukkit(f));

            return dyeColor != null ? dyeColor : DyeColor.WHITE;
        }

        @Nonnull
        org.bukkit.ChatColor toBukkitColor(@Nonnull F f);

        @Nonnull
        default net.md_5.bungee.api.ChatColor toBungeeColor(@Nonnull F f) {
            return toBukkitColor(f).asBungee();
        }
    }

}
