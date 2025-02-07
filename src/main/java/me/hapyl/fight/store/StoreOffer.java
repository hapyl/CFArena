package me.hapyl.fight.store;

import me.hapyl.eterna.module.hologram.Hologram;
import me.hapyl.eterna.module.util.Vectors;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.talents.Removable;
import me.hapyl.fight.garbage.SynchronizedGarbageEntityCollector;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class StoreOffer implements Removable {

    private final Cosmetic offer;
    private final Item item;
    private final Hologram hologram;
    private final String toString;

    public StoreOffer(Player player, Location location, Cosmetic offer) {
        this.offer = offer;

        this.item = location.getWorld().spawn(location, Item.class, self -> {
                    self.setItemStack(offer.createItem(player).toItemStack());
                    self.setVelocity(Vectors.ZERO);

                    self.setUnlimitedLifetime(true);
                    self.setCanPlayerPickup(false);
                    self.setCanMobPickup(false);
                    self.setGravity(false);

                    self.setVisibleByDefault(false);

                    // The item is only visible to the player
                    player.showEntity(CF.getPlugin(), self);

                    // Add to gc because for some reason it's not getting removed on reload
                    SynchronizedGarbageEntityCollector.add(self);
                }
        );

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
        return this.toString;
    }

    @Override
    public void remove() {
        this.item.remove();
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
