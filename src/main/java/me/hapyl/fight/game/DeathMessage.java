package me.hapyl.fight.game;

import me.hapyl.eterna.module.chat.Gradient;
import me.hapyl.eterna.module.chat.gradient.Interpolators;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public record DeathMessage(String message, String damagerSuffix) {

    private static final String DAMAGER_PLACEHOLDER = "{damager}";

    public DeathMessage(String message, String damagerSuffix) {
        this.message = message;

        // If a message has placeholder, then damagerSuffix is not needed
        if (message.contains(DAMAGER_PLACEHOLDER)) {
            this.damagerSuffix = "";
        }
        else {
            // If the suffix has placeholder, then don't append it
            if (damagerSuffix.contains(DAMAGER_PLACEHOLDER)) {
                this.damagerSuffix = damagerSuffix;
            }
            else {
                if (damagerSuffix.isEmpty() || damagerSuffix.isBlank()) {
                    Debug.warn("A death message is missing a suffix! Fixing by suffixing with 'by'! See the console for details.");
                    Main.getPlugin().getLogger().warning("Missing death message: '%s'".formatted(message));

                    damagerSuffix = "by";
                }

                this.damagerSuffix = damagerSuffix + " " + DAMAGER_PLACEHOLDER;
            }
        }
    }

    public String formatMessage(String damager) {
        return message.replace(DAMAGER_PLACEHOLDER, damager);
    }

    public String formatSuffix(String damager) {
        if (damagerSuffix.isBlank()) {
            return "";
        }

        return damagerSuffix.replace(DAMAGER_PLACEHOLDER, damager);
    }

    @Nonnull
    public String format(@Nonnull GamePlayer player, @Nullable GameEntity killer, double distance) {
        final String killerPronoun = getValidPronoun(killer);
        final String message = message().replace(DAMAGER_PLACEHOLDER, killerPronoun);
        final String suffix = damagerSuffix().replace(DAMAGER_PLACEHOLDER, killerPronoun);
        final String longDistanceSuffix =
                (player.getLastDamageCause().isProjectile() && distance >= 20.0d)
                        ? " (from %.1f meters away!)".formatted(distance)
                        : "";

        String string;
        final String playerName = player.getName();

        if (killerPronoun.isBlank()) {
            string = "%s %s".formatted(playerName, message + longDistanceSuffix);
        }
        else {
            string = "%s %s %s".formatted(playerName, message, suffix + longDistanceSuffix);
        }

        return "&4☠ " + new Gradient(string)
                .rgb(
                        new Color(160, 0, 0),
                        new Color(255, 51, 51),
                        Interpolators.LINEAR
                );
    }

    private String getValidPronoun(@Nullable GameEntity gameEntity) {
        if (gameEntity == null) {
            return "something";
        }

        final LivingEntity entity = gameEntity.getEntity();

        if (entity instanceof Projectile projectile) {
            final ProjectileSource shooter = projectile.getShooter();

            if (shooter instanceof LivingEntity livingShooter) {
                return livingShooter.getName() + "'s " + gameEntity.getNameUnformatted();
            }
        }

        return gameEntity.getNameUnformatted();
    }

    public static DeathMessage of(String message, String suffix) {
        return new DeathMessage(message, suffix);
    }

}