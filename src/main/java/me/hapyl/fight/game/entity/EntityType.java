package me.hapyl.fight.game.entity;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.named.NamedGameEntity;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

/**
 * Represents a type of the entity.
 * Mainly used for health/name display.
 */
public enum EntityType {

    /**
     * A friendly entity that should not attack player.
     */
    FRIENDLY {
        @Nonnull
        @Override
        public String formatName(@Nonnull String name) {
            return ChatColor.GREEN + name;
        }
    },
    /**
     * A friendly entity by itself, but will attack if attacked.
     */
    PASSIVE {
        @Nonnull
        @Override
        public String formatName(@Nonnull String name) {
            return ChatColor.YELLOW + name;
        }
    },
    /**
     * A hostile entity, attacks players and/or other entities.
     */
    HOSTILE {
        @Nonnull
        @Override
        public String formatName(@Nonnull String name) {
            return ChatColor.RED + name;
        }
    },
    /**
     * A mini-boss entity, usually with named abilities.
     */
    MINIBOSS {
        @Nonnull
        @Override
        public String formatName(@Nonnull String name) {
            return "&c☠ &b&l" + name;
        }

        @Nonnull
        public String formatHealth(@Nonnull NamedGameEntity<?> entity) {
            final double health = entity.getHealth();
            final double maxHealth = entity.getMaxHealth();
            final ChatColor healthColor = getHealthColor(health, maxHealth);

            return healthColor + getHealthString(health);
        }
    },
    /**
     * A boss entity, usually MUCH stronger than the mini-boss and has "phases".
     */
    BOSS {
        @Nonnull
        @Override
        public String formatName(@Nonnull String name) {
            return "&e&l﴾ " + Color.ERROR + name + " &e&l﴿";
        }

        @Nonnull
        public String formatHealth(@Nonnull NamedGameEntity<?> entity) {
            return MINIBOSS.formatHealth(entity);
        }
    };

    @Nonnull
    public String formatName(@Nonnull String name) {
        return name;
    }

    @Nonnull
    public String formatHealth(@Nonnull NamedGameEntity<?> entity) {
        final double health = entity.getHealth();
        final double maxHealth = entity.getMaxHealth();
        final ChatColor healthColor = getHealthColor(health, maxHealth);

        return healthColor + getHealthString(health) + "&7/&a" + getHealthString(maxHealth);
    }

    protected final ChatColor getHealthColor(double health, double maxHealth) {
        double percentHealth = health / maxHealth;

        if (percentHealth <= 0.25d) {
            return ChatColor.RED;
        }
        else if (percentHealth <= 0.5d) {
            return ChatColor.YELLOW;
        }

        return ChatColor.GREEN;
    }

    protected final String getHealthString(double health) {
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

}
