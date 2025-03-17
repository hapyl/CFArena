package me.hapyl.fight.game.damage;

import me.hapyl.eterna.module.annotate.NotEmpty;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record DeathMessage(@Nonnull String message, @Nonnull String damagerSuffix) {

    private static final String DAMAGER_PLACEHOLDER = "{damager}";
    private static final String DEFAULT_COLOR = ChatColor.RED.toString();

    private static final double PROJECTILE_DISTANCE_THRESHOLD = 15d;

    public DeathMessage(@Nonnull String message, @Nonnull String damagerSuffix) {
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
                this.damagerSuffix = damagerSuffix + " " + DAMAGER_PLACEHOLDER;
            }
        }
    }

    @Nonnull
    public String format(@Nonnull GamePlayer player, @Nullable GameEntity killer, double distance) {
        final StringBuilder builder = new StringBuilder("&4 ☠ " + DEFAULT_COLOR);

        // Append the base message
        builder.append(player.getNameWithTeamColor()).append(DEFAULT_COLOR).append(" ").append(message);

        // If there was a killer, append suffix, because it usually contains 'by', 'from', etc.
        if (killer != null && !damagerSuffix.isEmpty()) {
            builder.append(" ").append(damagerSuffix);
        }

        builder.append(DEFAULT_COLOR).append(".");

        // If the shot was projectile, and it was from far away, add flex distance
        if (player.getLastDamageCause().type() == DamageType.DIRECT_RANGE && distance >= PROJECTILE_DISTANCE_THRESHOLD) {
            builder.append(" &7(from %.1f blocks away!)".formatted(distance));
        }

        return builder.toString()
                      .replace(DAMAGER_PLACEHOLDER, getValidPronoun(killer));
    }

    @NotEmpty
    @Nonnull
    private String getValidPronoun(@Nullable GameEntity gameEntity) {
        if (gameEntity == null) {
            return "something";
        }

        final LivingEntity entity = gameEntity.getEntity();

        if (entity instanceof Projectile projectile) {
            final ProjectileSource shooter = projectile.getShooter();

            if (shooter instanceof LivingEntity livingShooter) {
                return livingShooter.getName() + "'s " + gameEntity.getNameWithTeamColor();
            }
        }

        return gameEntity.getNameWithTeamColor() + DEFAULT_COLOR;
    }

}