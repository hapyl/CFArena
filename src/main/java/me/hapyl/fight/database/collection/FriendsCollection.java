package me.hapyl.fight.database.collection;

import me.hapyl.fight.Main;
import org.bson.Document;

import java.util.UUID;

// Stores friends relationships.
// Yes, I know you don't have any, but don't worry, neither do I.
public class FriendsCollection extends AsynchronousDatabase {
    public FriendsCollection(Document filter) {
        super(Main.getPlugin().getDatabase().friends, filter);
    }

    public boolean isFriends(UUID a, UUID b) {
        return false;
    }

}
