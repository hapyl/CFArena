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
 * and deletes once the player leaves.
 */
public class PlayerProfile {

    public static final String RESOURCE_PACK_URI = "https://download.mc-packs.net/pack/38a5079ae119f7159b56de8b22da9de089f82cc2.zip";
    public static final String RESOURCE_PACK_HASH = "38a5079ae119f7159b56de8b22da9de089f82cc2";

    private final Player player;
    private final PlayerDatabase playerDatabase;
    private final ScoreboardTeams scoreboardTeams;
    private final ProfileDisplay display;

    @Nullable
    private GamePlayer gamePlayer; // current game player
    private GamePlayerUI playerUI; // ui
    private Heroes selectedHero;   // selected hero

    private boolean loaded;
    private boolean resourcePack;

    public PlayerProfile(@Nonnull Player player) {
        this.player = player;

        // Init player database
        this.playerDatabase = new PlayerDatabase(player);
        this.scoreboardTeams = new ScoreboardTeams(player);
        this.display = new ProfileDisplay(this);
        this.loaded = false;
        this.resourcePack = false;
    }

    public boolean isResourcePack() {
        return resourcePack;
    }

    public void setResourcePack() {
        this.resourcePack = true;
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

        // Prompt Resource Pack
        promptResourcePack();
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

    public void promptResourcePack() {
        if (true) {
            return;
        }

        player.setResourcePack(PlayerProfile.RESOURCE_PACK_URI, null, """
                                
                §b§lOPTIONAL
                §aDownload resource pack for additional features?
                                    
                §e§lSome of the features:
                §bBetter effects
                §bCustom 3d models
                §bFancies text
                """);
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
