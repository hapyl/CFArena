package me.hapyl.fight.database.entry;

import com.google.common.collect.Lists;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class FriendsEntry extends PlayerDatabaseEntry {
    public FriendsEntry(@Nonnull PlayerDatabase playerDatabase) {
        super(playerDatabase, "friends");
    }

    /**
     * Adds a friend to the database
     *
     * @param uuid uuid of the friend
     */
    public void addFriend(@Nonnull UUID uuid) {
        final OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if (!player.hasPlayedBefore()) {
            return;
        }

        final String playerName = player.getName();

        //fetchDocument(document -> {
        //    document.put(
        //            uuid.toString(),
        //            new Document("last_known_name", playerName == null ? "null" : playerName).append("since", System.currentTimeMillis())
        //    );
        //});
    }

    public boolean isFriend(UUID uuid) {
        //return fetchFromDocument(document -> document.containsKey(uuid.toString()));
        return false;
    }

    public void removeFriend(UUID uuid) {
        //fetchDocument(document -> document.remove(uuid.toString()));
    }

    @Nonnull
    public List<UUID> getFriends() {
        final List<UUID> friends = Lists.newArrayList();

        //fetchDocument(document -> {
        //    for (String key : document.keySet()) {
        //        friends.add(UUID.fromString(key));
        //    }
        //});

        return friends;
    }

    public long getFriendSince(UUID uuid) {
        //return fetchFromDocument(document -> document.get(uuid.toString(), new Document()).get("since", 0L));
        return 0L;
    }

}
