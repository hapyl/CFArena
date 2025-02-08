package me.hapyl.fight.store.escrow;

import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.database.entry.CurrencyEntry;
import me.hapyl.fight.store.Purchasable;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public abstract class Escrow {

    protected final Player player;
    protected final Purchasable purchasable;
    protected final PlayerDatabase database;

    public Escrow(@Nonnull Player player, @Nonnull Purchasable purchasable) {
        this.player = player;
        this.purchasable = purchasable;
        this.database = CF.getDatabase(player);
    }

    /**
     * Called after the payment was collected.
     *
     * @throws EscrowException - If the product could not be delivered for any reason.
     *                         The escrow will refund the payment.
     */
    public abstract void deliverProduct() throws EscrowException;

    /**
     * Called after the product was successfully delivered.
     */
    public abstract void productDelivered();

    public final void completeTransaction() {
        final CurrencyEntry entry = database.currencyEntry;
        final Currency currency = purchasable.getCurrency();
        final long price = purchasable.getPrice();

        try {
            if (!purchasable.isPurchasable()) {
                Message.error(player, "This item is not purchasable!");
                return;
            }

            // Escrow should not be created if a player doesn't have enough currency, the check is a fail-safe
            if (!entry.has(currency, price)) {
                Message.error(player, "&oYou don't have enough {%s}!".formatted(currency.getName()));
                return;
            }

            // Put goods in escrow
            Message.info(player, "&oCollecting payment...");
            entry.subtract(currency, price);

            Message.info(player, "&oDelivering product...");
            deliverProduct();

            productDelivered();
            Message.sound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f);
        } catch (EscrowException e) {
            // Refund if failed to deliver the product
            entry.add(currency, price);

            Message.error(player, "&oSomething went wrong! {%s}".formatted(e.getMessage()));
            Message.info(player, "&oEscrow refunded {%s}.".formatted(currency.formatProduct(price)));

            Message.sound(player, Sound.BLOCK_ANVIL_LAND, 1.0f);
        } finally {
            player.closeInventory();
        }
    }

}
