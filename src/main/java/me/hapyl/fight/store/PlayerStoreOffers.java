package me.hapyl.fight.store;

import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.talents.Removable;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class PlayerStoreOffers implements Removable {

    private final Player player;
    private final StoreOffer[] offers;

    public PlayerStoreOffers(Store store, Player player, Cosmetic[] offers) {
        this.player = player;
        this.offers = new StoreOffer[4];

        for (int i = 0; i < this.offers.length; i++) {
            this.offers[i] = new StoreOffer(player, store.getStoreLocations(i), offers[i]);
        }
    }

    @Nonnull
    public Player getPlayer() {
        return player;
    }

    @Nonnull
    public StoreOffer[] getOffers() {
        return offers;
    }

    @Override
    public void remove() {
        for (StoreOffer offer : offers) {
            offer.remove();
        }
    }
}
