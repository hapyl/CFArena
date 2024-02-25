package me.hapyl.fight.game.cosmetic;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.IGameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.State;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

public class CosmeticsListener implements Listener {

    @EventHandler()
    public void handlePlayerMoveEvent(PlayerMoveEvent ev) {
        final Player player = ev.getPlayer();
        final Location from = ev.getFrom();
        final Location to = ev.getTo();

        // Check if player moves a full block
        if (to == null || (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ())) {
            return;
        }

        // Check if player has a contrail
        final Cosmetics selectedContrail = PlayerDatabase.getDatabase(player).cosmeticEntry.getSelected(Type.CONTRAIL);
        if (selectedContrail == null) {
            return;
        }

        // Check for player
        if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            return;
        }

        // Don't display if game is not started but exists
        final IGameInstance currentGame = Manager.current().getCurrentGame();

        if (currentGame.isReal() && currentGame.getGameState() != State.IN_GAME) {
            return;
        }

        selectedContrail.getCosmetic().onDisplay0(new Display(player, player.getLocation()));
    }

}
