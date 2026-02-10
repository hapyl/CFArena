package me.hapyl.fight.database.async;

import com.mongodb.client.MongoCollection;
import me.hapyl.fight.Main;
import me.hapyl.fight.anticheat.PunishmentReport;
import me.hapyl.fight.database.NamedCollection;
import me.hapyl.fight.database.serialize.MongoSerializable;
import me.hapyl.fight.infraction.HexID;
import org.bson.Document;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Stores anti cheat reports.
 * <br>
 * This is meant to be used statically.
 */
public class AntiCheatAsynchronousDocument {

    private static MongoCollection<Document> COLLECTION;

    /**
     * Posts the given {@link PunishmentReport}.
     *
     * @param report - Report to post.
     */
    public static void post(@Nonnull PunishmentReport report) {
        async(() -> collection().insertOne(report.serialize()));
    }

    /**
     * Gets a {@link PunishmentReport} from the given {@link HexID}.
     *
     * @param hexId - Report id.
     * @return a report.
     */
    @Nullable
    public static PunishmentReport get(@Nonnull HexID hexId) {
        final Document punishmentDocument = collection()
                .find(new Document("punishment_id", hexId.toString()))
                .first();

        if (punishmentDocument == null) {
            return null;
        }

        return MongoSerializable.deserialize(PunishmentReport.class, punishmentDocument);
    }

    /**
     * Gets all the {@link PunishmentReport} for the given player {@link UUID}.
     *
     * @param uuid - Player's UUID.
     * @return a list of reports for the given UUID.
     */
    @Nonnull
    public static List<PunishmentReport> get(@Nonnull UUID uuid) {
        final List<PunishmentReport> reports = new ArrayList<>();

        collection()
                .find(new Document("punisher_id", uuid.toString()))
                .forEach((Consumer<Document>) document -> {
                    reports.add(MongoSerializable.deserialize(PunishmentReport.class, document));
                });

        return reports;
    }

    private static void async(Runnable runnable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskAsynchronously(Main.getPlugin());
    }

    private static MongoCollection<Document> collection() {
        if (COLLECTION == null) {
            COLLECTION = Main.getPlugin().getDatabase().collection(NamedCollection.ANTI_CHEAT);
        }

        return COLLECTION;
    }

}
