package me.hapyl.fight.anticheat;

import me.hapyl.fight.database.serialize.MongoSerializable;
import me.hapyl.fight.database.serialize.MongoSerializableConstructor;
import me.hapyl.fight.infraction.HexID;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.util.UUID;

public class PunishmentReport implements MongoSerializable {
    
    private HexID punishmentId;
    private UUID punisherId;
    private String reason;
    private long timeStamp;
    
    @MongoSerializableConstructor
    private PunishmentReport() {
    }
    
    public PunishmentReport(
            @Nonnull HexID punishmentId,
            @Nonnull UUID punisherId,
            @Nonnull String reason,
            long timeStamp
    ) {
        this.punishmentId = punishmentId;
        this.punisherId = punisherId;
        this.reason = reason;
        this.timeStamp = timeStamp;
    }
    
    @Nonnull
    public HexID punishmentId() {
        return punishmentId;
    }
    
    @Nonnull
    public UUID punisherId() {
        return punisherId;
    }
    
    @Nonnull
    public String reason() {
        return reason;
    }
    
    public long timeStamp() {
        return timeStamp;
    }
    
    @Nonnull
    @Override
    public Document serialize() {
        return new Document()
                .append("punishment_id", this.punishmentId.toString())
                .append("punisher_id", this.punisherId.toString())
                .append("reason", this.reason)
                .append("time_stamp", this.timeStamp);
    }
    
    @Override
    public void deserialize(@Nonnull Document document) {
        this.punishmentId = HexID.fromString(document.getString("punishment_id"));
        this.punisherId = UUID.fromString(document.getString("punisher_id"));
        this.reason = document.getString("reason");
        this.timeStamp = document.getLong("time_stamp");
    }
}
