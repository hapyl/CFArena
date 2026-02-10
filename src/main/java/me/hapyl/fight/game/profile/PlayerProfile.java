package me.hapyl.fight.game.profile;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.player.PlayerSkin;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.database.rank.RankFormatter;
import me.hapyl.fight.fastaccess.PlayerFastAccess;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.challenge.PlayerChallengeList;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.loadout.HotBarLoadout;
import me.hapyl.fight.activity.ActivityInstance;
import me.hapyl.fight.game.profile.data.PlayerProfileData;
import me.hapyl.fight.game.profile.relationship.PlayerRelationship;
import me.hapyl.fight.game.team.Entry;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.team.LocalTeamManager;
import me.hapyl.fight.game.trial.Trial;
import me.hapyl.fight.game.ui.PlayerUI;
import me.hapyl.fight.infraction.PlayerInfraction;
import me.hapyl.fight.util.CFUtils;
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
    
    private final PlayerProfileData playerData;
    private final PlayerInfraction infractions;
    private final PlayerRelationship relationship;
    private final HotBarLoadout hotbarLoadout;
    private final PlayerFastAccess fastAccess;
    private final LocalTeamManager localTeamManager;
    private final PlayerChallengeList challengeList;
    private final PlayerSocialConversation conversation;
    private final PlayerUI playerUI;
    
    @Nullable
    private GamePlayer gamePlayer; // GamePlayer instance is only created for a game
    private Hero selectedHero;
    
    private PlayerSkin originalSkin;
    private Trial trial; // FIXME @Jun 09, 2025 (xanyjl) -> This is dog-shit for the castle
    private boolean resourcePack;
    
    public PlayerProfile(@Nonnull Player player) {
        this.player = player;
        
        // Init database before anything else
        PlayerDatabase.instantiate(player);
        
        this.resourcePack = false;
        
        // Load data here
        this.localTeamManager = new LocalTeamManager(this);
        this.infractions = new PlayerInfraction(this);
        this.relationship = new PlayerRelationship(this);
        this.playerData = new PlayerProfileData(this);
        this.originalSkin = PlayerSkin.of(player);
        this.hotbarLoadout = new HotBarLoadout(this);
        this.fastAccess = new PlayerFastAccess(this);
        this.challengeList = new PlayerChallengeList(this);
        this.conversation = new PlayerSocialConversation(this);
        
        // Add to a team
        GameTeam.addMemberIfNotInTeam(this);
        
        // Load some data after init method
        this.selectedHero = getDatabase().heroEntry.getSelectedHero();
        this.playerUI = new PlayerUI(this);
        
        // Prompt Resource Pack
        //promptResourcePack();
        
        // Load Deliveries
        // GameTask.runLater(
        //         () -> Deliveries.notify(player), 20
        // );
    }
    
    @Deprecated //
    public void activity(@Nonnull ActivityInstance handler) {
    
    }
    
    @Nonnull
    public PlayerSocialConversation getConversation() {
        return conversation;
    }
    
    @Nonnull
    public PlayerChallengeList getChallengeList() {
        return challengeList;
    }
    
    public void newTrial() {
        if (trial != null) {
            throw new IllegalStateException("Trial already is in progress!");
        }
        
        trial = new Trial(this);
        trial.onStart();
    }
    
    public boolean stopTrial() {
        if (trial == null) {
            return false;
        }
        
        trial.onStop();
        trial = null;
        return true;
    }
    
    @Nullable
    public Trial getTrial() {
        return trial;
    }
    
    public boolean hasTrial() {
        return trial != null;
    }
    
    @Nonnull
    public PlayerFastAccess getFastAccess() {
        return fastAccess;
    }
    
    @Nonnull
    public HotBarLoadout getHotbarLoadout() {
        return hotbarLoadout;
    }
    
    @Nonnull
    public PlayerRelationship getPlayerRelationship() {
        return relationship;
    }
    
    @Nonnull
    public PlayerInfraction getInfractions() {
        return infractions;
    }
    
    @Nonnull
    public PlayerProfileData getPlayerData() {
        return playerData;
    }
    
    @Nonnull
    public PlayerSkin getOriginalSkin() {
        return originalSkin;
    }
    
    public void setOriginalSkin(@Nonnull PlayerSkin originalSkin) {
        this.originalSkin = originalSkin;
    }
    
    public void resetSkin() {
        if (originalSkin != null) {
            originalSkin.apply(player);
        }
    }
    
    public boolean isResourcePack() {
        return resourcePack;
    }
    
    public void setResourcePack() {
        this.resourcePack = true;
    }
    
    @Nonnull
    public PlayerDisplay display() {
        return new PlayerDisplay(getDatabase());
    }
    
    @Nonnull
    public LocalTeamManager getLocalTeamManager() {
        return localTeamManager;
    }
    
    @Nonnull
    public Player getPlayer() {
        return player;
    }
    
    /**
     * @see CF#getDatabase(Player)
     * //@deprecated {@link PlayerDatabase} can exist without {@link PlayerProfile}
     */
    @Nonnull
    // @Deprecated
    public PlayerDatabase getDatabase() {
        return CF.getDatabase(player);
    }
    
    @Nullable
    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }
    
    public void setGamePlayer(@Nullable GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }
    
    @Nonnull
    public GamePlayer getOrCreateGamePlayer() {
        return gamePlayer != null ? gamePlayer : createGamePlayer();
    }
    
    /**
     * Creates a new game player.
     *
     * @return a new game player.
     */
    @Nonnull
    public GamePlayer createGamePlayer() {
        this.gamePlayer = Manager.current().registerEntity(new GamePlayer(this));
        
        if (CF.environment().debug.isEnabled()) {
            CFUtils.dumpStackTrace();
        }
        
        return gamePlayer;
    }
    
    public boolean isHidden() {
        return false;
    }
    
    @Nonnull
    public String getTeamFlag() {
        final GameTeam team = GameTeam.getEntryTeam(Entry.of(player));
        
        return team != null ? team.getFlagColored() + " " + team.getNameSmallCapsColorized() : "&8None!";
    }
    
    public void resetGamePlayer() {
        gamePlayer = null;
    }
    
    @Nonnull
    public Hero getSelectedHero() {
        return selectedHero;
    }
    
    public void setSelectedHero(@Nonnull Hero selectedHero) {
        setSelectedHero(selectedHero, true);
    }
    
    public void setSelectedHero(@Nonnull Hero hero, boolean save) {
        this.selectedHero = hero;
        
        if (save) {
            // Store to database here duh
            getDatabase().heroEntry.setSelectedHero(hero);
        }
    }
    
    public String getSelectedHeroString() {
        return getDatabase().randomHeroEntry.isEnabled() ? "&l❓&f ʀᴀɴᴅᴏᴍ" : selectedHero.getFormatted();
    }
    
    @Nonnull
    public PlayerUI getPlayerUI() {
        return playerUI;
    }
    
    public void promptResourcePack() {
        player.setResourcePack(
                PlayerProfile.RESOURCE_PACK_URI, null, """
                                                       
                                                       §b§lOPTIONAL
                                                       §aDownload resource pack for additional features?
                                                       
                                                       §e§lSome of the features:
                                                       §bBetter effects
                                                       §bCustom 3d models
                                                       §bFancier text
                                                       """
        );
    }
    
    @Nonnull
    public UUID getUuid() {
        return player.getUniqueId();
    }
    
    @Nonnull
    public Hero getHero() {
        return selectedHero;
    }
    
    @Nonnull
    public PlayerRank getRank() {
        return getDatabase().getRank();
    }
    
    @Nullable
    public String getJoinMessage() {
        final RankFormatter format = getRank().getFormat();
        final String message = format.joinMessage();
        
        return message != null ? Chat.format("&8[&a+&8] " + message.formatted(display().toString())) : null;
    }
    
    @Nullable
    public String getLeaveMessage() {
        final RankFormatter format = getRank().getFormat();
        final String message = format.leaveMessage();
        
        return message != null ? Chat.format("&8[&c-&8] " + message.formatted(display().toString())) : null;
    }
    
    @Nullable
    public String getJoinOrQuitMessage(boolean join) {
        // If the game is in progress, don't send join/quit messages
        if (Manager.current().isGameInProgress()) {
            return null;
        }
        
        return join ? getJoinMessage() : getLeaveMessage();
    }
    
    @Nonnull
    public Entry getEntry() {
        return Entry.of(player);
    }
    
    public void applyOriginalSkin() {
        originalSkin.apply(player);
    }
    
}
