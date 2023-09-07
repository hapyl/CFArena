package me.hapyl.fight.game.profile;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.ScoreboardTeams;
import me.hapyl.fight.game.delivery.Deliveries;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.playerskin.PlayerSkin;
import me.hapyl.fight.game.profile.data.PlayerData;
import me.hapyl.fight.game.setting.Setting;
import me.hapyl.fight.game.task.GameTask;
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
    private final PlayerSkin originalSkin;
    private final PlayerData playerData;

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
        this.loaded = false;
        this.resourcePack = false;
        this.buildMode = false;

        this.playerData = new PlayerData(this);
        this.originalSkin = PlayerSkin.of(player);
    }

    @Nonnull
    public PlayerData getPlayerData() {
        return playerData;
    }

    @Nonnull
    public PlayerSkin getOriginalSkin() {
        return originalSkin;
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

    @Nonnull
    public ProfileDisplay getDisplay() {
        return new ProfileDisplay(this);
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

        // Load Deliveries
        GameTask.runLater(() -> {
            Deliveries.notify(player);
        }, 20);
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

    /**
     * Creates a new game player.
     *
     * @return a new game player.
     */
    @Nonnull
    public GamePlayer createGamePlayer() {
        this.gamePlayer = new GamePlayer(this);

        final RuntimeException exception = new RuntimeException();
        Debug.severe("Dumped GamePlayer creation for " + player.getName());
        exception.printStackTrace();

        return gamePlayer;
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

    public String getSelectedHeroString() {
        final boolean randomHeroEnabled = Setting.RANDOM_HERO.isEnabled(player);
        return randomHeroEnabled ? "&l❓&f ʀᴀɴᴅᴏᴍ" : selectedHero.getFormatted();
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
    public PlayerRank getRank() {
        return playerDatabase.getRank();
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
