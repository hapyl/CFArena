package me.hapyl.fight.game.entity;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.util.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class GameEntity {

    public final UUID uuid;
    @Nonnull
    protected LivingEntity entity;

    public GameEntity(@Nonnull LivingEntity entity) {
        this.uuid = entity.getUniqueId();
        this.entity = entity;
    }

    @Nonnull
    public LivingEntity getEntity() {
        return entity;
    }

    public void teleport(Location location) {
        entity.teleport(location);
    }

    @Nonnull
    public Location getLocation() {
        return entity.getLocation();
    }

    public <T extends LivingEntity> boolean is(@Nonnull Class<T> clazz) {
        return clazz.isInstance(entity);
    }

    public boolean is(@Nullable LivingEntity entity) {
        return this.entity == entity;
    }

    public boolean isNot(LivingEntity player) {
        return !is(player);
    }

    public <T extends LivingEntity> boolean isNot(@Nonnull Class<T> clazz) {
        return !is(clazz);
    }

    @Nonnull
    public String getName() {
        return entity.getName();
    }

    @Nonnull
    public final String getNameUnformatted() {
        return Utils.stripColor(getName());
    }

    @Nonnull
    public Location getEyeLocation() {
        return entity.getEyeLocation();
    }

    public void asPlayer(Consumer<Player> consumer) {
        if (!(entity instanceof Player player)) {
            return;
        }

        consumer.accept(player);
    }

    public final UUID getUUID() {
        return uuid;
    }

    @Nonnull
    public World getWorld() {
        final World world = getLocation().getWorld();

        if (world != null) {
            return world;
        }

        throw new IllegalArgumentException("unloaded world!!!");
    }

    public boolean isValid() {
        return isValid(null);
    }

    public boolean isValid(Player player) {
        // null entities, self or armor stands are not valid
        if (is(player) || entity instanceof ArmorStand) {
            return false;
        }

        // dead or invisible entities are not valid
        if (entity.isDead() || entity.isInvisible()) {
            return false;
        }

        // players are only valid if they are alive and not on the same team
        if (entity instanceof Player targetPlayer) {
            // creative players should not be valid!
            if (targetPlayer.getGameMode() == GameMode.CREATIVE) {
                return false;
            }

            if (Manager.current().isGameInProgress() && !CF.getOrCreatePlayer(targetPlayer).isAlive()) {
                return false;
            }
            return !GameTeam.isTeammate(player, targetPlayer);
        }

        // Dummy check
        if (entity.getScoreboardTags().contains("dummy")) {
            return true;
        }

        return entity.hasAI();
    }

    public boolean hasLineOfSight(@Nonnull LivingEntity entity) {
        return this.entity.hasLineOfSight(entity);
    }

    public void setVelocity(Vector vector) {
        entity.setVelocity(vector);
    }

    public boolean hasTag(String s) {
        return entity.getScoreboardTags().contains(s);
    }

    public void removeTag(String s) {
        entity.getScoreboardTags().remove(s);
    }

    public void setInvulnerable(boolean b) {
        entity.setInvulnerable(b);
    }

    public double getEyeHeight() {
        return entity.getEyeHeight();
    }

    public void setFreezeTicks(int tick) {
        entity.setFreezeTicks(tick);
    }

    public int getNoDamageTicks() {
        return entity.getNoDamageTicks();
    }

    public void setFireTicks(int tick) {
        entity.setFireTicks(tick);
    }

    public void remove() {
        entity.remove();
    }

    /**
     * Calls entity removal without the death animation.
     * Does <b>nothing</b> to players.
     */
    public final void forceRemove() {
        if (entity instanceof Player) {
            return;
        }

        entity.remove();
        remove();
    }

    @Event
    public void onStart(@Nonnull GameInstance instance) {
    }

    @Event
    public void onStop(@Nonnull GameInstance instance) {
        forceRemove();
    }

    @Event
    public void onDeath() {
        remove();
    }

    public <T extends GameEntity> void as(@Nonnull Class<T> clazz, @Nonnull Consumer<T> consumer) {
        if (clazz.isInstance(entity)) {
            consumer.accept(clazz.cast(entity));
        }
    }

    @Override
    public String toString() {
        return "GameEntity{" + uuid.toString() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final GameEntity that = (GameEntity) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

}
