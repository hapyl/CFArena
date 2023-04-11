package me.hapyl.fight.game.profile;

import me.hapyl.fight.Main;
import me.hapyl.fight.database.Database;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.ScoreboardTeams;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.ui.GamePlayerUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class stores all player data while the player is online,
 * and deletes once player leaves.
 */
public class PlayerProfile {

    private final Player player;
    private final Database database;
    private final ScoreboardTeams scoreboardTeams;
    private final ProfileDisplay display;

    @Nullable
    private GamePlayer gamePlayer; // current game player
    private GamePlayerUI playerUI; // ui
    private Heroes selectedHero;   // selected hero

    private boolean loaded;

    public PlayerProfile(Player player) {
        this.player = player;

        // Init database
        final Main main = Main.getPlugin();
        if (main.isDatabaseLegacy()) {
            main.getLogger().severe("Legacy database is not supported anymore!");
            Bukkit.getPluginManager().disablePlugin(main);
            throw new RuntimeException("Legacy database is not supported anymore!");
        }
        else {
            this.database = new Database(player);
        }
        this.scoreboardTeams = new ScoreboardTeams(player);
        this.display = new ProfileDisplay(this);
        this.loaded = false;
    }

    @Nonnull
    public static PlayerProfile getProfile(Player player) {
        return Manager.current().getProfile(player);
    }

    public ProfileDisplay getDisplay() {
        return display;
    }

    public ScoreboardTeams getScoreboardTeams() {
        return scoreboardTeams;
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
