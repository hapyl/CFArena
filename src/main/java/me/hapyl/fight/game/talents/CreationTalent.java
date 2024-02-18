package me.hapyl.fight.game.talents;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.util.collection.NonnullTuple;
import me.hapyl.fight.util.collection.Tuple;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Represents a talent, that creates something, be that entity or a pillar or anything really.
 * It automatically handled to remove the creation upon death/stop, etc.
 */
public abstract class CreationTalent extends Talent {

    protected final PlayerMap<CreationBuffer> mapped;
    private final int maxCreations;

    private boolean removeAtDeath;

    public CreationTalent(@Nonnull String name) {
        this(name, 1);
    }

    public CreationTalent(@Nonnull String name, int maxCreations) {
        this(name, "", maxCreations);
    }

    public CreationTalent(@Nonnull String name, @Nonnull String description) {
        this(name, description, 1);
    }

    public CreationTalent(@Nonnull String name, @Nonnull String description, int maxCreations) {
        super(name, description, Type.CREATABLE);

        this.mapped = PlayerMap.newMap();
        this.removeAtDeath = true;
        this.maxCreations = maxCreations;
    }

    @Override
    @Deprecated
    public Talent setType(@Nonnull Type type) {
        return super.setType(Type.CREATABLE);
    }

    @Nonnull
    @Override
    public Type getType() {
        return Type.CREATABLE;
    }

    /**
     * Whenever all creations should be removed upon player's death.
     *
     * @return true if it should be removed; false otherwise.
     */
    public boolean isRemoveAtDeath() {
        return removeAtDeath;
    }

    /**
     * Sets whenever to remove creations upon player's death.
     *
     * @param removeAtDeath - New value.
     */
    protected void setRemoveAtDeath(boolean removeAtDeath) {
        this.removeAtDeath = removeAtDeath;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onStop() {
        mapped.forEachAndClear(CreationBuffer::clear);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onDeath(@Nonnull GamePlayer player) {
        if (!removeAtDeath) {
            return;
        }

        removeAllCreations(player);
    }

    /**
     * Gets the first creation value if present; null otherwise.
     *
     * @param player - Player.
     * @return the first creation if present; null otherwise.
     */
    @Nullable
    public final Creation getFirstCreation(@Nonnull GamePlayer player) {
        return getBuffer(player).peekFirst();
    }

    /**
     * Adds the new creation to the buffer.
     * This will remove the first placed creation if it exceeds the limit.
     *
     * @param player   - Player.
     * @param creation - Creation.
     * @return the newly created creation.
     */
    @Nonnull
    public final Creation newCreation(@Nonnull GamePlayer player, @Nonnull Creation creation) {
        getBuffer(player).add(creation);
        return creation;
    }

    // not using abstraction since it adds unnecessary layer, as example, fetching locations, etc.
    //public abstract Creation create(Player player);

    /**
     * Removes the current mapped value from the buffer,
     * calls {@link Creation#remove()} and returns it if present; null otherwise.
     *
     * @param player - Player.
     * @return the removed value or null.
     */
    @Nullable
    public final Creation removeFirstCreation(@Nonnull GamePlayer player) {
        final CreationBuffer buffer = getBuffer(player);
        final Creation first = buffer.first();

        buffer.remove(first);

        return first;
    }

    /**
     * Removes all creations for this player.
     *
     * @param player - Player.
     * @return number of creations removed.
     */
    public int removeAllCreations(GamePlayer player) {
        final CreationBuffer buffer = getBuffer(player);
        final int count = buffer.count();

        buffer.clear();

        return count;
    }

    /**
     * Removes the given creation value from the map,
     * calls {@link Creation#remove()}.
     * <p>
     * This method will always call {@link Creation#remove()}
     * on a given creation even if it does not match with the current value.
     *
     * @param player   - Player.
     * @param creation - Creation.
     */
    public final void removeCreation(@Nonnull GamePlayer player, @Nonnull Creation creation) {
        getBuffer(player).remove(creation);
    }

    /**
     * Returns the number of creations' player has.
     *
     * @param player - Player.
     * @return the number of creations' player has.
     */
    public int countCreations(@Nonnull GamePlayer player) {
        return getBuffer(player).count();
    }

    /**
     * Returns true if player has at least one creation.
     *
     * @param player - Player.
     * @return true if a player has at least one creation; false otherwise.
     */
    public boolean isExists(@Nonnull GamePlayer player) {
        return countCreations(player) >= 1;
    }

    @Nonnull
    private CreationBuffer getBuffer(@Nonnull GamePlayer player) {
        return mapped.computeIfAbsent(player, v -> new CreationBuffer(player, maxCreations));
    }

    @Nullable
    public Creation getCreationByEntity(@Nullable Entity entity) {
        final NonnullTuple<GamePlayer, Creation> tuple = getByEntity(entity);

        return tuple != null ? tuple.b() : null;
    }

    @Nullable
    public GamePlayer getCreationOwnerByEntity(@Nullable Entity entity) {
        final NonnullTuple<GamePlayer, Creation> tuple = getByEntity(entity);

        return tuple != null ? tuple.a() : null;
    }

    /**
     * Gets a {@link NonnullTuple} with a {@link GamePlayer} and {@link Creation} by its entity.
     *
     * @param entity - Entity.
     * @return a tuple or null.
     */
    @Nullable
    public NonnullTuple<GamePlayer, Creation> getByEntity(@Nullable Entity entity) {
        if (entity == null) {
            return null;
        }

        for (Map.Entry<GamePlayer, CreationBuffer> entry : mapped.entrySet()) {
            final GamePlayer player = entry.getKey();
            final CreationBuffer buffer = entry.getValue();

            for (Creation creation : buffer) {
                if (creation.isCreation(entity)) {
                    return Tuple.ofNonnull(player, creation);
                }
            }
        }

        return null;
    }

    public boolean isCreation(@Nullable Entity entity) {
        return entity != null && getByEntity(entity) != null;
    }

}
