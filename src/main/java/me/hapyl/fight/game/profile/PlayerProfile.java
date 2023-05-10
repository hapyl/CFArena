package me.hapyl.fight.game.profile;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.ScoreboardTeams;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.ui.GamePlayerUI;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class stores all player data while the player is online,
 * and deletes once player leaves.
 */
public class PlayerProfile {

    private final Player player;
    private final PlayerDatabase playerDatabase;
    private final ScoreboardTeams scoreboardTeams;
    private final ProfileDisplay display;

    @Nullable
    private GamePlayer gamePlayer; // current game player
    private GamePlayerUI playerUI; // ui
    private Heroes selectedHero;   // selected hero

    private boolean loaded;

    public PlayerProfile(@Nonnull Player player) {
        this.player = player;

        // Init player database
        this.playerDatabase = new PlayerDatabase(player);
        this.scoreboardTeams = new ScoreboardTeams(player);
        this.display = new ProfileDisplay(this);
        this.loaded = false;
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
        selectedHero = playerDatabase.getHeroEntry().getSelectedHero();
        GameTeam.addMemberIfNotInTeam(player);
        playerUI = new GamePlayerUI(this);
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerDatabase getDatabase() {
        return playerDatabase;
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

    @Nonnull
    public static PlayerProfile getOrCreateProfile(Player player) {
        return Manager.current().getOrCreateProfile(player);
    }

    @Nullable
    public static PlayerProfile getProfile(Player player) {
        return Manager.current().getProfile(player);
    }

}
