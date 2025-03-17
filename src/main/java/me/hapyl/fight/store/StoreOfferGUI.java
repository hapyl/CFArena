package me.hapyl.fight.store;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.player.dialog.Dialog;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.entry.CosmeticEntry;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledTexture;
import me.hapyl.fight.npc.StoreOwnerNPC;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.store.escrow.Escrow;
import me.hapyl.fight.store.escrow.EscrowException;
import org.bukkit.entity.Player;

public class StoreOfferGUI extends StyledGUI {

    private final StoreOffer offer;
    private final CosmeticEntry entry;

    public StoreOfferGUI(Player player, StoreOffer offer) {
        super(player, "View Offer", Size.FOUR);

        this.offer = offer;
        this.entry = CF.getDatabase(player).cosmeticEntry;

        openInventory();
    }

    @Override
    public void onUpdate() {
        setHeader(StyledTexture.ICON_STORE.asIcon());

        final Cosmetic cosmetic = offer.getOffer();
        final ItemBuilder builder = cosmetic.createItem(player);

        // We assume that we bought it player already has the
        // cosmetic since it can't generate owned cosmetics
        final boolean hasPurchased = entry.isUnlocked(cosmetic);

        builder.addLore();

        if (cosmetic.isPurchasable()) {
            builder.addLore("&ePrice: " + cosmetic.getPriceFormatted());
            builder.addLore();

            if (hasPurchased) {
                builder.addLore(Color.SUCCESS.bold() + "PURCHASED!");
            }
            else {
                builder.addLore(Color.BUTTON + "Click to purchase!");
            }
        }

        // Preview
        if (cosmetic.hasPreview()) {
            builder.addLore(Color.BUTTON_DARKER + "Right Click to preview!");
        }

        setItem(22, builder.asIcon(), player -> {
            if (!cosmetic.isPurchasable()) {
                return;
            }

            if (hasPurchased) {
                Message.ERROR.sendWithSound(player, "You have already purchased this!");
                return;
            }

            new Escrow(player, cosmetic) {
                @Override
                public void deliverProduct() throws EscrowException {
                    if (cosmetic.isUnlocked(player)) {
                        throw new EscrowException("You already own this cosmetic!");
                    }

                    cosmetic.setUnlocked(player, true);
                }

                @Override
                public void productDelivered() {
                    final StoreOwnerNPC npc = Registries.npcs().STORE_OWNER;
                    final long price = cosmetic.getPrice();

                    new Dialog()
                            .addEntry(npc, "Thanks for the purchase~")
                            .addEntry(npc, "I will add %,d to the coins spent.".formatted(price))
                            .startForcefully(player);

                    // Add coins
                    database.storeEntry.addCoinsSpent(price);
                }
            }.completeTransaction();
        });
    }
}
