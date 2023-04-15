package me.hapyl.fight.game;

import com.google.common.collect.Maps;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.collection.HeroStatsCollection;
import me.hapyl.fight.game.cosmetic.skin.SkinEffectManager;
import me.hapyl.fight.game.gamemode.Modes;
import me.hapyl.fight.game.heroes.ComplexHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.lobby.LobbyItems;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.setting.Setting;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.trial.Trial;
import me.hapyl.fight.game.ui.GamePlayerUI;
import me.hapyl.fight.util.NonNullableElementHolder;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.ParamFunction;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.EternaPlugin;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.parkour.ParkourManager;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.glow.Glowing;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import me.hapyl.spigotutils.module.util.DependencyInjector;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Manager extends DependencyInjector<Main> {

    protected final NonNullableElementHolder<GameMaps> currentMap = new NonNullableElementHolder<>(GameMaps.ARENA);
    protected final NonNullableElementHolder<Modes> currentMode = new NonNullableElementHolder<>(Modes.FFA);
    private final Map<Player, PlayerProfile> profiles;

    // I really don't know why this is needed, but would I risk to removing it? Hell nah. -h
    private final Map<Integer, ParamFunction<Talent, Hero>> slotPerTalent = new HashMap<>();
    private final Map<Integer, ParamFunction<Talent, ComplexHero>> slotPerComplexTalent = new HashMap<>();

    private final SkinEffectManager skinEffectManager;
    private boolean isDebug = true;
    private GameInstance gameInstance; // @implNote: For now, only one game instance can be active at a time.
    private Trial trial;

    private final AutoSync autoSave;

    public Manager(Main main) {
        super(main);
        profiles = Maps.newConcurrentMap();

        slotPerTalent.put(1, Hero::getFirstTalent);
        slotPerTalent.put(2, Hero::getSecondTalent);
        slotPerComplexTalent.put(3, ComplexHero::getThirdTalent);
        slotPerComplexTalent.put(4, ComplexHero::getFourthTalent);
        slotPerComplexTalent.put(5, ComplexHero::getFifthTalent);

        // load map
        final FileConfiguration config = Main.getPlugin().getConfig();
        currentMap.set(GameMaps.byName(config.getString("current-map"), GameMaps.ARENA));

        // load mode
        currentMode.set(Modes.byName(config.getString("current-mode"), Modes.FFA));

        // init skin effect manager
        skinEffectManager = new SkinEffectManager(getPlugin());

        // start auto save timer
        this.autoSave = new AutoSync(Tick.fromMinute(10));
    }

    /**
     * Gets player's current profile or creates new if it doesn't exist yet.
     *
     * @param player - Player.
     */
    @Nonnull
    public PlayerProfile getProfile(Player player) {
        PlayerProfile profile = profiles.get(player);
        if (profile == null) {
            profile = new PlayerProfile(player);
            profiles.put(player, profile);
            profile.loadData();

            Main.getPlugin().getExperience().triggerUpdate(player);
        }
        return profile;
    }

    public void allProfiles(Consumer<PlayerProfile> consumer) {
        profiles.values().forEach(consumer);
    }

    public boolean hasProfile(Player player) {
        return profiles.containsKey(player);
    }

    @Nullable
    public GamePlayerUI getPlayerUI(Player player) {
        return getProfile(player).getPlayerUI();
    }

    public boolean isAbleToUse(Player player) {
        return isGameInProgress() || isTrialExistsAndIsOwner(player);
    }

    public boolean isGameInProgress() {
        return gameInstance != null && !gameInstance.isTimeIsUp();
    }

    /**
     * Returns the current GameInstance.
     *
     * @return GameInstance
     * @deprecated Use {@link #getCurrentGame()} to safely retrieve GameInstace.
     */
    @Nullable
    @Deprecated
    public GameInstance getGameInstance() throws RuntimeException {
        return gameInstance;
    }

    /**
     * @return game instance is present, else abstract version.
     */
    @Nonnull
    public IGameInstance getCurrentGame() {
        return gameInstance == null ? IGameInstance.NULL_GAME_INSTANCE : gameInstance;
    }

    public GameMaps getCurrentMap() {
        return currentMap.getElement();
    }

    public void setCurrentMap(GameMaps maps) {
        currentMap.set(maps);
        // save to config
        Main.getPlugin().getConfig().set("current-map", maps.name().toLowerCase(Locale.ROOT));
    }

    public boolean isDebug() {
        return isDebug;
    }

    public Trial getTrial() {
        return trial;
    }

    public boolean hasTrial() {
        return getTrial() != null;
    }

    public boolean isTrialExistsAndIsOwner(Player player) {
        return hasTrial() && getTrial().getPlayer() == player;
    }

    public void startTrial(Player player, Heroes heroes) {
        if (hasTrial()) {
            return;
        }

        trial = new Trial(getProfile(player), heroes);
        trial.onStart();
        trial.onPlayersReveal();
        trial.broadcastMessage("&a%s started a trial of %s.", player.getName(), heroes.getHero().getName());
    }

    public void stopTrial() {
        if (!hasTrial()) {
            return;
        }

        trial.broadcastMessage("&a%s has stopped trial challenge.", trial.getPlayer().getName());
        trial.onStop();
        trial = null;
    }

    public Modes getCurrentMode() {
        return currentMode.getElement();
    }

    public void setCurrentMode(Modes mode) {
        if (mode == getCurrentMode()) {
            return;
        }
        currentMode.set(mode);
        Chat.broadcast("&aChanged current game mode to %s.", mode.getMode().getName());

        // save to config
        Main.getPlugin().getConfig().set("current-mode", mode.name().toLowerCase(Locale.ROOT));
    }

    public void setCurrentMap(GameMaps maps, @Nullable Player player) {
        if (getCurrentMap() == maps && player != null) {
            PlayerLib.villagerNo(player, "&cAlready selected!");
            return;
        }

        setCurrentMap(maps);

        final String mapName = maps.getMap().getName();
        if (player == null) {
            Chat.broadcast("&aCurrent map is now &l%s&a.", mapName);
        }
        else {
            Chat.broadcast("&a%s selected &l%s &aas current map!", player.getName(), mapName);
        }
    }

    public void createNewGameInstance() {
        createNewGameInstance(false);
    }

    /**
     * Creates a new game instance.
     *
     * Only one game instance can be active at a time. (for now?)
     */
    public void createNewGameInstance(boolean debug) {
        // Pre game start checks
        final GameMaps currentMap = this.currentMap.getElement();

        if ((!currentMap.isPlayable() || !currentMap.getMap().hasLocation()) && !debug) {
            displayError("Invalid map!");
            return;
        }

        // Stop trial
        if (hasTrial()) {
            stopTrial();
        }

        isDebug = debug;

        final int playerRequirements = getCurrentMode().getMode().getPlayerRequirements();
        final Collection<Player> nonSpectatorPlayers = getNonSpectatorPlayers();

        // Check for minimum players
        // fixme -> Check for teams, not players
        if (nonSpectatorPlayers.size() < playerRequirements && !isDebug) {
            displayError("Not enough players! &l(%s/%s)", nonSpectatorPlayers.size(), playerRequirements);
            return;
        }

        // Check for team balance
        // todo -> Maybe add config support for unbalanced teams
        final List<GameTeam> populatedTeams = GameTeam.getPopulatedTeams();
        int teamPlayers = 0;
        for (GameTeam populatedTeam : populatedTeams) {
            final int size = populatedTeam.getPlayers().size();
            if (teamPlayers == 0) {
                teamPlayers = size;
                continue;
            }

            if (size != teamPlayers) {
                Chat.broadcast("&6&lUnbalanced Team! &e%s has more players than other teams.", populatedTeam.getName());
                //                displayError("Teams are not balanced! &l(%s)", populatedTeam.getName());
                //                return;
            }
        }

        // Stop parkour
        final ParkourManager parkourManager = EternaPlugin.getPlugin().getParkourManager();
        for (Player player : Bukkit.getOnlinePlayers()) {
            parkourManager.quitParkour(player);
        }

        // Create new instance and call onStart methods
        this.gameInstance = new GameInstance(getCurrentMode(), getCurrentMap());
        this.gameInstance.onStart();

        final boolean customSetup = this.gameInstance.getMode().onStart(this.gameInstance);

        if (!customSetup) {
            // Populate teams
            GameTeam.getPopulatedTeams().forEach(GameTeam::clearAndPopulateTeams);
        }

        for (final Heroes value : Heroes.values()) {
            Nulls.runIfNotNull(value.getHero(), Hero::onStart);
        }

        for (final Talents value : Talents.values()) {
            Nulls.runIfNotNull(value.getTalent(), Talent::onStart);
        }

        gameInstance.getMap().getMap().onStart();

        for (final GamePlayer gamePlayer : gameInstance.getPlayers().values()) {
            final Player player = gamePlayer.getPlayer();

            // Equip and hide players
            if (!gamePlayer.isSpectator()) {
                equipPlayer(player, gamePlayer.getHero());
                Utils.hidePlayer(player);
            }

            player.teleport(currentMap.getMap().getLocation());
        }

        if (!isDebug) {
            Chat.broadcast("&a&l➺ &aAll players have been hidden!");
            Chat.broadcast(
                    "&a&l➺ &aThey have &e%ss &ato spread before being revealed.",
                    BukkitUtils.roundTick(currentMap.getMap().getTimeBeforeReveal())
            );
        }
        else {
            this.gameInstance.setTimeLeft(10000000000L);
        }

        // On reveal
        GameTask.runLater(() -> {
            Chat.broadcast("&a&l➺ &aPlayers have been revealed. &lFIGHT!");
            gameInstance.setGameState(State.IN_GAME);

            // Call onPlayersReveal
            gameInstance.onPlayersReveal();

            for (final Heroes value : Heroes.values()) {
                Nulls.runIfNotNull(value.getHero(), Hero::onPlayersReveal);
            }

            for (final Talents value : Talents.values()) {
                Nulls.runIfNotNull(value.getTalent(), Talent::onPlayersReveal);
            }

            gameInstance.getMap().getMap().onPlayersReveal();

            if (debug) {
                Chat.broadcast("&4Running in debug mode!");
                Chat.broadcast("&cRunning in debug mode!");
                Chat.broadcast("&6Running in debug mode!");
            }

            gameInstance.getAlivePlayers().forEach(target -> {
                final Player player = target.getPlayer();
                final World world = player.getLocation().getWorld();

                Utils.showPlayer(player);
                Nulls.runIfNotNull(GameTeam.getPlayerTeam(player), GameTeam::glowTeammates);

                if (world != null && !debug) {
                    world.strikeLightningEffect(player.getLocation().add(0.0d, 2.0d, 0.0d));
                }
            });

            playAnimation();
        }, isDebug ? 1 : currentMap.getMap().getTimeBeforeReveal());

    }

    /**
     * Stops the current game instance.
     */
    public void stopCurrentGame() {
        if (this.gameInstance == null || this.gameInstance.getGameState() == State.POST_GAME) {
            return;
        }

        // Call mode onStop to clear player and assign winners
        final boolean response = gameInstance.getMode().onStop(this.gameInstance);

        if (!response) { // if returns false means mode will add their own winners
            gameInstance.getGameResult().supplyDefaultWinners();
        }

        final HeroStatsCollection heroStats = Main.getPlugin().getDatabases().getHeroStats();
        gameInstance.calculateEverything();

        // Reset player before clearing the instance
        this.gameInstance.getPlayers().values().forEach(player -> {
            final Heroes hero = player.getEnumHero();
            final StatContainer stats = player.getStats();

            Glowing.stopGlowing(player.getPlayer());
            player.updateScoreboard(true);
            player.resetPlayer();
            player.setValid(false);

            Utils.showPlayer(player.getPlayer());

            // Keep winner in survival, so it's clear for them that they have won
            final boolean isWinner = this.gameInstance.isWinner(player.getPlayer());
            if (!isWinner) {
                player.getPlayer().setGameMode(GameMode.SPECTATOR);
            }
            else {
                stats.markAsWinner();
            }

            // Reset game player
            player.getProfile().resetGamePlayer();

            // Save stats
            player.getDatabase().getStatistics().fromPlayerStatistic(hero, stats);
            heroStats.fromPlayerStatistic(hero, stats);
        });

        this.gameInstance.onStop();
        this.gameInstance.setGameState(State.POST_GAME);

        // Save stats
        heroStats.saveAsync();

        // Clear teams
        GameTeam.clearAllPlayers();

        // reset all cooldowns
        for (final Material value : Material.values()) {
            if (value.isItem()) {
                Bukkit.getOnlinePlayers().forEach(player -> player.setCooldown(value, 0));
            }
        }

        // call talents onStop and reset cooldowns
        for (final Talents value : Talents.values()) {
            Nulls.runIfNotNull(value.getTalent(), Talent::onStop);
        }

        // call heroes onStop
        for (final Heroes value : Heroes.values()) {
            Nulls.runIfNotNull(value.getHero(), hero -> {
                hero.onStop();
                hero.clearUsingUltimate();
            });
        }

        // call maps onStop
        currentMap.getElement().getMap().onStop();

        // stop all game tasks
        Main.getPlugin().getTaskList().onStop();

        // remove temp entities
        Entities.killSpawned();

        if (isDebug) {
            onStop();
            return;
        }

        // Spawn Fireworks
        gameInstance.executeWinCosmetic();
    }

    public void equipPlayer(Player player, Hero hero) {
        final PlayerInventory inventory = player.getInventory();
        inventory.setHeldItemSlot(0);
        player.setGameMode(GameMode.SURVIVAL);

        // Apply equipment
        hero.getEquipment().equip(player);
        hero.onStart(player);

        inventory.setItem(0, hero.getWeapon().getItem());
        giveTalentItem(player, hero, 1);
        giveTalentItem(player, hero, 2);

        if (hero instanceof ComplexHero) {
            giveTalentItem(player, hero, 3);
            giveTalentItem(player, hero, 4);
            giveTalentItem(player, hero, 5);
        }

        player.updateInventory();
    }

    public void equipPlayer(Player player) {
        equipPlayer(player, getCurrentHero(player));
    }

    public Talent getTalent(Hero hero, int slot) {
        if (slot >= 1 && slot < 3) {
            final ParamFunction<Talent, Hero> function = slotPerTalent.get(slot);
            return function == null ? null : function.execute(hero);
        }

        else if (hero instanceof ComplexHero complexHero) {
            final ParamFunction<Talent, ComplexHero> function = slotPerComplexTalent.get(slot);
            return function == null ? null : function.execute(complexHero);
        }
        return null;
    }

    /**
     * Called after the game stopped.
     */
    public void onStop() {
        // reset game state
        gameInstance.setGameState(State.FINISHED);
        gameInstance = null;

        // teleport players to spawn
        for (final Player player : Bukkit.getOnlinePlayers()) {
            player.setInvulnerable(false);
            player.setHealth(player.getMaxHealth());
            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(GameMaps.SPAWN.getMap().getLocation());

            LobbyItems.giveAll(player);
        }

        if (autoSave.scheduleSave) {
            autoSave.save();
        }
    }

    public void setSelectedHero(Player player, Heroes heroes) {
        setSelectedHero(player, heroes, false);
    }

    public void setSelectedHero(Player player, Heroes heroes, boolean force) {
        if (Manager.current().isGameInProgress()) {
            Chat.sendMessage(player, "&cUnable to change hero during the game!");
            PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
            return;
        }

        if (!heroes.isValidHero()) {
            if (!player.isOp()) {
                Chat.sendMessage(player, "&cThis hero is currently disabled. Sorry!");
                PlayerLib.villagerNo(player);
                return;
            }

            if (!force) {
                Chat.sendMessage(player, "&cNot selecting disabled hero without &e-IKnowItsDisabledHeroAndWillBreakTheGame&c argument!");
                PlayerLib.villagerNo(player);
                return;
            }

            Chat.sendMessage(player, "&cYou've selected &lDISABLED&c hero, which is either broken or not finished yet!");
            Chat.sendMessage(player, "&cIt &lwill&c throw errors and break the game!");
            Chat.sendMessage(player, "&4&lYOU HAVE BEEN WARNED");
        }

        if (getSelectedLobbyHero(player) == heroes) {
            Chat.sendMessage(player, "&cAlready selected!");
            PlayerLib.villagerNo(player);
            return;
        }

        getProfile(player).setSelectedHero(heroes);
        player.closeInventory();
        PlayerLib.villagerYes(player);
        Chat.sendMessage(player, "&aSelected %s!", heroes.getHero().getName());

        if (Setting.RANDOM_HERO.isEnabled(player)) {
            Chat.sendMessage(player, "");
            Chat.sendMessage(player, "&aKeep in mind &l%s &ais enabled! Use &e/setting", Setting.RANDOM_HERO.getName());
            Chat.sendMessage(player, "&aturn the feature off and play as %s!", heroes.getHero().getName());
            Chat.sendMessage(player, "");
        }

        // save to database
        getProfile(player).getDatabase().getHeroEntry().setSelectedHero(heroes);
    }

    /**
     * @return actual hero player is using right now, trial, lobby or game.
     */
    @Nonnull
    public Hero getCurrentHero(Player player) {
        return getCurrentEnumHero(player).getHero();
    }

    @Nonnull
    public Heroes getCurrentEnumHero(Player player) {
        if (isTrialExistsAndIsOwner(player)) {
            return getTrial().getHeroes();
        }

        else if (isPlayerInGame(player)) {
            final GamePlayer gamePlayer = getCurrentGame().getPlayer(player);
            if (gamePlayer == null) {
                return Heroes.ARCHER;
            }
            return gamePlayer.getEnumHero();
        }

        return getSelectedLobbyHero(player);
    }

    /**
     * @deprecated Use {@link #getCurrentHero(Player)} to get player current hero. This will always return lobby hero.
     */
    @Deprecated
    public Heroes getSelectedLobbyHero(Player player) {
        return getProfile(player).getSelectedHero();
    }

    public boolean isPlayerInGame(Player player) {
        return gameInstance != null && gameInstance.getPlayer(player) != null && GamePlayer.getPlayer(player).isAlive();
    }

    public void removeProfile(Player player) {
        profiles.remove(player);
    }

    public void createProfile(Player player) {
        getProfile(player);
    }

    public boolean anyProfiles() {
        return profiles.size() > 0;
    }

    private void playAnimation() {
        new TitleAnimation();
    }

    private void displayError(String message, Object... objects) {
        Chat.broadcast("&c&lUnable to start the game! &c" + message.formatted(objects));
    }

    private Collection<Player> getNonSpectatorPlayers() {
        return Bukkit.getOnlinePlayers().stream().filter(player -> !Setting.SPECTATE.isEnabled(player))
                .collect(Collectors.toSet());
    }

    private void giveTalentItem(Player player, Hero hero, int slot) {
        final PlayerInventory inventory = player.getInventory();
        final Talent talent = getTalent(hero, slot);
        final ItemStack talentItem = talent == null || talent.getItem() == null ? new ItemStack(Material.AIR) : talent.getItem();

        if (talent != null && !talent.isAutoAdd()) {
            return;
        }

        inventory.setItem(slot, talentItem);
        fixTalentItemAmount(player, slot, talent);
    }

    private void fixTalentItemAmount(Player player, int slot, Talent talent) {
        if (!(talent instanceof ChargedTalent chargedTalent)) {
            return;
        }

        final PlayerInventory inventory = player.getInventory();
        final ItemStack item = inventory.getItem(slot);

        if (item == null) {
            return;
        }

        item.setAmount(chargedTalent.getMaxCharges());
    }

    public static Manager current() {
        return Main.getPlugin().getManager();
    }
}
