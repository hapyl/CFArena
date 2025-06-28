package me.hapyl.fight.database;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.eterna.module.registry.Registry;
import me.hapyl.eterna.module.util.Enums;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.game.profile.PlayerProfile;
import org.bson.Document;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class PlayerDatabaseEntry {
    
    protected final PlayerDatabase database;
    protected final Document root;
    protected final String parent;
    
    public PlayerDatabaseEntry(@Nonnull PlayerDatabase database, @Nonnull String parent) {
        this.database = database;
        this.root = database.getDocument();
        this.parent = parent;
    }
    
    /**
     * Returns the database associated with this entry.
     *
     * @return - PlayerDatabase.
     */
    @Nonnull
    public PlayerDatabase getDatabase() {
        return database;
    }
    
    /**
     * Gets the root {@link Document}.
     *
     * @return the root document.
     */
    @Nonnull
    public Document getRootDocument() {
        return database.getDocument();
    }
    
    /**
     * Gets or computes this {@link PlayerDatabaseEntry} {@link Document} in the root.
     *
     * @return this entry's document.
     */
    @Nonnull
    public Document getDocument() {
        return (Document) getRootDocument().computeIfAbsent(parent, fn -> new Document());
    }
    
    @Nonnull
    public Optional<Player> player() {
        return database.player();
    }
    
    @Nonnull
    public UUID uuid() {
        return database.getUuid();
    }
    
    public void sendMessage(@Nonnull Message.Channel channel, @Nonnull String message) {
        player().ifPresent(player -> channel.send(player, message));
    }
    
    /**
     * Called right before writing the root {@link Document} into the remote database.
     */
    @EventLike
    public void onSave() {
    }
    
    /**
     * Called once after loading all the entries.
     */
    @EventLike
    public void onLoad() {
    }
    
    @Nonnull
    public final String makeKey(@Path String path) {
        return this.parent + "." + path;
    }
    
    /**
     * Sets the given value to the given {@link Path}.
     *
     * @param path  - {@link Path}
     * @param value - Value to set.
     *              The value will be removed if the given value is null.
     */
    protected final <T> void setValue(@Nonnull @Path String path, @Nullable T value) {
        MongoUtils.set(root, makeKey(path), value);
    }
    
    /**
     * Gets a value at the given {@link Path}.
     *
     * @param path - {@link Path}.
     * @param def  - Default value.
     * @return the value at the given {@link Path}.
     */
    protected final <T> T getValue(@Nonnull @Path String path, @Nullable T def) {
        return MongoUtils.get(root, makeKey(path), def);
    }
    
    /**
     * Fetches a value from the {@link Document} at the given {@link Path},
     * applies the given {@link Consumer} and sets it to the same {@link Path}.
     *
     * <pre>{@code
     * fetchDocumentValue("hello", new ArrayList<>(), list -> { list.add(1); })
     *
     * root:
     * {
     *     hello: [1]
     * }
     * }</pre>
     * <pre>{@code
     * fetchDocumentValue("hello.world", new ArrayList<>(), list -> { list.add(2); })
     *
     * root:
     * {
     *     hello:
     *     {
     *         world: [2]
     *     }
     * }
     * }</pre>
     *
     * @param path   - {@link Path}.
     * @param def    - Default value.
     * @param action - Action to perform either on the value or default value.
     */
    protected <E> void fetchDocumentValue(@Nonnull @Path String path, @Nonnull E def, @Nonnull Consumer<E> action) {
        final E value = getValue(path, def);
        
        action.accept(value);
        setValue(path, value);
    }
    
    /**
     * Fetches a {@link Document} at the given {@link Path}, applies the given {@link Consumer} and sets it to the same {@link Path}.
     *
     * <pre>{@code
     * fetchDocument("hello", document -> {
     *     document.put("goodbye", 1);
     * })
     *
     * root:
     * {
     *     hello:
     *     {
     *         goodbye: 1
     *     }
     * }
     * }</pre>
     *
     * @param path     - {@link Path}.
     * @param consumer - Action to perform.
     */
    protected final void fetchDocument(@Nonnull @Path String path, @Nonnull Consumer<Document> consumer) {
        final Document document = getValue(path, new Document());
        
        consumer.accept(document);
        setValue(path, document);
    }
    
    /**
     * Fetches a value from the {@link Document} at the given {@link Path}.
     *
     * <pre>{@code
     * fetchFromDocument("hello", document -> {
     *      return document.get("world", 1);
     * });
     *
     * root:
     * {
     *     hello:
     *     {
     *         world: 123 // <-- Gets this value
     *     }
     * }
     * }</pre>
     *
     * @param path     - {@link Path}.
     * @param function - Function on how to retrieve the value.
     * @return a value.
     */
    protected final <T> T fetchFromDocument(@Nonnull String path, @Nonnull Function<Document, T> function) {
        final Document document = getValue(path, new Document());
        
        return function.apply(document);
    }
    
    /**
     * Gets a {@link Registry} value at the given {@link Path}.
     * <pre>{@code
     * Cosmetic cosmetic = getRegistryValue(Registries.getCosmetics(), "selected");
     *
     * root:
     * {
     *     selected: "cosmetic_id" // <-- Gets this value
     * }
     * }</pre>
     *
     * @param registry - Registry.
     * @param path     - {@link Path}.
     * @return a registry item, or null if not registered.
     */
    @Nullable
    protected <T extends Keyed> T getRegistryValue(@Nonnull Registry<T> registry, @Nonnull @Path String path) {
        return registry.get(getValue(path, ""));
    }
    
    /**
     * Gets an {@link Enum} value at the given {@link Path}.
     *
     * <pre>{@code
     * DayOfWeek dayOfWeek = getEnumValue(DayOfWeek.class, "day_of_week");
     *
     * root:
     * {
     *     day_of_week: "monday" // <-- Gets this value
     * }
     * }</pre>
     *
     * @param clazz - Enum class.
     * @param paths - {@link Path}.
     * @return an enum, or null if it doesn't exist.
     */
    @Nullable
    protected <T extends Enum<T>> T getEnumValue(@Nonnull Class<T> clazz, @Nonnull @Path String paths) {
        final String value = getValue(paths, "");
        
        return Enums.byName(clazz, value);
    }
    
    /**
     * Attempts to get player's {@link PlayerProfile}.
     *
     * @return a player profile if the player is online; null otherwise.
     */
    @Nullable
    protected PlayerProfile getProfile() {
        return player().map(CF::getProfile).orElse(null);
    }
    
}
