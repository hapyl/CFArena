package me.hapyl.fight.game.profile;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.ScoreboardTeams;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.setting.Setting;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.ui.GamePlayerUI;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

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
    private boolean buildMode;

    public PlayerProfile(@Nonnull Player player) {
        this.player = player;

        // Init player database first before loading everything else
        this.playerDatabase = new PlayerDatabase(player);

        this.scoreboardTeams = new ScoreboardTeams(player);
        this.display = new ProfileDisplay(this);
        this.loaded = false;
        this.resourcePack = false;
        this.buildMode = false;
    }

    public boolean isBuildMode() {
        return buildMode;
    }

    public void setBuildMode(boolean buildMode) {
        this.buildMode = buildMode;
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

    /**
     * Creates a new game player.
     *
     * @return a new game player.
     */
    @Nonnull
    public GamePlayer createGamePlayer() {
        this.gamePlayer = new GamePlayer(this);
        return gamePlayer;
    }

    /**
     * Gets rid of the current game player.
     *
     * @return the old game player if existed.
     */
    @Nullable
    public GamePlayer deleteGamePlayer() {
        final GamePlayer oldGamePlayer = gamePlayer;
        this.gamePlayer = null;

        return oldGamePlayer;
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

    public String getSelectedHeroString() {
        final boolean randomHeroEnabled = Setting.RANDOM_HERO.isEnabled(player);
        return randomHeroEnabled ? "&l❓&f ʀᴀɴᴅᴏᴍ" : selectedHero.getFormatted();
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
                §bFancier text
                """);
    }

    public UUID getUuid() {
        return player.getUniqueId();
    }

    public Heroes getHero() {
        return selectedHero;
    }

    public Hero getHeroHandle() {
        return selectedHero.getHero();
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
