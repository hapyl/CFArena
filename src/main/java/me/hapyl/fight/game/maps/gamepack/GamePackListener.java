package me.hapyl.fight.game.maps.gamepack;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.IGameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.State;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.maps.GameMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class GamePackListener implements Listener {

    @EventHandler()
    public void handlePlayerMove(PlayerMoveEvent ev) {
        final IGameInstance gameInstance = Manager.current().getCurrentGame();

        if (!gameInstance.isReal() || gameInstance.getGameState() != State.IN_GAME) {
            return;
        }

        final GameMap currentMap = gameInstance.getEnumMap().getMap();
        final Player player = ev.getPlayer();
        final GamePlayer gamePlayer = CF.getPlayer(player);

        if (gamePlayer == null || !gamePlayer.isAlive()) {
            return;
        }

        for (GamePack pack : currentMap.getGamePacks()) {
            final ActivePack collisionPack = pack.getCollisionPack(player);

            if (collisionPack == null) {
                continue;
            }

            collisionPack.pickup0(gamePlayer);
            break;
        }

    }


}
