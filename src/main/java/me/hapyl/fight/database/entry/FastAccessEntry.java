package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseArrayEntry;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.fastaccess.FastAccess;
import me.hapyl.fight.registry.EnumId;
import me.hapyl.fight.registry.Registry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FastAccessEntry extends PlayerDatabaseArrayEntry<FastAccess> {
    public FastAccessEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase, "fast_access", 9);
    }

    @Nonnull
    @Override
    public FastAccess[] newArray() {
        return new FastAccess[size];
    }

    public int getLength() {
        final PlayerRank playerRank = getDatabase().getRank();

        return getMaxLength(playerRank);
    }

    public boolean hasUnlocked(int index) {
        return index >= getLength();
    }

    @Nullable
    @Override
    public FastAccess fromString(@Nonnull String string) {
        return !string.isEmpty() ? Registry.FAST_ACCESS.get(new EnumId(string)) : null;
    }

    @Nonnull
    @Override
    public String toString(@Nonnull FastAccess fastAccess) {
        return fastAccess.getId();
    }

    public static int getMaxLength(@Nonnull PlayerRank rank) {
        if (rank.isOrHigher(PlayerRank.PREMIUM)) {
            return 9;
        }
        else if (rank.isOrHigher(PlayerRank.VIP)) {
            return 6;
        }

        return 3;
    }
}
