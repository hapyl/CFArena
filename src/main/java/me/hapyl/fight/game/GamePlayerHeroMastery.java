package me.hapyl.fight.game;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;

import javax.annotation.Nonnull;
import java.util.Map;

public class GamePlayerHeroMastery {

    private final Map<GamePlayer, Integer> playerMasteryMap;

    public GamePlayerHeroMastery(@Nonnull GameInstance instance) {
        this.playerMasteryMap = Maps.newHashMap();

        // Because mastery is not retroactive, to avoid leveling up mid-game
        // the mastery is stored before the game and accessed from here instead of directly from the database
        final Manager manager = Manager.current();

        manager.getPlayers().forEach(player -> {
            final FairMode fairMode = manager.getFairMode();

            // It is impossible to change the hero during the game, so don't care to store the hero
            final Hero hero = player.getHero();

            int mastery = fairMode == FairMode.UNFAIR
                    ? player.getDatabase().masteryEntry.getLevel(hero)
                    : fairMode.getValue();

            playerMasteryMap.put(player, mastery);
        });
    }

    public int getMastery(@Nonnull GamePlayer player) {
        return playerMasteryMap.getOrDefault(player, 0);
    }
}
