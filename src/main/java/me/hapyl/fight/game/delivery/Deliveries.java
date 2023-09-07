package me.hapyl.fight.game.delivery;

import com.google.common.collect.Lists;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.crate.Crates;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.chat.LazyEvent;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Deliveries are automatically given to players upon login.
 */
public enum Deliveries implements Handle<Delivery> {

    LEGACY_CRATES(new Delivery("10 x Legacy Crates", "Since cosmetics are no longer purchasable, please take these crates!") {
        @Override
        public void deliver(@Nonnull Player player) {
            final PlayerDatabase database = PlayerDatabase.getDatabase(player);

            database.crateEntry.addCrate(Crates.LEGACY, 10);
        }
    }),

    PREFIX_CRATES(new Delivery("10 x Prefix Crates", "Due to prefix changes, please take these crates!") {
        @Override
        public void deliver(@Nonnull Player player) {
            final PlayerDatabase database = PlayerDatabase.getDatabase(player);

            database.crateEntry.addCrate(Crates.TITLE, 10);
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
            Chat.sendMessage(player, "&b&lᴅᴇʟɪᴠᴇʀʏ &cYou already claimed this delivery!");
            PlayerLib.villagerNo(player);
            return;
        }

        delivery.deliver(player);
        PlayerDatabase.getDatabase(player).deliveryEntry.setDelivered(this);

        // Fx
        Chat.sendMessage(player, "&b&lᴅᴇʟɪᴠᴇʀʏ " + Color.SUCCESS + "Successfully claimed %s!", delivery.getName());
        PlayerLib.villagerYes(player);
    }

    @Nonnull
    @Override
    public Delivery getHandle() {
        return delivery;
    }

    @Nonnull
    public static List<Deliveries> getUnclaimedDeliveries(Player player) {
        final List<Deliveries> unclaimedList = Lists.newArrayList();

        for (Deliveries delivery : values()) {
            if (delivery.isDelivered(player)) {
                continue;
            }

            unclaimedList.add(delivery);
        }

        return unclaimedList;
    }

    public static void notify(@Nonnull Player player) {
        final List<Deliveries> unclaimedList = getUnclaimedDeliveries(player);

        if (unclaimedList.isEmpty()) {
            return;
        }

        Chat.sendMessage(player, "&b&lᴅᴇʟɪᴠᴇʀʏ &aYou have %s unclaimed deliveries!", unclaimedList.size());
        Chat.sendClickableHoverableMessage(
                player,
                LazyEvent.runCommand("/delivery"),
                LazyEvent.showText("&7Click to claim deliveries!"),
                "&b&lᴅᴇʟɪᴠᴇʀʏ &6&lCLICK HERE &eto claim them!"
        );

        PlayerLib.playSound(player, Sound.ENTITY_CAT_AMBIENT, 1.0f);
    }

}
