package me.hapyl.fight.fastaccess;

import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.Notifier;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.player.PlayerLib;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class FastAccessListener implements Listener {

    @EventHandler()
    public void handleInventoryClickEvent(InventoryClickEvent ev) {
        if (!(ev.getWhoClicked() instanceof Player player)) {
            return;
        }

        final Inventory clickedInventory = ev.getClickedInventory();

        if (clickedInventory != null && clickedInventory.getType() != InventoryType.PLAYER) {
            return;
        }

        if (Manager.current().isInGameOrTrial(player) || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        final int rawSlot = ev.getRawSlot();
        if (rawSlot < 9 || rawSlot > 17) {
            return;
        }

        final PlayerProfile profile = PlayerProfile.getProfile(player);

        if (profile == null) {
            return;
        }

        final PlayerFastAccess playerFastAccess = profile.getFastAccess();
        final int index = rawSlot - 9;

        final PlayerRank rankToAccess = FastAccess.slowRankMap.get(index);
        final PlayerRank playerRank = profile.getRank();

        ev.setCancelled(true);

        if (!playerRank.isOrHigher(rankToAccess)) {
            Notifier.error(player, "You must be {%s} or higher to use this slot!".formatted(rankToAccess.getPrefixWithFallback()));
            PlayerLib.villagerNo(player);
            return;
        }

        final FastAccess fastAccess = playerFastAccess.getFastAccess(index);

        if (fastAccess == null) {
            new FastAccessGUI(profile, index);
            return;
        }

        if (fastAccess.hasCooldown(player)) {
            Chat.sendMessage(player, Color.ERROR + "Please wait a bit before doing this!");
            PlayerLib.villagerNo(player);
            return;
        }

        final ClickType clickType = ev.getClick();

        // Use
        if (clickType.isLeftClick()) {
            fastAccess.onClick(player);
            fastAccess.startCooldown(player);

            // Needed to update lore
            playerFastAccess.update();
        }
        // Edit
        else {
            new FastAccessGUI(profile, index);
        }

    }

}
