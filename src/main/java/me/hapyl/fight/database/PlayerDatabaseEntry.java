package me.hapyl.fight.database;

import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.util.Enums;
import org.bson.Document;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @deprecated Please prefer {@link StrictPlayerDatabaseEntry}!
 */
@Deprecated
public class PlayerDatabaseEntry {

    protected final PlayerDatabase playerDatabase;
    protected String path;

    public PlayerDatabaseEntry(PlayerDatabase playerDatabase) {
        this.playerDatabase = playerDatabase;
        this.path = null;
    }

    /**
     * Returns the database associated with this entry.
     *
     * @return - PlayerDatabase.
     */
    public PlayerDatabase getDatabase() {
        return playerDatabase;
    }

    /**
     * Return current document aka root.
     *
     * @return - Document
     */
    public Document getDocument() {
        return playerDatabase.getDocument();
    }

    /**
     * Gets the {@link OfflinePlayer} associated with this entry.
     *
     * @return the offline player.
     */
    @Nonnull
    public OfflinePlayer getPlayer() {
        return this.playerDatabase.getPlayer();
    }

    /**
     * Gets the {@link Player} associated with this entry; or null if they're offline.
     *
     * @return the player associated with this entry; or null if they're offline.
     */
    @Nullable
    public Player getOnlinePlayer() {
        return getPlayer().getPlayer();
    }

    public void sendMessage(@Nonnull String message, @Nullable Object... format) {
        final Player player = getOnlinePlayer();

        if (player != null) {
            Chat.sendMessage(player, message.formatted(format));
        }
    }

    /**
     * Called right before writing the document into the remote database.
     */
    @Event
    public void onSave() {
    }

    /**
     * Called once after loading all the entries.
     */
    @Event
    public void onLoad() {
    }

    @Nonnull
    protected String getPath() throws IllegalStateException {
        if (this.path == null) {
            throw new IllegalStateException("Path is not set for " + this.getClass().getSimpleName() + "!");
        }

        return this.path;
    }

    protected void setPath(@Nonnull String path) {
        this.path = path;
    }

    @Nonnull
    protected final String getPathWithDot() throws IllegalStateException {
        return getPath() + ".";
    }

    /**
     * Gets the value from a document by a given string.
     * <p>
     * The string can be separated by a dot (.) to access nested documents.
     * </p>
     *
     * @param paths - Path to value.
     * @param def   - Default value.
     * @return - Value or def if not found.
     */
    protected <T> T getValue(@Nonnull String paths, @Nullable T def) {
        return MongoUtils.get(getDocument(), paths, def);
    }

    @Nullable
    protected <T extends Enum<T>> T getEnumValue(@Nonnull String paths, @Nonnull Class<T> clazz) {
        final String value = getValue(paths, "");

        return Enums.byName(clazz, value);
    }

    protected <T extends Enum<T>> void setEnumValue(@Nonnull String paths, @Nullable T value) {
        setValue(paths, value != null ? value.name().toLowerCase() : null);
    }

    /**
     * Gets the value from a document by a given string.
     * <p>
     * The string can be separated by a dot (.) to access nested documents.
     * </p>
     * <p>
     * Requires {@link #path} to be set.
     * </p>
     *
     * @param paths - Path to value.
     * @param def   - Default value.
     * @return - Value or def if not found.
     */
    protected final <T> T getValueInPath(@Nonnull String paths, @Nullable T def) {
        return getValue(getPathWithDot() + paths, def);
    }

    /**
     * Sets the value from a document by a given string.
     * <p>
     * The string can be separated by a dot (.) to access nested documents.
     * If the string does not exist, it will be created.
     * </p>
     *
     * @param paths - Path to value.
     * @param value - Value to set.
     */
    protected <T> void setValue(@Nonnull String paths, @Nullable T value) {
        MongoUtils.set(getDocument(), paths, value);
    }

    /**
     * Sets the value to a document by a given string.
     * <p>
     * The string can be separated by a dot (.) to access nested documents.
     * If the string does not exist, it will be created.
     * </p>
     * <p>
     * Requires {@link #path} to be set.
     * </p>
     *
     * @param paths - Path to value.
     * @param value - Value to set.
     */
    protected final <T> void setValueInPath(@Nonnull String paths, @Nullable T value) {
        setValue(getPathWithDot() + paths, value);
    }

    protected final <T> void setValueIfNotSet(@Nonnull String paths, @Nonnull T value) {
        if (getValue(paths, null) != null) {
            return;
        }

        setValue(paths, value);
    }

    /**
     * Returns a a document in this (root) document.
     * <p>
     * <code>getInDocument("string");</code> will return the following:
     * </p>
     * <pre>
     *     {
     *          "string": {
     *              ...
     *          }
     *     }
     * </pre>
     *
     * @param path - Path to document.
     * @return - Document
     */
    protected Document getInDocument(String path) {
        return getDocument().get(path, new Document());
    }

    protected Document getInDocument() {
        return getInDocument(getPath());
    }

    /**
     * Fetches a document from the root document and puts it in the root document.
     *
     * @param path     - Path to document.
     * @param consumer - Consumer to accept the document.
     */
    protected void fetchDocument(String path, Consumer<Document> consumer) {
        final Document document = getInDocument(path);

        consumer.accept(document);
        getDocument().put(path, document);
    }

    protected void fetchDocument(Consumer<Document> consumer) {
        fetchDocument(getPath(), consumer);
    }

    /**
     * Fetches a document from the root document and gets the value from it according to the function.
     *
     * @param path     - Path to document.
     * @param function - Function to get the value from the document.
     * @return - Value from the document.
     */
    protected <T> T fetchFromDocument(String path, Function<Document, T> function) {
        final Document document = getInDocument(path);

        return function.apply(document);
    }

    protected <T> T fetchFromDocument(Function<Document, T> function) {
        return fetchFromDocument(getPath(), function);
    }

    /**
     * Attempts to get player's {@link PlayerProfile}.
     *
     * @return a player profile if the player is online; null otherwise.
     */
    @Nullable
    protected PlayerProfile getProfile() {
        final Player player = getOnlinePlayer();

        return player != null ? PlayerProfile.getProfile(player) : null;
    }
}
