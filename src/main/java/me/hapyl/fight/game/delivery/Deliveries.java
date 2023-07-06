package me.hapyl.fight.game.delivery;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.cosmetic.crate.Crates;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public enum Deliveries {

    LEGACY_CRATES(new Delivery("10 x Legacy Crates", "Since cosmetics are no longer purchasable, please take these crates!") {
        @Override
        public void deliver(@Nonnull Player player) {
            final PlayerDatabase database = PlayerDatabase.getDatabase(player);

            database.crateEntry.addCrate(Crates.LEGACY, 10);
        }
    }),

    ;

    private final Delivery delivery;

    Deliveries(Delivery delivery) {
        this.delivery = delivery;
    }

    public boolean isDelivered(@Nonnull Player player) {
        return PlayerDatabase.getDatabase(player).deliveryEntry.isDelivered(this);
    }

    public void deliver(@Nonnull Player player) {
        if (isDelivered(player)) {
            return;
        }

        delivery.deliver(player);
        PlayerDatabase.getDatabase(player).deliveryEntry.setDelivered(this);

        // Fx
        Chat.sendMessage(player, "&b&lDELIVERY! &a%s was successfully delivered!", delivery.getName());
        Chat.sendMessage(player, "&b&lDELIVERY! &7" + delivery.getMessage());

        PlayerLib.playSound(player, Sound.ENTITY_VILLAGER_YES, 1.25f);
    }

}
