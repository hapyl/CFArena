package me.hapyl.fight.game.entity;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.commission.CommissionEntity;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

/**
 * Represents a type of the entity.
 * Mainly used for health/name display.
 */
@SuppressWarnings("deprecation") // fuck paper
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
        public String formatHealth(@Nonnull CommissionEntity entity) {
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
        public String formatHealth(@Nonnull CommissionEntity entity) {
            return MINIBOSS.formatHealth(entity);
        }

    };

    @Nonnull
    public String formatName(@Nonnull String name) {
        return name;
    }

    @Nonnull
    public String formatHealth(@Nonnull CommissionEntity entity) {
        final double health = entity.getHealth();
        final double maxHealth = entity.getMaxHealth();
        final ChatColor healthColor = getHealthColor(health, maxHealth);

        return healthColor + getHealthString(health) + "&7/&a" + getHealthString(maxHealth);
    }

    protected static ChatColor getHealthColor(double health, double maxHealth) {
        double percentHealth = health / maxHealth;

        if (percentHealth <= 0.25d) {
            return ChatColor.RED;
        }
        else if (percentHealth <= 0.5d) {
            return ChatColor.YELLOW;
        }

        return ChatColor.GREEN;
    }

    @Nonnull
    protected static String getHealthString(double health) {
        if (health >= 1e9) {
            return "%,.0fB".formatted(health / 1e9);
        }
        else if (health >= 1e6) {
            return "%,.0fM".formatted(health / 1e6);
        }
        else if (health >= 1e4) {
            return "%,.0fK".formatted(health / 1e3);
        }

        return "%,.0f".formatted(health);
    }

}
