package me.hapyl.fight.game.profile;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.database.Database;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.scoreboard.GamePlayerUI;
import me.hapyl.fight.game.team.GameTeam;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * This class stores all player data while the player is online,
 * and deletes once player leaves.
 */
public class PlayerProfile {

    private final Player player;
    private final Database database;

    @Nullable
    private GamePlayer gamePlayer; // current game player
    private Heroes selectedHero;   // selected hero
    private GamePlayerUI playerUI; // ui

    private boolean loaded;

    public PlayerProfile(Player player) {
        this.player = player;
        this.database = new Database(player);
        this.loaded = false;
    }

    public void loadData() {
        if (loaded) {
            return;
        }
        loaded = true;

        // load some data after init method
        selectedHero = database.getHeroEntry().getSelectedHero();
        GameTeam.getSmallestTeam().addToTeam(player);
        playerUI = new GamePlayerUI(this);
    }

    public Player getPlayer() {
        return player;
    }

    public Database getDatabase() {
        return database;
    }

    @Nullable
    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(@Nullable GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public void resetGamePlayer() {
        gamePlayer = null;
    }

    public Heroes getSelectedHero() {
        return selectedHero;
    }

    public void setSelectedHero(Heroes selectedHero) {
        this.selectedHero = selectedHero;
    }

    public GamePlayerUI getPlayerUI() {
        return playerUI;
    }

    public void setPlayerUI(GamePlayerUI playerUI) {
        this.playerUI = playerUI;
    }

    public void delete() {
        Manager.current().removeProfile(player);
    }

}
