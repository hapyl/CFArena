package me.hapyl.fight.anticheat;

import me.hapyl.fight.database.serialize.MongoSerializableConstructor;
import me.hapyl.fight.database.serialize.MongoSerializableField;
import me.hapyl.fight.database.serialize.MongoSerializable;
import me.hapyl.fight.infraction.HexID;

import javax.annotation.Nonnull;
import java.util.UUID;

public class PunishmentReport implements MongoSerializable {

    @MongoSerializableField
    private HexID punishmentId;

    @MongoSerializableField
    private UUID punisherId;

    @MongoSerializableField
    private String reason;

    @MongoSerializableField
    private long timeStamp;

    @MongoSerializableConstructor
    PunishmentReport() {
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
    public HexID getPunishmentId() {
        return punishmentId;
    }

    @Nonnull
    public UUID getPunisherId() {
        return punisherId;
    }

    @Nonnull
    public String getReason() {
        return reason;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public String toString() {
        return "PunishmentReport{" +
                "punishmentId=" + punishmentId +
                ", punisherId=" + punisherId +
                ", reason=" + reason +
                ", timeStamp=" + timeStamp +
                "}";
    }
}
