package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.delivery.Deliveries;

public class DeliveryEntry extends PlayerDatabaseEntry {
    public DeliveryEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
    }

    public void setDelivered(Deliveries delivery) {
        setValue("delivery." + delivery.name(), true);
    }

    public boolean isDelivered(Deliveries delivery) {
        return getValue("delivery." + delivery.name(), false);
    }

}
