package me.hapyl.fight.game.profile;

import com.google.common.collect.Queues;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.dialog.ActiveDialog;
import me.hapyl.fight.fastaccess.PlayerFastAccess;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.delivery.Deliveries;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.loadout.HotbarLoadout;
import me.hapyl.fight.game.playerskin.PlayerSkin;
import me.hapyl.fight.game.profile.data.PlayerProfileData;
import me.hapyl.fight.game.profile.relationship.PlayerRelationship;
import me.hapyl.fight.game.setting.Settings;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.team.Entry;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.team.LocalTeamManager;
import me.hapyl.fight.game.ui.GamePlayerUI;
import me.hapyl.fight.infraction.PlayerInfraction;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Deque;
import java.util.Optional;
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
    private final LocalTeamManager localTeamManager;
    private final PlayerSkin originalSkin;
    private final PlayerProfileData playerData;
    private final PlayerInfraction infractions;
    private final PlayerRelationship relationship;
    private final HotbarLoadout hotbarLoadout;
    private final PlayerFastAccess fastAccess;

    @Nullable
    private GamePlayer gamePlayer; // current game player
    private GamePlayerUI playerUI; // ui
    private Heroes selectedHero;   // selected hero

    private boolean loaded;
    private boolean resourcePack;
    private boolean buildMode;
    public ActiveDialog dialog;

    public PlayerProfile(@Nonnull Player player) {
        this.player = player;

        // Init database before anything else
        this.playerDatabase = new PlayerDatabase(player);
        this.localTeamManager = new LocalTeamManager(player);
        this.loaded = false;
        this.resourcePack = false;
        this.buildMode = false;
        this.infractions = new PlayerInfraction(this);
        this.relationship = new PlayerRelationship(this);
        this.playerData = new PlayerProfileData(this);
        this.originalSkin = PlayerSkin.of(player);
        this.hotbarLoadout = new HotbarLoadout(this);
        this.fastAccess = new PlayerFastAccess(this);
    }

    @Nonnull
    public PlayerFastAccess getFastAccess() {
        return fastAccess;
    }

    @Nonnull
    public HotbarLoadout getHotbarLoadout() {
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

    @Nonnull
    public LocalTeamManager getLocalTeamManager() {
        return localTeamManager;
    }

    public void loadData() {
        if (loaded) {
            return;
        }

        // Check for fullness to not create anything
        loaded = true;

        // Load some data after init method
        selectedHero = playerDatabase.getHeroEntry().getSelectedHero();
        GameTeam.addMemberIfNotInTeam(this);
        playerUI = new GamePlayerUI(this);

        // Prompt Resource Pack
        promptResourcePack();

        // Load Deliveries
        GameTask.runLater(() -> {
            Deliveries.notify(player);
        }, 20);
    }

    @Nonnull
    public Player getPlayer() {
        return player;
    }

    @Nonnull
    public PlayerDatabase getDatabase() {
        return playerDatabase;
    }

    @Nullable
    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    @Nonnull
    public GamePlayer getOrCreateGamePlayer() {
        return gamePlayer != null ? gamePlayer : createGamePlayer();
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
        this.gamePlayer = Manager.current().registerGamePlayer(new GamePlayer(this));

        if (Manager.current().getDebug().any()) {
            final RuntimeException exception = new RuntimeException();
            createTraceDump(exception);
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

    private void createTraceDump(RuntimeException exception) {
        final StackTraceElement[] stackTrace = exception.getStackTrace();
        final Deque<String> deque = Queues.newArrayDeque();

        for (int i = stackTrace.length - 1; i >= 0; i--) {
            final StackTraceElement trace = stackTrace[i];
            final String classLoaderName = trace.getClassLoaderName();

            if (classLoaderName == null || !classLoaderName.contains("ClassesFightArena.jar")) {
                continue;
            }

            String fileName = trace.getFileName();

            if (fileName != null) {
                fileName = fileName.replace(".java", "");
            }

            final String methodName = trace.getMethodName();
            deque.offer(fileName + "." + methodName + ":" + trace.getLineNumber());
        }

        final StringBuilder builder = new StringBuilder();

        int index = 0;
        for (String string : deque) {
            if (index != 0) {
                builder.append("\n");
            }

            builder.append(index % 2 == 0 ? ChatColor.BLUE : ChatColor.RED);

            if (index != deque.size() - 1) {
                builder.append("├─");
            }
            else {
                builder.append("└─");
            }

            builder.append(string);

            index++;
        }

        Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(player -> {
            Chat.sendHoverableMessage(
                    player,
                    builder.toString(),
                    "&c&lDEBUG &e" + deque.getFirst() + " requested GamePlayer creation! &6&lHOVER"
            );
        });
    }

    public void resetGamePlayer() {
        gamePlayer = null;
    }

    @Nonnull
    public Heroes getSelectedHero() {
        return selectedHero;
    }

    public void setSelectedHero(Heroes selectedHero) {
        this.selectedHero = selectedHero;
    }

    public String getSelectedHeroString() {
        final boolean randomHeroEnabled = Settings.RANDOM_HERO.isEnabled(player);
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

    @Nullable
    public static PlayerProfile getProfile(Player player) {
        return Manager.current().getProfile(player);
    }

    @Nonnull
    public static Optional<PlayerProfile> getProfileOptional(Player player) {
        return Optional.ofNullable(getProfile(player));
    }

}
