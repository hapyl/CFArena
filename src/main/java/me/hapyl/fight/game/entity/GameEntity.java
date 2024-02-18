package me.hapyl.fight.game.entity;

import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.team.Entry;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.garbage.CFGarbageCollector;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.locaiton.LocationHelper;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.Reflect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class GameEntity {

    public final UUID uuid;
    private final Set<String> tags;
    @Nonnull
    protected LivingEntity entity;
    private boolean forceValid;
    // By default, GameEntity is a 'base' class, which allows for
    // faster and better checks for if entity is valid.
    protected boolean base = false;

    public GameEntity(@Nonnull LivingEntity entity) {
        this.uuid = entity.getUniqueId();
        this.tags = Sets.newHashSet();
        this.entity = entity;
    }

    @Nonnull
    public LivingEntity getEntity() {
        return entity;
    }

    @Nonnull
    public GameEntity getGameEntity() {
        return this;
    }

    public void teleport(@Nonnull Location location) {
        entity.teleport(location);
    }

    public void teleport(@Nonnull GameEntity entity) {
        teleport(entity.getLocation());
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

    public boolean isNot(@Nonnull LivingEntity entity) {
        return this.entity != entity;
    }

    public <T extends LivingEntity> boolean isNot(@Nonnull Class<T> clazz) {
        return !is(clazz);
    }

    public void addPassenger(@Nonnull GameEntity entity) {
        this.entity.addPassenger(entity.getEntity());
    }

    public void addPassenger(@Nonnull Entity entity) {
        this.entity.addPassenger(entity);
    }

    @Nonnull
    public String getName() {
        return entity.getName();
    }

    @Nonnull
    public final String getNameUnformatted() {
        return CFUtils.stripColor(getName());
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
        return entity.getWorld();
    }

    public boolean isValid() {
        return isValid(null);
    }

    public boolean isValid(@Nullable GamePlayer player) {
        // null entities, self or armor stands are not valid
        if (equals(player) || entity instanceof ArmorStand) {
            return false;
        }

        // dead or base entities are not valid
        if (entity.isDead() || base) { // entity.isInvisible()
            return false;
        }

        // garbage entities are not valid
        if (CFGarbageCollector.isGarbageEntity(entity)) {
            return false;
        }

        // Teammate check
        if (player != null && GameTeam.isTeammate(Entry.of(this), Entry.of(player))) {
            return false;
        }

        // players are only valid if they are alive and not on the same team
        if (entity instanceof Player targetPlayer) {
            // Only survival players are valid
            if (targetPlayer.getGameMode() != GameMode.SURVIVAL) {
                return false;
            }

            final GamePlayer gamePlayer = CF.getPlayer(targetPlayer);

            if (gamePlayer == null || !gamePlayer.isAlive()) {
                return false;
            }

            if (gamePlayer.hasEffect(Effects.INVISIBILITY)) {
                return gamePlayer.getHero().isValidIfInvisible(gamePlayer);
            }

            return true;
        }

        // Force valid check
        if (forceValid) {
            return true;
        }

        return entity.hasAI() && !entity.isInvulnerable();
    }

    public boolean hasLineOfSight(@Nonnull GameEntity entity) {
        return this.entity.hasLineOfSight(entity.getEntity());
    }

    public boolean hasLineOfSight(@Nonnull Entity entity) {
        return this.entity.hasLineOfSight(entity);
    }

    public void addTag(@Nonnull String tag) {
        tags.add(tag);
    }

    public boolean hasTag(@Nonnull String tag) {
        return tags.contains(tag);
    }

    public void removeTag(@Nonnull String tag) {
        tags.remove(tag);
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

    public void kill() {
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
        kill();
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
        kill();
    }

    public <T extends Entity> void as(@Nonnull Class<T> clazz, @Nonnull Consumer<T> consumer) {
        if (clazz.isInstance(entity)) {
            consumer.accept(clazz.cast(entity));
        }
    }

    public void sendWarning(String warning, int stay) {
        asPlayer(player -> Chat.sendTitle(player, "&4&l⚠", warning, 0, stay, 5));
    }

    public void sendMessage(String message, Object... objects) {
        Chat.sendMessage(entity, message, objects);
    }

    public void sendTitle(@Nullable String title, @Nullable String subtitle, int fadeIn, int stay, int fadeOut) {
        asPlayer(player -> {
            player.sendTitle(
                    title != null ? Chat.format(title) : null,
                    subtitle != null ? Chat.format(subtitle) : null,
                    fadeIn,
                    stay,
                    fadeOut
            );
        });
    }

    public void sendSubtitle(String subtitle, int fadeIn, int stay, int fadeOut) {
        sendTitle("", subtitle, fadeIn, stay, fadeOut);
    }

    public void sendActionbar(String text, Object... objects) {
        asPlayer(player -> Chat.sendActionbar(player, text, objects));
    }

    public void playSound(Sound sound, final float pitch) {
        asPlayer(player -> PlayerLib.playSound(player, sound, Numbers.clamp(pitch, 0.0f, 2.0f)));
    }

    public void playSound(Location location, Sound sound, float pitch) {
        asPlayer(player -> PlayerLib.playSound(player, location, sound, pitch));
    }

    public void playWorldSound(Location location, Sound sound, float pitch) {
        PlayerLib.playSound(location, sound, pitch);
    }

    public void playWorldSound(Sound sound, float pitch) {
        playWorldSound(getLocation(), sound, pitch);
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

    public int getId() {
        return entity.getEntityId();
    }

    public void setCustomNameVisible(boolean visible) {
        this.entity.setCustomNameVisible(visible);
    }

    @Nullable
    public String getCustomName() {
        return this.entity.getCustomName();
    }

    public void setCustomName(@Nullable String name) {
        this.entity.setCustomName(name);
    }

    public void flip() {
        final String name = getCustomName();
        if (name == null || !name.equals("Dinnerbone")) {
            setCustomName("Dinnerbone");
        }
        else {
            setCustomName(null);
        }
    }

    public void setAI(boolean b) {
        this.entity.setAI(false);
    }

    public boolean isProjectile() {
        return entity instanceof Projectile;
    }

    @Nonnull
    public Vector getVelocity() {
        return entity.getVelocity();
    }

    /**
     * Gets the absolute velocity.
     */
    @Nonnull
    public Vector getAbsoluteVelocity() {
        final Vector velocity = getVelocity();

        return new Vector(Math.abs(velocity.getX()), Math.abs(velocity.getY()), Math.abs(velocity.getZ()));
    }


    public void setVelocity(Vector vector) {
        entity.setVelocity(vector);
    }

    @Nonnull
    public Vector getVectorOffsetLeft(double multiply) {
        final Vector direction = getLocation().getDirection().normalize();

        return new Vector(direction.getZ(), 0.0, -direction.getX()).normalize().multiply(multiply);
    }

    @Nonnull
    public Vector getVectorOffsetRight(double multiply) {
        final Vector direction = getLocation().getDirection().normalize();

        return new Vector(-direction.getZ(), 0.0, direction.getX()).normalize().multiply(multiply);
    }

    @Nonnull
    public Location getLocationInFront(double multiply) {
        return LocationHelper.getInFront(getLocation(), multiply);
    }

    @Nonnull
    public Location getLocationInFrontFromEyes(double offset) {
        return LocationHelper.getInFront(getEyeLocation(), offset);
    }

    @Nonnull
    public Location getLocationBehind(double multiply) {
        return LocationHelper.getBehind(getLocation(), multiply);
    }

    @Nonnull
    public Location getLocationBehindFromEyes(double offset) {
        return LocationHelper.getBehind(getEyeLocation(), offset);
    }

    @Nonnull
    public Block getBlock() {
        return getLocation().getBlock();
    }

    public int getBlockLight() {
        return getBlock().getLightLevel();
    }

    public float getYaw() {
        return getLocation().getYaw();
    }

    public float getPitch() {
        return getLocation().getPitch();
    }

    public double getY() {
        return getLocation().getY();
    }

    public int getBlockY() {
        return (int) getY();
    }

    @Nonnull
    public Vector getDirection() {
        return getLocation().getDirection();
    }

    @Nonnull
    public Vector getEyeDirection() {
        return getEyeLocation().getDirection();
    }

    /**
     * Gets the throw direction from entity's {@link #getDirection()} multiplied by <code>normalized</code> {@link #getAbsoluteVelocity()}.
     *
     * @return the throw direction.
     */
    @Nonnull
    public Vector getThrowDirection() {
        final Vector direction = getDirection();
        final Vector velocity = getAbsoluteVelocity().normalize();

        return direction.multiply(velocity);
    }

    public net.minecraft.world.entity.Entity getNMSEntity() {
        return Reflect.getMinecraftEntity(entity);
    }

    public double dot(@Nonnull Location other) {
        final Vector vector = other.subtract(getEyeLocation()).toVector().normalize();

        return getEyeLocation().getDirection().dot(vector);
    }

    public void addToTeam(@Nonnull GameTeam team) {
        team.addEntry(Entry.of(this));
    }

    public void setGravity(boolean b) {
        entity.setGravity(b);
    }

    public boolean hasGravity() {
        return entity.hasGravity();
    }

    public void setForceValid(boolean forceValid) {
        this.forceValid = forceValid;
    }

    public boolean isForceValid() {
        return forceValid;
    }
}
