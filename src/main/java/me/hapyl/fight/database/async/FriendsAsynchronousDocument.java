package me.hapyl.fight.database.async;

import me.hapyl.fight.CF;
import me.hapyl.fight.database.NamedCollection;
import org.bson.Document;

import java.util.UUID;

// Stores friends relationships.
// Yes, I know you don't have any, but don't worry, neither do I.
public class FriendsAsynchronousDocument extends AsynchronousDocument {
    public FriendsAsynchronousDocument(Document filter) {
        super(CF.getServerDatabase().collection(NamedCollection.FRIENDS), filter);
    }

    public boolean isFriends(UUID a, UUID b) {
        return false;
    }

}
