package me.hapyl.fight.game.entity;

import me.hapyl.fight.game.color.Color;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public enum EntityType {

    FRIENDLY {
        @Nonnull
        @Override
        public String formatName(@Nonnull String name) {
            return ChatColor.GREEN + "🐰 " + name;
        }
    },
    PASSIVE {
        @Nonnull
        @Override
        public String formatName(@Nonnull String name) {
            return ChatColor.YELLOW + name;
        }
    },
    HOSTILE {
        @Nonnull
        @Override
        public String formatName(@Nonnull String name) {
            return ChatColor.DARK_RED + "☠ " + ChatColor.RED + name;
        }
    },
    BOSS {
        // ﴾ ﴿

        @Nonnull
        @Override
        public String formatName(@Nonnull String name) {
            return "&e&l﴾ " + Color.ERROR + name + " &e&l﴿";
        }
    };

    @Nonnull
    public String formatName(@Nonnull String name) {
        return name;
    }

    @Nonnull
    public String formatHealth(@Nonnull NamedGameEntity entity) {
        final double health = entity.getHealth();
        final double maxHealth = entity.getMaxHealth();
        final ChatColor healthColor = getHealthColor(health, maxHealth);

        return healthColor + getHealthString(health) + "&7/&c" + getHealthString(maxHealth);
    }

    private String getHealthString(double health) {
        if (health < 1000000) {
            return String.format("%.0f", health);
        }
        else if (health < 1000000000) {
            return String.format("%.1fM", health / 1e6);
        }
        else {
            return String.format("%.1fB", health / 1e9);
        }
    }

    private ChatColor getHealthColor(double health, double maxHealth) {
        double percentHealth = health / maxHealth;

        if (percentHealth <= 0.0d) {
            return ChatColor.DARK_RED;
        }
        else if (percentHealth <= 0.5d) {
            return ChatColor.YELLOW;
        }

        return ChatColor.RED;
    }

}
