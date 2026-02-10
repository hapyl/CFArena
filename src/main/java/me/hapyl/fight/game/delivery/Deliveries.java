package me.hapyl.fight.game.delivery;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.chat.LazyEvent;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.color.Color;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Deliveries are automatically given to players upon login.
 */
// FIXME (Fri, Aug 30 2024 @xanyjl): deprecated
public enum Deliveries implements Handle<Delivery> {


    ;

    private final Delivery delivery;

    Deliveries(Delivery delivery) {
        this.delivery = delivery;
    }

    public boolean isDelivered(@Nonnull Player player) {
        return CF.getDatabase(player).deliveryEntry.isDelivered(this);
    }

    public void deliver(@Nonnull Player player) {
        if (isDelivered(player)) {
            Chat.sendMessage(player, "&b&lᴅᴇʟɪᴠᴇʀʏ &cYou already claimed this delivery!");
            PlayerLib.villagerNo(player);
            return;
        }

        delivery.deliver(player);
        CF.getDatabase(player).deliveryEntry.setDelivered(this);

        // Fx
        Chat.sendMessage(player, "&b&lᴅᴇʟɪᴠᴇʀʏ " + Color.SUCCESS + "Successfully claimed %s!".formatted(delivery.getName()));
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

        Chat.sendMessage(player, "&b&lᴅᴇʟɪᴠᴇʀʏ &aYou have %s unclaimed deliveries!".formatted(unclaimedList.size()));
        Chat.sendClickableHoverableMessage(
                player,
                LazyEvent.runCommand("/delivery"),
                LazyEvent.showText("&7Click to claim deliveries!"),
                "&b&lᴅᴇʟɪᴠᴇʀʏ &6&lCLICK HERE &eto claim them!"
        );

        PlayerLib.playSound(player, Sound.ENTITY_CAT_AMBIENT, 1.0f);
    }

}
