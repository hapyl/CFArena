package me.hapyl.fight.database.entry;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import org.bson.Document;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class MetadataEntry extends PlayerDatabaseEntry {

    @Deprecated
    public final MetadataParent noParent;

    public final MetadataParent dialog;
    public final MetadataParent poi;
    public final MetadataParent claimedRewards;

    private final Map<String, MetadataParent> parents;

    public MetadataEntry(@Nonnull PlayerDatabase playerDatabase) {
        super(playerDatabase, "metadata");

        this.parents = new HashMap<>();

        // Init parents
        this.noParent = new MetadataParent(null); // don't cache null

        this.dialog = getParent("dialog");
        this.poi = getParent("poi");
        this.claimedRewards = getParent("claimed_rewards");
    }

    @Nonnull
    public MetadataParent getParent(@Nonnull String string) {
        final String lowerCase = string.toLowerCase();

        return parents.computeIfAbsent(lowerCase, MetadataParent::new);
    }

    public static <T> void set(@Nonnull Player player, @Nonnull Key key, @Nullable T value) {
        CF.getDatabase(player).metadataEntry.noParent.set(key, value);
    }

    public static <T> T get(@Nonnull Player player, @Nonnull Key key, T def) {
        return CF.getDatabase(player).metadataEntry.noParent.get(key, def);
    }

    public static boolean isTrue(@Nonnull Player player, @Nonnull Key key) {
        return get(player, key, false);
    }

    public static boolean has(@Nonnull Player player, @Nonnull Key key) {
        return CF.getDatabase(player).metadataEntry.noParent.has(key);
    }

    @Nonnull
    public static Map<String, Object> map(Player player) {
        final Map<String, Object> metadata = Maps.newHashMap();
        final Document document = CF.getDatabase(player).metadataEntry.getDocument();

        traverseDocument("", document, metadata);
        return metadata;
    }

    private static void traverseDocument(String parent, Document document, Map<String, Object> metadata) {
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();

            final String newKey = parent.isEmpty() ? key : parent + "." + key;

            if (value instanceof Document) {
                traverseDocument(newKey, (Document) value, metadata);
            }
            else {
                metadata.put(newKey, value);
            }
        }
    }

    public class MetadataParent {

        private final String parent;

        MetadataParent(@Nullable String parent) {
            this.parent = parent;
        }

        public <T> void set(@Nonnull Key key, @Nullable T value) {
            setValue(makeKey(key), value);
        }

        public <T> T get(@Nonnull Key key, T def) {
            return getValue(makeKey(key), def);
        }

        public boolean has(@Nonnull Key key) {
            return getValue(makeKey(key), null) != null;
        }

        private String makeKey(@Nonnull Key key) {
            return parent != null ? "%s.%s".formatted(parent, key.getKey()) : key.getKey();
        }

    }

}
