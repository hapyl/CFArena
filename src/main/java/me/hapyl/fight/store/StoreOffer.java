package me.hapyl.fight.store;

import me.hapyl.eterna.module.entity.packet.PacketItem;
import me.hapyl.eterna.module.hologram.Hologram;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.talents.Removable;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class StoreOffer implements Removable {

    private final PlayerDatabase database;
    private final Cosmetic offer;
    private final PacketItem item;
    private final Hologram hologram;
    private final String toString;

    public StoreOffer(Player player, Location location, Cosmetic offer) {
        this.database = CF.getDatabase(player);
        this.offer = offer;

        this.item = PacketItem.create(location, offer.createItem(player).toItemStack());
        this.item.show(player);

        // The FIREWORKS (with S) cosmetic is used as "hey, you own so many items we couldn't generate anything."
        location.add(0, 0.5, 0);

        this.hologram = new Hologram();

        this.toString = Color.SUCCESS + formatOffer();
        this.hologram
                .addLine(Color.SUCCESS + offer.getName())
                .addLine(offer.getRarityString());

        this.hologram.create(location).show(player);
    }

    @Override
    public String toString() {
        if (database.cosmeticEntry.isUnlocked(offer)) {
            return this.toString + " &a&l✔";
        }

        return this.toString;
    }

    @Override
    public void remove() {
        this.item.hideGlobally();
        this.hologram.destroy();
    }

    @Nonnull
    public Location getLocation() {
        return item.getLocation();
    }

    @Nonnull
    public Cosmetic getOffer() {
        return offer;
    }

    private String formatOffer() {
        if (offer.equals(Cosmetic.getNullCosmetic())) {
            return "No offer!";
        }
        else {
            return "%s &8(%s&8)".formatted(offer.getName(), offer.getRarity().getFormatted());
        }
    }

}
