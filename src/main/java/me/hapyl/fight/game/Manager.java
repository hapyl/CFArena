package me.hapyl.fight.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.cosmetic.skin.SkinEffectManager;
import me.hapyl.fight.game.entity.ConsumerFunction;
import me.hapyl.fight.game.entity.EntityData;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.gamemode.Modes;
import me.hapyl.fight.game.heroes.ComplexHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.lobby.LobbyItems;
import me.hapyl.fight.game.lobby.StartCountdown;
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
import me.hapyl.fight.game.weapons.RangeWeapon;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.EternaPlugin;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.parkour.ParkourManager;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import me.hapyl.spigotutils.module.util.DependencyInjector;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class Manager extends DependencyInjector<Main> {

    private final Map<UUID, GameEntity> entities;
    private final Map<UUID, PlayerProfile> profiles;
    private final SkinEffectManager skinEffectManager;
    private final AutoSync autoSave;
    private final Trial trial;

    @Nonnull private GameMaps currentMap;
    @Nonnull private Modes currentMode;
    private boolean isDebug = true;

    private StartCountdown startCountdown;
    private GameInstance gameInstance; // @implNote: For now, only one game instance can be active at a time.

    public Manager(Main main) {
        super(main);

        entities = Maps.newConcurrentMap();
        profiles = Maps.newConcurrentMap();

        // load config data
        currentMap = Main.getPlugin().getConfigEnumValue("current-map", GameMaps.class, GameMaps.ARENA);
        currentMode = Main.getPlugin().getConfigEnumValue("current-mode", Modes.class, Modes.FFA);

        // init skin effect manager
        skinEffectManager = new SkinEffectManager(getPlugin());

        // start auto save timer
        autoSave = new AutoSync(Tick.fromMinute(10));

        trial = new Trial(main);
    }

    public void createStartCountdown() {
        if (!canStartGame(false)) {
            return;
        }

        if (startCountdown != null) {
            startCountdown.cancel();
        }

        startCountdown = new StartCountdown() {
            @Override
            public void onCountdownFinish() {
                startCountdown = null;
                createNewGameInstance();
            }
        };
    }

    @Nullable
    public StartCountdown getStartCountdown() {
        return startCountdown;
    }

    /**
     * Gets player's current profile or creates new if it doesn't exist yet.
     *
     * @param player - Player.
     * @throws IllegalArgumentException if player is null or offline.
     */
    @Nonnull
    public PlayerProfile getOrCreateProfile(Player player) {
        if (player == null || !player.isOnline()) {
            throw new IllegalArgumentException("player must be online");
        }

        PlayerProfile profile = profiles.get(player.getUniqueId());

        if (profile == null) {
            profile = createProfile(player);
        }

        return profile;
    }

    @Nullable
    public PlayerProfile getProfile(Player player) {
        return profiles.get(player.getUniqueId());
    }

    @Nonnull
    public PlayerProfile createProfile(Player player) {
        final PlayerProfile profile = new PlayerProfile(player);
        profiles.put(player.getUniqueId(), profile);

        profile.loadData();
        getPlugin().getExperience().triggerUpdate(player);
        Main.getPlugin().getCrateManager().createHologram(player);
        return profile;
    }

    public GameEntity createEntity(@Nonnull LivingEntity entity) {
        return createEntity(entity, e -> new GameEntity(entity));
    }

    public <T extends GameEntity> T createEntity(@Nonnull LivingEntity entity, ConsumerFunction<LivingEntity, T> consumer) {
        final UUID uuid = entity.getUniqueId();
        final T gameEntity = consumer.apply(entity);
        consumer.andThen(gameEntity);

        final GameEntity oldEntity = entities.put(uuid, gameEntity);

        if (oldEntity != null) {
            oldEntity.remove();
        }

        entities.put(uuid, gameEntity);
        return gameEntity;
    }

    @Nullable
    public <T extends GameEntity> T getEntity(@Nonnull UUID uuid, @Nonnull Class<T> clazz) {
        final GameEntity entity = entities.get(uuid);

        if (!clazz.isInstance(entity)) {
            return null;
        }

        return clazz.cast(entity);
    }

    @Nullable
    public GameEntity getEntity(UUID uuid) {
        return getEntity(uuid, GameEntity.class);
    }

    public GamePlayer getOrCreatePlayer(Player player) {
        final UUID uuid = player.getUniqueId();
        final GamePlayer entity = getEntity(uuid, GamePlayer.class);

        if (entity != null) {
            return entity;
        }

        // FIXME (hapyl): 031, Jul 31: Might be some issues with scoreboard!

        final PlayerProfile profile = PlayerProfile.getProfile(player);

        if (profile == null) {
            throw new IllegalArgumentException("Cannot create game player for offline player!");
        }

        final GamePlayer gamePlayer = profile.createGamePlayer();
        gamePlayer.updateScoreboardTeams(false);

        entities.put(uuid, gamePlayer);
        return gamePlayer;
    }

    @Nonnull
    public GameEntity getOrCreateEntity(LivingEntity entity) {
        final UUID uuid = entity.getUniqueId();
        final GameEntity existing = getEntity(uuid);

        if (existing != null) {
            return existing;
        }

        final GameEntity newInstance = new GameEntity(entity);
        final GameEntity oldInstance = entities.put(uuid, newInstance);

        if (oldInstance != null) {
            oldInstance.remove();
        }

        return newInstance;
    }

    public void allProfiles(Consumer<PlayerProfile> consumer) {
        profiles.values().forEach(consumer);
    }

    public boolean hasProfile(Player player) {
        return profiles.containsKey(player.getUniqueId());
    }

    @Nullable
    public GamePlayerUI getPlayerUI(Player player) {
        return getOrCreateProfile(player).getPlayerUI();
    }

    public boolean isAbleToUse(Player player) {
        return isGameInProgress() || trial.isInTrial(player);
    }

    public boolean isGameInProgress() {
        return gameInstance != null && !gameInstance.isTimeIsUp();
    }

    public void handlePlayer(Player player) {
        createProfile(player);

        // teleport either to spawn or the map if there is a game in progress
        final IGameInstance game = getCurrentGame();
        if (!game.isReal()) {
            final GameMode gameMode = player.getGameMode();

            if (gameMode != GameMode.CREATIVE && gameMode != GameMode.SPECTATOR) {
                player.teleport(GameMaps.SPAWN.getMap().getLocation());
                LobbyItems.giveAll(player);
            }
        }
        else {
            player.teleport(game.getMap().getMap().getLocation());
        }

        // Notify operators
        if (player.isOp()) {
            Chat.sendMessage(player, getPlugin().database.getDatabaseString());
        }
    }

    /**
     * Returns the current GameInstance.
     *
     * @return GameInstance
     * @deprecated Use {@link #getCurrentGame()} to safely retrieve GameInstance.
     */
    @Nullable
    @Deprecated
    public GameInstance getGameInstance() throws RuntimeException {
        return gameInstance;
    }

    /**
     * @return game instance if present, else an abstract version.
     */
    @Nonnull
    public IGameInstance getCurrentGame() {
        return gameInstance == null ? IGameInstance.NULL_GAME_INSTANCE : gameInstance;
    }

    public GameMaps getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(@Nonnull GameMaps maps) {
        currentMap = maps;

        // save to config
        Main.getPlugin().setConfigValue("current-map", maps.name().toLowerCase(Locale.ROOT));
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

    public Modes getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(@Nonnull Modes mode) {
        if (mode == getCurrentMode()) {
            return;
        }

        currentMode = mode;
        Chat.broadcast("&aChanged current game mode to %s.", mode.getMode().getName());

        // save to config
        Main.getPlugin().setConfigValue("current-mode", mode.name().toLowerCase(Locale.ROOT));
    }

    public void setCurrentMap(@Nonnull GameMaps maps, @Nullable Player player) {
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

    public boolean canStartGame(boolean debug) {
        // Pre-game start checks
        if ((!currentMap.isPlayable() || !currentMap.getMap().hasLocation()) && !debug) {
            displayError("Invalid map!");
            return false;
        }

        final int playerRequirements = getCurrentMode().getMode().getPlayerRequirements();
        final Collection<Player> nonSpectatorPlayers = getNonSpectatorPlayers();

        // Check for minimum players
        // fixme -> Check for teams, not players
        if (nonSpectatorPlayers.size() < playerRequirements && !isDebug) {
            displayError("Not enough players! &l(%s/%s)", nonSpectatorPlayers.size(), playerRequirements);
            return false;
        }

        return true;
    }

    /**
     * Creates a new game instance.
     * <p>
     * Only one game instance can be active at a time.
     */
    public void createNewGameInstance(boolean debug) {
        if (!canStartGame(debug)) {
            return;
        }

        isDebug = debug;

        // Check for team balance
        // todo -> Maybe add config support for unbalanced teams
        GameTeam.removeOfflinePlayers(); // Make sure to remove offline players
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

        this.gameInstance.getMode().onStart(this.gameInstance);

        for (final Heroes value : Heroes.values()) {
            Nulls.runIfNotNull(value.getHero(), Hero::onStart);
        }

        for (final Talents value : Talents.values()) {
            Nulls.runIfNotNull(value.getTalent(), Talent::onStart);
        }

        gameInstance.getMap().getMap().onStart();

        for (final GamePlayer gamePlayer : CF.getPlayers()) {
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

            CF.getAlivePlayers().forEach(target -> {
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

        if (!response) { // if returns false means mode adds their own winners
            gameInstance.getGameResult().supplyDefaultWinners();
        }

        gameInstance.calculateEverything();

        // remove game entities
        entities.values().forEach(gameEntity -> gameEntity.onStop(this.gameInstance));
        entities.clear();

        this.gameInstance.onStop();
        this.gameInstance.setGameState(State.POST_GAME);

        EntityData.resetDamageData(); // clear damage handler

        // Save stats
        // this.gameInstance.getActiveHeroes().forEach(hero -> hero.getStats().saveAsync());

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

                if (hero.getWeapon() instanceof RangeWeapon rangeWeapon) {
                    rangeWeapon.onStop();
                }
            });
        }

        // call maps onStop
        currentMap.getMap().onStop();

        // stop all game tasks
        Main.getPlugin().getTaskList().onStop();

        // clean-up teams
        for (GameTeam value : GameTeam.values()) {
            value.onStop();
        }

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

            // Progress achievement
            Achievements.PLAY_FIRST_GAME.complete(player);

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
            Chat.sendMessage(player, "&cUnable to change a hero during the game!");
            PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
            return;
        }

        if (heroes.isLocked(player) && !force) {
            Chat.sendMessage(player, "&cThis hero is locked!");
            return;
        }

        if (!heroes.isValidHero()) {
            if (!player.isOp()) {
                Chat.sendMessage(player, "&cThis hero is currently disabled. Sorry!");
                PlayerLib.villagerNo(player);
                return;
            }

            if (!force) {
                Chat.sendMessage(player, "&cNot selecting a disabled hero without &e-IKnowItsDisabledHeroAndWillBreakTheGame&c argument!");
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

        getOrCreateProfile(player).setSelectedHero(heroes);
        player.closeInventory();
        PlayerLib.villagerYes(player);
        Chat.sendMessage(player, "&aSelected %s!", heroes.getHero().getName());

        if (Setting.RANDOM_HERO.isEnabled(player)) {
            Chat.sendMessage(player, "");
            Chat.sendMessage(player, "&aKeep in mind &l%s &ais enabled! Use &e/setting", Setting.RANDOM_HERO.getName());
            Chat.sendMessage(player, "&aturn the feature off and play as %s!", heroes.getHero().getName());
            Chat.sendMessage(player, "");
        }

        // save to the database
        getOrCreateProfile(player).getDatabase().getHeroEntry().setSelectedHero(heroes);
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
        if (isPlayerInGame(player)) {
            final GamePlayer gamePlayer = CF.getPlayer(player);
            if (gamePlayer == null) {
                return Heroes.ARCHER;
            }
            return gamePlayer.getEnumHero();
        }

        return getSelectedLobbyHero(player);
    }

    public boolean isPlayerInGame(Player player) {
        final GamePlayer gamePlayer = CF.getPlayer(player);
        return gameInstance != null && gamePlayer != null && gamePlayer.isAlive();
    }

    public void removeProfile(Player player) {
        final PlayerProfile profile = profiles.remove(player.getUniqueId());

        if (profile == null) {
            return;
        }

        Main.getPlugin().getCrateManager().removeHologram(player);
    }

    public boolean anyProfiles() {
        return profiles.size() > 0;
    }

    public void listProfiles() {
        final Logger logger = getPlugin().getLogger();

        logger.info("Listing all profiles:");
        logger.info(profiles.values().stream().map(PlayerProfile::toString).collect(Collectors.joining("\n")));
    }

    public GamePlayer getPlayer(@Nonnull Player player) {
        return getEntity(player.getUniqueId(), GamePlayer.class);
    }

    @Nonnull
    public Set<GamePlayer> getPlayers() {
        final Set<GamePlayer> players = Sets.newHashSet();
        for (GameEntity entity : entities.values()) {
            if (entity instanceof GamePlayer player) {
                players.add(player);
            }
        }

        return players;
    }

    @Nonnull
    public List<GamePlayer> getAlivePlayers() {
        return getAlivePlayers(player -> true);
    }

    @Nonnull
    public Collection<GamePlayer> getAllPlayers() {
        return getPlayers();
    }

    @Nonnull
    public List<GamePlayer> getAlivePlayers(Predicate<GamePlayer> predicate) {
        final Set<GamePlayer> players = getPlayers();
        players.removeIf(player -> !player.isAlive() || !predicate.test(player));

        return Lists.newArrayList(players);
    }

    @Nonnull
    public List<GamePlayer> getAlivePlayers(Heroes enumHero) {
        return getAlivePlayers(player -> player.getEnumHero() == enumHero);
    }

    @Nonnull
    public Set<Heroes> getActiveHeroes() {
        final Set<Heroes> heroes = Sets.newHashSet();
        getPlayers().forEach(player -> heroes.add(player.getEnumHero()));

        return heroes;
    }

    public void removeEntity(LivingEntity entity) {
        final GameEntity gameEntity = entities.remove(entity.getUniqueId());

        if (gameEntity == null) {
            return;
        }

        gameEntity.remove();
    }

    public Set<GameEntity> getEntities() {
        return Sets.newHashSet(entities.values());
    }

    public Set<GameEntity> getEntitiesExcludePlayers() {
        final Set<GameEntity> entities = getEntities();
        entities.removeIf(entity -> entities instanceof GamePlayer);

        return entities;
    }

    private Heroes getSelectedLobbyHero(Player player) {
        final PlayerProfile profile = getProfile(player);
        if (profile == null) {
            return Heroes.ARCHER;
        }

        return profile.getSelectedHero();
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
        final Talent talent = hero.getTalent(slot);
        final ItemStack talentItem = talent == null ? new ItemStack(Material.AIR) : talent.getItem();

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

    /**
     * Returns current and only manager.
     *
     * @return the manager.
     */
    @Nonnull
    public static Manager current() {
        return Main.getPlugin().getManager();
    }
}
