package me.hapyl.fight.game;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.eterna.Eterna;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.parkour.ParkourRegistry;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.player.PlayerSkin;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.Runnables;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.command.RateHeroCommand;
import me.hapyl.fight.database.Award;
import me.hapyl.fight.database.entry.RandomHeroEntry;
import me.hapyl.fight.game.challenge.ChallengeType;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.competetive.Tournament;
import me.hapyl.fight.game.cosmetic.skin.SkinEffectManager;
import me.hapyl.fight.game.element.ElementCaller;
import me.hapyl.fight.game.entity.*;
import me.hapyl.fight.game.event.ServerEvents;
import me.hapyl.fight.game.type.EnumGameType;
import me.hapyl.fight.game.heroes.ArchetypeList;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.lobby.LobbyItems;
import me.hapyl.fight.game.lobby.StartCountdown;
import me.hapyl.fight.game.maps.EnumLevel;
import me.hapyl.fight.game.maps.Level;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.profile.data.AchievementData;
import me.hapyl.fight.game.profile.data.PlayerProfileData;
import me.hapyl.fight.game.profile.data.Type;
import me.hapyl.fight.game.setting.Settings;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.ui.PlayerUI;
import me.hapyl.fight.garbage.CFGarbageCollector;
import me.hapyl.fight.guesswho.GuessWho;
import me.hapyl.fight.npc.PersistentNPCs;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.collection.CacheSet;
import me.hapyl.fight.Notifier;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class Manager extends BukkitRunnable {

    public final Set<UUID> ignoredEntities;
    public final CacheSet<Player> goldenGg = new CacheSet<>(15_000);

    private final Main main;
    private final Set<EntityType> ignoredTypes = Sets.newHashSet(
            EntityType.ARMOR_STAND,
            EntityType.MARKER,
            EntityType.TEXT_DISPLAY,
            EntityType.BLOCK_DISPLAY,
            EntityType.ITEM_DISPLAY,
            EntityType.GIANT
    );

    private final Map<UUID, GameEntity> entities; // This might need optimizing if A LOT of entities since getting players is iterating over the whole map
    private final Map<UUID, PlayerProfile> profiles;
    private final Map<Attribute, Double> defaultAttributeValues;
    private final SkinEffectManager skinEffectManager;
    private final AutoSync autoSave;

    @Nonnull private EnumLevel currentMap;
    @Nonnull private EnumGameType currentMode;

    private StartCountdown startCountdown;
    private GameInstance gameInstance; // @implNote: For now, only one game instance can be active at a time.
    private DebugData debugData;
    private Tournament competitive;
    private GuessWho guessWhoGame;

    private FairMode fairMode;

    public Manager(Main main) {
        this.main = main;

        ignoredEntities = Sets.newHashSet();
        entities = Maps.newConcurrentMap();
        profiles = Maps.newConcurrentMap();
        defaultAttributeValues = Map.of(
                Attribute.GENERIC_MAX_HEALTH, 20.0d,
                Attribute.GENERIC_KNOCKBACK_RESISTANCE, 0.0d,
                Attribute.GENERIC_ATTACK_SPEED, 1_000d, // Keep attack speed 1,000 because I said so
                Attribute.GENERIC_SCALE, 1.0d,
                Attribute.GENERIC_STEP_HEIGHT, 0.6d,
                Attribute.GENERIC_GRAVITY, 0.08d
        );

        // load config data
        currentMap = Main.getPlugin().getConfigEnumValue("current-map", EnumLevel.class, EnumLevel.ARENA);
        currentMode = Main.getPlugin().getConfigEnumValue("current-mode", EnumGameType.class, EnumGameType.FFA);

        // init skin effect manager
        skinEffectManager = new SkinEffectManager(main);

        // start auto save timer
        autoSave = new AutoSync(Tick.fromMinute(10));

        debugData = DebugData.EMPTY;

        fairMode = FairMode.UNFAIR;

        runTaskTimer(main, 0, 1);
    }

    @Nullable
    public GuessWho getGuessWhoGame() {
        return guessWhoGame;
    }

    public boolean isGuessWhoGameInProgress() {
        return guessWhoGame != null;
    }

    public void stopGuessWhoGame() {
        if (guessWhoGame == null) {
            return;
        }

        guessWhoGame.onStop();
        guessWhoGame = null;
    }

    public boolean createNewGuessWhoGame(@Nonnull Player player1, @Nonnull Player player2) {
        if (!player1.isOnline()) {
            displayError("Player 1 is not online!");
            return false;
        }

        if (!player2.isOnline()) {
            displayError("Player 2 is not online!");
            return false;
        }

        guessWhoGame = new GuessWho(player1, player2);
        guessWhoGame.onStart();
        return true;
    }

    @Override
    public void run() {
        // Tick entities
        final Collection<GameEntity> entities = Manager.this.entities.values();
        entities.removeIf(entity -> {
            if (entity instanceof GamePlayer) {
                return false;
            }

            return entity.getEntity().isDead();
        });

        // Tick players
        entities.forEach(entity -> {
            // There was a Ticking instance check before,
            // but I really think that entities other than players should be ticked separately.
            if (entity instanceof Ticking ticking) {
                ticking.tick();
            }
        });
    }

    public void createStartCountdown() {
        createStartCountdown(DebugData.EMPTY);
    }

    public void createStartCountdown(DebugData debug) {
        if (!canStartGame(debug)) {
            return;
        }

        if (startCountdown != null) {
            startCountdown.cancel();
        }

        startCountdown = new StartCountdown() {
            @Override
            public void onTaskStop() {
                startCountdown = null;
            }

            @Override
            public void onCountdownFinish() {
                startCountdown = null;
                createNewGameInstance();
            }
        };
    }

    public void stopStartCountdown(@Nonnull Player player) {
        if (startCountdown != null) {
            startCountdown.cancel();
            startCountdown = null;
        }

        final PlayerProfileData playerData = getOrCreateProfile(player).getPlayerData();
        final AchievementData data = playerData.getAchievementData(Registries.getAchievements().I_DONT_WANT_TO_PLAY);

        final int useTime = data.checkExpire(10000).increment(Type.USE_TIME, 1);

        if (useTime >= 10) {
            data.completeAchievement();
        }
    }

    public void addIgnored(LivingEntity entity) {
        ignoredEntities.add(entity.getUniqueId());
    }

    public void removeIgnored(LivingEntity entity) {
        ignoredEntities.remove(entity.getUniqueId());
    }

    public boolean isIgnored(@Nonnull LivingEntity entity) {
        return ignoredTypes.contains(entity.getType()) || ignoredEntities.contains(entity.getUniqueId());
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

        main.getExperience().triggerUpdate(player);
        main.getCrateManager().createHologram(player);

        for (PersistentNPCs enumNpc : PersistentNPCs.values()) {
            enumNpc.getNpc().onCreate(player);
        }

        // Call server events
        ServerEvents.getActiveEvents().forEach(event -> event.onJoin(profile));

        return profile;
    }

    /**
     * Creates an entity based on the living entity type.
     * <p>
     * For some entities such as arrows, there is no need for
     * damage and entity data to be present, and only the base is needed.
     *
     * @param entity - Entity.
     */
    @Nonnull
    public GameEntity createEntity(@Nonnull LivingEntity entity) {
        final EntityType type = entity.getType();

        return switch (type) {
            // Why are ARMOR_STAND here?
            // That literally deletes the armor stands spawned during the lobby.
            case ARROW, SPECTRAL_ARROW -> registerEntity(new GameEntity(entity));
            default -> registerEntity(new LivingGameEntity(entity));
        };
    }

    /**
     * Creates a {@link GameEntity} handle for a {@link LivingEntity}.
     *
     * @param location - Location, where to spawn the entity.
     * @param type     - Entity type.
     * @param consumer - Consumer.
     * @return a {@link GameEntity} handle.
     * @throws IllegalArgumentException if an entity with that {@link UUID} already exists.
     */
    @Nonnull
    public <E extends LivingEntity, T extends GameEntity> T createEntity(@Nonnull Location location, @Nonnull Entities<E> type, @Nonnull ConsumerFunction<E, T> consumer) {
        final AtomicReference<T> reference = new AtomicReference<>();

        type.spawn(location, self -> {
            // Add ignored entity so the manager doesn't create a handle for it.
            ignoredEntities.add(self.getUniqueId());

            // Create a game entity via the reference, because lambdas are cool.
            reference.set(consumer.apply(self));
        });

        final T gameEntity = reference.get();

        return registerEntity(gameEntity, consumer::andThen);
    }

    /**
     * Registers the given {@link GameEntity} to the manager.
     *
     * @param entity   - Entity to register.
     * @param consumer - Consumer.
     * @return the same {@link GameEntity}.
     * @throws IllegalArgumentException if an entity with that {@link UUID} already exists.
     */
    @Nonnull
    public <E extends LivingEntity, T extends GameEntity> T registerEntity(@Nonnull T entity, @Nullable Consumer<T> consumer) {
        final UUID uuid = entity.getUUID();

        if (entities.containsKey(uuid)) {
            entity.forceRemove();
            throw new IllegalArgumentException("duplicate entity creation");
        }

        if (consumer != null) {
            consumer.accept(entity);
        }

        entities.put(uuid, entity);
        ignoredEntities.remove(uuid);

        return entity;
    }

    @Nonnull
    public <T extends GameEntity> T registerEntity(@Nonnull T entity) {
        return registerEntity(entity, null);
    }

    @Nonnull
    public GamePlayer registerGamePlayer(GamePlayer gamePlayer) {
        entities.put(gamePlayer.getUUID(), gamePlayer);
        return gamePlayer;
    }

    @Nullable
    public <T extends GameEntity> T getEntity(@Nonnull UUID uuid, @Nonnull Class<T> clazz) {
        final GameEntity entity = entities.get(uuid);

        if (entity == null) {
            return null;
        }

        if (!clazz.isInstance(entity)) {
            return null;
        }

        return clazz.cast(entity);
    }

    @Nullable
    public LivingGameEntity getEntity(UUID uuid) {
        return getEntity(uuid, LivingGameEntity.class);
    }

    public void allProfiles(Consumer<PlayerProfile> consumer) {
        profiles.values().forEach(consumer);
    }

    public boolean hasProfile(Player player) {
        return profiles.containsKey(player.getUniqueId());
    }

    @Nullable
    public PlayerUI getPlayerUI(Player player) {
        return getOrCreateProfile(player).getPlayerUI();
    }

    /**
     * Returns if player is able to use an ability.
     * Checks for the game to exist and being in progress.
     *
     * @param profile - Player to check.
     * @return true if a player is able to use an ability; false otherwise.
     */
    public boolean isAbleToUse(PlayerProfile profile) {
        return isGameInProgress() || profile.hasTrial();
    }

    public boolean isGameInProgress() {
        return gameInstance != null && gameInstance.getGameState() == State.IN_GAME;
    }

    @Nonnull
    public PlayerProfile handlePlayer(Player player) {
        final PlayerProfile profile = createProfile(player);

        // teleport either to spawn or the map if there is a game in progress
        final GameInstance gameInstance = getGameInstance();

        if (gameInstance == null) {
            final GameMode gameMode = player.getGameMode();

            if (gameMode != GameMode.CREATIVE && gameMode != GameMode.SPECTATOR) {
                player.teleport(EnumLevel.SPAWN.getLevel().getLocation());
                LobbyItems.giveAll(player);
            }
        }
        else {
            player.teleport(gameInstance.getEnumMap().getLevel().getLocation());
        }

        // Notify operators
        if (player.isOp()) {
            Chat.sendMessage(player, main.getDatabase().getDatabaseString());
        }

        return profile;
    }

    /**
     * Returns the current GameInstance.
     *
     * @return GameInstance
     * @implNote Use {@link #getCurrentGame()} to safely retrieve GameInstance.
     */
    @Nullable
    //@Deprecated
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

    @Nonnull
    public EnumLevel getCurrentMap() {
        return currentMap;
    }

    @Nonnull
    public Level currentLevel() {
        return currentMap.getLevel();
    }

    public void setCurrentMap(@Nonnull EnumLevel maps) {
        currentMap = maps;

        // save to config
        Main.getPlugin().setConfigValue("current-map", maps.name().toLowerCase(Locale.ROOT));
    }

    public boolean isDebug() {
        return debugData.is(DebugData.Flag.DEBUG);
    }

    @Nonnull
    public DebugData getDebug() {
        return debugData;
    }

    public EnumGameType getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(@Nonnull EnumGameType mode) {
        if (mode == getCurrentMode()) {
            return;
        }

        currentMode = mode;
        Chat.broadcast("&aChanged current game mode to %s.".formatted(mode.getMode().getName()));

        // save to config
        Main.getPlugin().setConfigValue("current-mode", mode.name().toLowerCase(Locale.ROOT));
    }

    public void createNewGameInstance() {
        createNewGameInstance(DebugData.EMPTY);
    }

    public boolean canStartGame(DebugData debug) {
        // Pre-game start checks
        if ((!currentMap.isPlayable() || !currentMap.getLevel().hasLocation()) && !debug.is(DebugData.Flag.DEBUG)) {
            displayError("Invalid map!");
            return false;
        }

        // Check if a player is in a trial
        for (PlayerProfile profile : profiles.values()) {
            if (profile.hasTrial()) {
                displayError(profile.getPlayer().getName() + " is in a Trial!");
                return false;
            }
        }

        final int playerRequirements = getCurrentMode().getMode().getPlayerRequirements();
        final Collection<Player> nonSpectatorPlayers = getNonSpectatorPlayers();

        // Check for minimum players if not in debug
        if (!debug.is(DebugData.Flag.DEBUG) && debug.not(DebugData.Flag.FORCE)) {
            final List<GameTeam> teams = GameTeam.getPopulatedTeams();

            // Hardcoded minimum 2 teams
            if (teams.size() < 2) {
                displayError("Not enough teams! &l(%s/2)", teams.size());
                return false;
            }

            if (nonSpectatorPlayers.size() < playerRequirements) {
                displayError("Not enough players! &l(%s/%s)", nonSpectatorPlayers.size(), playerRequirements);
                return false;
            }
        }

        return true;
    }

    /**
     * Creates a new game instance.
     * <p>
     * Only one game instance can be active at a time.
     */
    public void createNewGameInstance(DebugData debug) {
        if (!canStartGame(debug)) {
            return;
        }

        this.debugData = debug;

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
                Chat.broadcast("&6&lUnbalanced Team! &e%s has more players than other teams.".formatted(populatedTeam.getName()));
            }
        }

        // Stop GuessWho
        stopGuessWhoGame();

        // Stop parkour
        final ParkourRegistry parkourRegistry = Eterna.getRegistry().parkourRegistry;

        for (Player player : Bukkit.getOnlinePlayers()) {
            parkourRegistry.quitParkour(player);
        }

        // Create new instance and call onStart methods
        this.gameInstance = new GameInstance(getCurrentMode(), getCurrentMap());
        this.gameInstance.onStart();

        // Init skin manager
        this.skinEffectManager.onStart();

        for (final GamePlayer gamePlayer : CF.getPlayers()) {
            final Player player = gamePlayer.getPlayer();

            // Equip and hide players
            if (!gamePlayer.isSpectator()) {
                gamePlayer.prepare(gamePlayer.getHero());
                gamePlayer.hidePlayer();

                // Apply player skin if exists
                final PlayerSkin skin = gamePlayer.getHero().getSkin();

                if (Settings.USE_SKINS_INSTEAD_OF_ARMOR.isEnabled(player) && skin != null) {
                    skin.apply(player);
                }
            }

            // Teleport to the map
            player.teleport(currentMap.getLevel().getLocation());

            // Glow teammates right after teleport
            gamePlayer.getTeam().glowTeammates();
        }

        if (!debug.is(DebugData.Flag.DEBUG)) {
            Chat.broadcast("&a&l➺ &aAll players have been hidden!");
            Chat.broadcast("&a&l➺ &aThey have &e%ss &ato spread before being revealed.".formatted(
                    BukkitUtils.roundTick(currentMap.getLevel().getTimeBeforeReveal())));
        }
        else {
            this.gameInstance.setTimeLeft(10000000000L);
        }

        // On reveal
        GameTask.runLater(() -> {
            Chat.broadcast("&a&l➺ &aPlayers have been revealed. &lFIGHT!");
            gameInstance.setGameState(State.IN_GAME);

            ElementCaller.CALLER.onPlayersRevealed(gameInstance);

            if (debug.any()) {
                Chat.broadcast("&c&lDEBUG &fRunning in debug instance.");
                Chat.broadcast("&c&lDEBUG &fDebugging: " + debug.list());
            }

            CF.getAlivePlayers().forEach(player -> {
                final World world = player.getWorld();

                player.showPlayer();

                if (!debug.is(DebugData.Flag.DEBUG)) {
                    world.strikeLightningEffect(player.getLocation().add(0, 2, 0));
                }
            });

            playAnimation();
        }, debug.or(DebugData.Flag.DEBUG) ? 1 : currentMap.getLevel().getTimeBeforeReveal());

    }

    /**
     * Stops the current game instance.
     */
    public void stopCurrentGame() {
        if (this.gameInstance == null || this.gameInstance.getGameState() != State.IN_GAME) {
            return;
        }

        // Call mode onStop to clear player and assign winners
        final boolean response = gameInstance.getMode().onStop(this.gameInstance);

        if (!response) { // if returns false means mode adds their own winners
            gameInstance.getGameResult().supplyDefaultWinners();
        }

        gameInstance.calculateEverything();

        gameInstance.onStop();
        gameInstance.setGameState(State.POST_GAME);

        EntityData.resetDamageData(); // clear damage handler

        // Save stats
        // this.gameInstance.getActiveHeroes().forEach(hero -> hero.getStats().saveAsync());

        // stop all game tasks
        Main.getPlugin().getTaskList().onStop();

        // clean-up teams
        for (GameTeam team : GameTeam.values()) {
            team.onStop();
        }

        // Remove entities
        Entities.killSpawned();

        // Remove garbage entities
        CFGarbageCollector.clearInAllWorlds();

        entities.forEach((uuid, entity) -> entity.onStop(gameInstance));
        entities.clear();

        if (debugData.or(DebugData.Flag.DEBUG)) {
            onStop();
            return;
        }

        // Spawn Fireworks
        gameInstance.executeWinCosmetic();
    }

    public boolean goldenGg(@Nonnull Player player) {
        if (!goldenGg.contains(player)) {
            return false;
        }

        CFUtils.later(() -> Award.GG.award(PlayerProfile.getProfileOrThrow(player)), 1);

        goldenGg.remove(player);
        return true;
    }

    public void resetPlayer(@Nonnull Player player) {
        player.getInventory().clear();
        player.setAllowFlight(false);
        player.setArrowsInBody(0);
        player.setInvulnerable(false);
        player.setHealth(player.getMaxHealth());
        player.setGameMode(GameMode.SURVIVAL);
        player.setWalkSpeed(0.2f);
        player.setWorldBorder(player.getWorld().getWorldBorder());
        player.teleport(EnumLevel.SPAWN.getLevel().getLocation());

        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }

        // Reset attributes
        defaultAttributeValues.forEach((type, value) -> {
            final AttributeInstance attribute = player.getAttribute(type);

            if (attribute != null) {
                attribute.setBaseValue(value);
            }
        });
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
            final Hero hero = getSelectedLobbyHero(player);
            final ArchetypeList archetypes = hero.getArchetypes();

            resetPlayer(player);

            goldenGg.add(player);

            // Progress achievement
            Registries.getAchievements().PLAY_FIRST_GAME.complete(player);

            // Progress bonds
            ChallengeType.PLAY_GAMES.progress(player);

            // Progress archetype bond
            archetypes.forEach(archetype -> {
                ChallengeType.progressArchetypeBond(player, archetype);
            });

            // Give lobby items
            LobbyItems.giveAll(player);

            // Delay rating because too much text
            Runnables.runLater(() -> RateHeroCommand.allowRatingHeroIfHasNotRatedAlready(player, hero), 60);
        }

        if (autoSave.scheduleSave) {
            autoSave.save();
        }
    }

    public void setSelectedHero(Player player, Hero hero) {
        setSelectedHero(player, hero, false);
    }

    public void setSelectedHero(Player player, Hero hero, boolean force) {
        if (Manager.current().isGameInProgress()) {
            Chat.sendMessage(player, "&cUnable to change a hero during the game!");
            PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
            return;
        }

        if (hero.isLocked(player) && !force) {
            Chat.sendMessage(player, "&cThis hero is locked!");
            return;
        }

        if (!hero.isValidHero()) {
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

        if (getSelectedLobbyHero(player) == hero) {
            Chat.sendMessage(player, "&cAlready selected!");
            PlayerLib.villagerNo(player);
            return;
        }

        final PlayerProfile profile = getOrCreateProfile(player);

        profile.setSelectedHero(hero);
        player.closeInventory();

        PlayerLib.villagerYes(player);
        Notifier.success(player, "Selected %s!".formatted(hero.getFormatted(Color.SUCCESS)));

        final RandomHeroEntry entry = profile.getDatabase().randomHeroEntry;

        if (entry.isEnabled()) {
            entry.setEnabled(false);
            entry.setLastSelectedHero(null); // Forget last hero because yes

            Notifier.info(player, "&b&lRandom Hero Select &bwas &c&ndisabled&b because you selected a hero manually!");
        }
    }

    /**
     * @return actual hero player is using right now, trial, lobby or game.
     */
    @Nonnull
    public Hero getCurrentHero(GamePlayer player) {
        return getCurrentEnumHero(player);
    }

    @Nonnull
    public Hero getCurrentEnumHero(GamePlayer player) {
        if (isPlayerInGame(player)) {
            return player != null ? player.getHero() : HeroRegistry.defaultHero();
        }

        return getSelectedLobbyHero(player.getPlayer());
    }

    public boolean isPlayerInGame(GamePlayer player) {
        return gameInstance != null && player != null && player.isAlive();
    }

    public void removeProfile(Player player) {
        final PlayerProfile profile = profiles.remove(player.getUniqueId());

        if (profile == null) {
            return;
        }

        Main.getPlugin().getCrateManager().removeHologram(player);
    }

    public boolean anyProfiles() {
        return !profiles.isEmpty();
    }


    public void listProfiles() {
        final Logger logger = main.getLogger();

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
    public Set<GamePlayer> getPlayers(Predicate<GamePlayer> predicate) {
        final Set<GamePlayer> players = getPlayers();
        players.removeIf(player -> !predicate.test(player));

        return players;
    }

    @Nonnull
    public Set<GamePlayer> getAlivePlayers() {
        return getAlivePlayers(player -> true);
    }

    @Nonnull
    public Set<GamePlayer> getAlivePlayers(Predicate<GamePlayer> predicate) {
        final Set<GamePlayer> players = getPlayers();
        players.removeIf(player -> !player.isAlive() || !predicate.test(player));

        return players;
    }

    @Nonnull
    public Collection<GamePlayer> getAllPlayers() {
        return getPlayers();
    }

    @Nonnull
    public Set<GamePlayer> getAlivePlayers(Hero hero) {
        return getAlivePlayers(player -> player.getHero().equals(hero));
    }

    @Nonnull
    public Set<Hero> getActiveHeroes() {
        final Set<Hero> heroes = Sets.newHashSet();
        getPlayers().forEach(player -> heroes.add(player.getHero()));

        return heroes;
    }

    public void removeEntity(LivingEntity entity) {
        final GameEntity gameEntity = entities.remove(entity.getUniqueId());

        if (gameEntity == null) {
            return;
        }

        //gameEntity.remove();
    }

    public Set<GameEntity> getEntities() {
        return Sets.newHashSet(entities.values());
    }

    @Nonnull
    public <T extends GameEntity> Set<T> getEntities(Class<T> clazz) {
        final Set<T> entities = Sets.newHashSet();

        this.entities.forEach((uuid, entity) -> {
            if (clazz.isInstance(entity)) {
                entities.add(clazz.cast(entity));
            }
        });

        return entities;
    }

    public Set<GameEntity> getEntitiesExcludePlayers() {
        final Set<GameEntity> entities = getEntities();
        entities.removeIf(entity -> entities instanceof GamePlayer);

        return entities;
    }

    public boolean isEntity(LivingEntity living) {
        return entities.containsKey(living.getUniqueId());
    }

    @Nullable
    public GameEntity getEntityById(int entityId) {
        for (GameEntity value : entities.values()) {
            if (value.getId() == entityId) {
                return value;
            }
        }

        return null;
    }

    @Nullable
    public GamePlayer getPlayer(UUID uuid) {
        return getEntity(uuid, GamePlayer.class);
    }

    @Nonnull
    public Hero getSelectedLobbyHero(Player player) {
        final PlayerProfile profile = getProfile(player);

        return profile != null ? profile.getSelectedHero() : HeroRegistry.defaultHero();
    }

    public boolean isInGameOrTrial(@Nullable Player player) {
        final PlayerProfile profile = player != null ? PlayerProfile.getProfile(player) : null;

        return isGameInProgress() || (profile != null && profile.hasTrial());
    }

    @Nonnull
    public AutoSync getAutoSave() {
        return autoSave;
    }

    public void forEachProfile(@Nonnull Consumer<PlayerProfile> consumer) {
        profiles.values().forEach(consumer);
    }

    @Nonnull
    public FairMode getFairMode() {
        return fairMode;
    }

    public void setFairMode(@Nonnull Player player, @Nonnull FairMode fairMode) {
        if (this.fairMode == fairMode) {
            Notifier.error(player, "Already set!");
            return;
        }

        this.fairMode = fairMode;

        Notifier.INFO.broadcast("&6\uD83E\uDD32 &lFAIR MODE &a{%s} has set fair mode to {%s}!".formatted(
                player.getName(),
                fairMode.getName()
        ));
        Notifier.INFO.broadcast(fairMode.getDescription());
    }

    private void playAnimation() {
        new TitleAnimation();
    }

    private void displayError(String message, Object... objects) {
        Chat.broadcast("&c&lUnable to start the game! &c" + message.formatted(objects));
    }

    private Collection<Player> getNonSpectatorPlayers() {
        return Bukkit.getOnlinePlayers().stream().filter(player -> !Settings.SPECTATE.isEnabled(player))
                .collect(Collectors.toSet());
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
