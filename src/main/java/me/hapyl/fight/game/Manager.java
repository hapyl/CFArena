package me.hapyl.fight.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.cosmetic.skin.SkinEffectManager;
import me.hapyl.fight.game.entity.*;
import me.hapyl.fight.game.gamemode.Modes;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.lobby.LobbyItems;
import me.hapyl.fight.game.lobby.StartCountdown;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.playerskin.PlayerSkin;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.profile.data.AchievementData;
import me.hapyl.fight.game.profile.data.PlayerProfileData;
import me.hapyl.fight.game.profile.data.Type;
import me.hapyl.fight.game.setting.Settings;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.trial.Trial;
import me.hapyl.fight.game.ui.GamePlayerUI;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.range.RangeWeapon;
import me.hapyl.fight.garbage.CFGarbageCollector;
import me.hapyl.fight.util.Nulls;
import me.hapyl.spigotutils.EternaPlugin;
import me.hapyl.spigotutils.module.annotate.Super;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.parkour.ParkourManager;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import me.hapyl.spigotutils.module.util.DependencyInjector;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class Manager extends DependencyInjector<Main> {

    public final Set<UUID> ignoredEntities;
    private final Set<EntityType> ignoredTypes = Sets.newHashSet(
            //EntityType.ARMOR_STAND,
            EntityType.MARKER,
            EntityType.TEXT_DISPLAY,
            EntityType.BLOCK_DISPLAY,
            EntityType.ITEM_DISPLAY
    );
    private final Map<UUID, GameEntity> entities;
    private final Map<UUID, PlayerProfile> profiles;
    private final SkinEffectManager skinEffectManager;
    private final AutoSync autoSave;
    private final Trial trial;
    private final GameTask entityTickTask;

    @Nonnull private GameMaps currentMap;
    @Nonnull private Modes currentMode;
    private StartCountdown startCountdown;
    private GameInstance gameInstance; // @implNote: For now, only one game instance can be active at a time.
    private DebugData debugData;

    public Manager(Main main) {
        super(main);

        ignoredEntities = Sets.newHashSet();
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
        debugData = DebugData.EMPTY;

        entityTickTask = new GameTask() {
            @Override
            public void run() {
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
                    if (entity instanceof GamePlayer ticking) {
                        ticking.tick();
                    }
                });
            }
        }.runTaskTimer(0, 1);
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
        final AchievementData data = playerData.getAchievementData(Achievements.I_DONT_WANT_TO_PLAY);

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
        final Main plugin = getPlugin();

        profiles.put(player.getUniqueId(), profile);

        profile.loadData();

        plugin.getExperience().triggerUpdate(player);
        plugin.getCrateManager().createHologram(player);
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
            case ARROW, SPECTRAL_ARROW, ARMOR_STAND -> createEntity(entity, GameEntity::new);
            default -> createEntity(entity, LivingGameEntity::new);
        };
    }

    @Nonnull
    @Super
    public <E extends LivingEntity, T extends GameEntity> T createEntity(@Nonnull E entity, @Nonnull ConsumerFunction<E, T> consumer) {
        final UUID uuid = entity.getUniqueId();

        if (entities.containsKey(uuid)) {
            throw new IllegalArgumentException("duplicate entity creation");
        }

        final T gameEntity = consumer.apply(entity);
        consumer.andThen(gameEntity);

        entities.put(uuid, gameEntity);
        ignoredEntities.remove(uuid);
        return gameEntity;
    }

    @Nonnull
    public GamePlayer registerGamePlayer(GamePlayer gamePlayer) {
        entities.put(gamePlayer.getUUID(), gamePlayer);
        return gamePlayer;
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
    public GamePlayerUI getPlayerUI(Player player) {
        return getOrCreateProfile(player).getPlayerUI();
    }

    /**
     * Returns if player is able to use an ability.
     * Checks for the game to exist and being in progress.
     *
     * @param player - Player to check.
     * @return true if a player is able to use an ability; false otherwise.
     */
    public boolean isAbleToUse(Player player) {
        return isGameInProgress() || trial.isInTrial(player);
    }

    public boolean isGameInProgress() {
        return gameInstance != null && gameInstance.getGameState() == State.IN_GAME;
    }

    public void handlePlayer(Player player) {
        createProfile(player);

        // teleport either to spawn or the map if there is a game in progress
        final GameInstance gameInstance = getGameInstance();

        if (gameInstance == null) {
            final GameMode gameMode = player.getGameMode();

            if (gameMode != GameMode.CREATIVE && gameMode != GameMode.SPECTATOR) {
                player.teleport(GameMaps.SPAWN.getMap().getLocation());
                LobbyItems.giveAll(player);
            }
        }
        else {
            player.teleport(gameInstance.getEnumMap().getMap().getLocation());
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
        return debugData.is(DebugData.Flag.DEBUG);
    }

    @Nonnull
    public DebugData getDebug() {
        return debugData;
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

    public void createNewGameInstance() {
        createNewGameInstance(DebugData.EMPTY);
    }

    public boolean canStartGame(DebugData debug) {
        // Pre-game start checks
        if ((!currentMap.isPlayable() || !currentMap.getMap().hasLocation()) && !debug.is(DebugData.Flag.DEBUG)) {
            displayError("Invalid map!");
            return false;
        }

        final int playerRequirements = getCurrentMode().getMode().getPlayerRequirements();
        final Collection<Player> nonSpectatorPlayers = getNonSpectatorPlayers();

        // Check for minimum players
        // fixme -> Check for teams, not players
        if (nonSpectatorPlayers.size() < playerRequirements && !debug.is(DebugData.Flag.DEBUG) && debug.not(DebugData.Flag.FORCE)) {
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

        gameInstance.getEnumMap().getMap().onStart();

        for (final GamePlayer gamePlayer : CF.getPlayers()) {
            final Player player = gamePlayer.getPlayer();

            // Equip and hide players
            if (!gamePlayer.isSpectator()) {
                gamePlayer.equipPlayer(gamePlayer.getHero());
                gamePlayer.hide();

                // Apply player skin if exists
                final PlayerSkin skin = gamePlayer.getHero().getSkin();

                if (Settings.USE_SKINS_INSTEAD_OF_ARMOR.isEnabled(player) && skin != null) {
                    skin.apply(player);
                }
            }

            // Teleport to the map
            player.teleport(currentMap.getMap().getLocation());
        }

        if (!debug.is(DebugData.Flag.DEBUG)) {
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

            gameInstance.getEnumMap().getMap().onPlayersReveal();

            if (debug.any()) {
                Chat.broadcast("&c&lDEBUG &fRunning in debug instance.");
                Chat.broadcast("&c&lDEBUG &fDebugging: " + debug.list());
            }

            CF.getAlivePlayers().forEach(target -> {
                final World world = target.getWorld();

                target.getHero().onPlayersReveal(target);
                target.show();
                target.getTeam().glowTeammates();

                if (!debug.is(DebugData.Flag.DEBUG)) {
                    world.strikeLightningEffect(target.getLocation().add(0, 2, 0));
                }
            });

            playAnimation();
        }, debug.or(DebugData.Flag.DEBUG) ? 1 : currentMap.getMap().getTimeBeforeReveal());

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
        for (final Heroes enumHero : Heroes.values()) {
            final Hero hero = enumHero.getHero();

            if (hero == null) {
                continue;
            }

            hero.onStop();
            hero.clearUsingUltimate();

            final Weapon weapon = hero.getWeapon();
            weapon.getAbilities().forEach(Ability::clearCooldowns);

            if (weapon instanceof RangeWeapon rangeWeapon) {
                rangeWeapon.onStop();
            }
        }

        // call maps onStop
        currentMap.getMap().onStop();

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

        entities.forEach((uuid, entity) -> entity.onStop(this.gameInstance));
        entities.clear();

        if (debugData.any()) {
            onStop();
            return;
        }

        // Spawn Fireworks
        gameInstance.executeWinCosmetic();
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

        if (Settings.RANDOM_HERO.isEnabled(player)) {
            Chat.sendMessage(player, "");
            Chat.sendMessage(player, "&aKeep in mind &l%s &ais enabled! Use &e/setting", Settings.RANDOM_HERO.getName());
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
    public Hero getCurrentHero(GamePlayer player) {
        return getCurrentEnumHero(player).getHero();
    }

    // I'm so confused why this is here, it called the same thing
    @Nonnull
    public Heroes getCurrentEnumHero(GamePlayer player) {
        if (isPlayerInGame(player)) {
            if (player == null) {
                return Heroes.ARCHER;
            }

            return player.getEnumHero();
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

    private void loadStaticEvents() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nonnull
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
