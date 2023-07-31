package me.hapyl.fight.game.maps.gamepack;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.IGameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.State;
import me.hapyl.fight.game.maps.GameMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class HealthPackListener implements Listener {

    @EventHandler()
    public void handlePlayerMove(PlayerMoveEvent ev) {
        final IGameInstance gameInstance = Manager.current().getCurrentGame();

        if (!gameInstance.isReal() || gameInstance.getGameState() != State.IN_GAME) {
            return;
        }

        final GameMap currentMap = gameInstance.getMap().getMap();
        final Player player = ev.getPlayer();

        if (!GamePlayer.getPlayer(player).isAlive()) {
            return;
        }

        for (GamePack pack : currentMap.getGamePacks()) {
            final ActivePack collisionPack = pack.getCollisionPack(player);

            if (collisionPack == null) {
                continue;
            }

            collisionPack.pickup0(player);
            break;
        }

    }


}
