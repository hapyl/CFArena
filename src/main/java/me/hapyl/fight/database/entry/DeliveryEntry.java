package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.delivery.Deliveries;

import javax.annotation.Nonnull;

public class DeliveryEntry extends PlayerDatabaseEntry {
    public DeliveryEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase, "delivery");
    }

    public void setDelivered(@Nonnull Deliveries delivery) {
        setValue(delivery.name(), true);
    }

    public boolean isDelivered(@Nonnull Deliveries delivery) {
        return getValue(delivery.name(), false);
    }

}
