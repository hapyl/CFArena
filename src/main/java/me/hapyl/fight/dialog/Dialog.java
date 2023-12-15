package me.hapyl.fight.dialog;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.MetadataEntry;
import me.hapyl.fight.database.entry.MetadataKey;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.registry.EnumId;
import me.hapyl.fight.util.Keyed;
import me.hapyl.fight.ux.Message;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Represents a Dialog system.
 */
public class Dialog extends EnumId implements Keyed<MetadataKey> {

    private final Queue<DialogEntry> entries;
    private final MetadataKey key;

    public Dialog(@Nonnull String id) {
        super(id);

        this.entries = new LinkedList<>();
        this.key = new MetadataKey("dialog." + id);
    }

    public boolean hasTalked(@Nonnull Player player) {
        return getMetadata(player).has(key);
    }

    public void start(@Nonnull Player player) {
        final PlayerProfile profile = PlayerProfile.getProfile(player);

        if (profile == null) {
            Message.error(player, "Cannot start dialog because you don't have a profile somehow?");
            return;
        }

        // Already in a dialog, don't care
        if (profile.dialog != null) {
            return;
        }

        profile.dialog = new ActiveDialog(profile, this);
    }

    @Nonnull
    public Queue<DialogEntry> entriesCopy() {
        return new LinkedList<>(entries);
    }

    public Dialog addEntry(@Nonnull DialogEntry entry) {
        entries.offer(entry);
        return this;
    }

    public Dialog addEntries(@Nonnull DialogEntry[] entries) {
        this.entries.addAll(List.of(entries));
        return this;
    }

    @Nonnull
    @Override
    public MetadataKey getKey() {
        return key;
    }

    private MetadataEntry getMetadata(Player player) {
        return PlayerDatabase.getDatabase(player).metadataEntry;
    }

}
