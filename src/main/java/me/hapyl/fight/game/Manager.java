package me.hapyl.fight.game;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.eterna.Eterna;
import me.hapyl.eterna.builtin.manager.DialogManager;
import me.hapyl.eterna.builtin.manager.ParkourManager;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.player.PlayerSkin;
import me.hapyl.eterna.module.player.dialog.DialogInstance;
import me.hapyl.eterna.module.util.Compute;
import me.hapyl.eterna.module.util.Opt;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.eterna.module.util.collection.Cache;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.Message;
import me.hapyl.fight.annotate.ProgrammerShouldPreferCFCallInsteadOfCallingThisMethod;
import me.hapyl.fight.annotate.SensitiveParameter;
import me.hapyl.fight.command.RateHeroCommand;
import me.hapyl.fight.database.Award;
import me.hapyl.fight.database.entry.RandomHeroEntry;
import me.hapyl.fight.event.ProfileDeinitializationEvent;
import me.hapyl.fight.event.ProfileInitializationEvent;
import me.hapyl.fight.game.challenge.ChallengeType;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.competetive.Tournament;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.damage.DamageType;
import me.hapyl.fight.game.element.ElementCaller;
import me.hapyl.fight.game.entity.*;
import me.hapyl.fight.game.entity.commission.CommissionOverlayEntity;
import me.hapyl.fight.game.heroes.Archetype;
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
import me.hapyl.fight.game.setting.EnumSetting;
import me.hapyl.fight.game.skin.SkinEffectManager;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.type.EnumGameType;
import me.hapyl.fight.game.type.GameType;
import me.hapyl.fight.game.ui.PlayerUI;
import me.hapyl.fight.garbage.SynchronizedGarbageEntityCollector;
import me.hapyl.fight.guesswho.GuessWho;
import me.hapyl.fight.npc.TheEyeNPC;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class Manager extends BukkitRunnable {
    
    private final Main main;
    private final Set<EntityType> ignoredTypes = Sets.newHashSet(
            EntityType.ARMOR_STAND,
            EntityType.MARKER,
            EntityType.TEXT_DISPLAY,
            EntityType.BLOCK_DISPLAY,
            EntityType.GIANT
    );
    
    private final Cache<UUID> goldenGg = Cache.ofSet(10_000L);
    public final Set<UUID> ignoredEntities = Sets.newHashSet();
    
    private final Map<UUID, GameEntity> entities;
    private final Map<UUID, PlayerProfile> profiles;
    private final Map<Attribute, Double> defaultAttributeValues;
    private final Map<Projectile, DamageCause> overrideProjectileDamageCauseMap;
    
    private final SkinEffectManager skinEffectManager;
    private final AutoSync autoSave;
    
    @Nonnull private EnumLevel currentMap;
    @Nonnull private EnumGameType currentMode;
    
    private StartCountdown startCountdown;
    private GameInstance gameInstance; // @implNote: For now, only one game instance can be active at a time.
    private Tournament competitive;
    private GuessWho guessWhoGame;
    private FairMode fairMode;
    
    public Manager(@Nonnull Main main) {
        this.main = main;
        
        entities = Maps.newConcurrentMap();
        profiles = Maps.newConcurrentMap();
        overrideProjectileDamageCauseMap = Maps.newHashMap();
        
        defaultAttributeValues = Map.of(
                Attribute.MAX_HEALTH, 20.0d,
                Attribute.KNOCKBACK_RESISTANCE, 0.0d,
                Attribute.ATTACK_SPEED, 1_000d, // Keep attack speed 1,000 because I said so
                Attribute.SCALE, 1.0d,
                Attribute.STEP_HEIGHT, 0.6d,
                Attribute.GRAVITY, 0.08d
        );
        
        currentMap = main.getConfigEnumValue("current-map", EnumLevel.class, EnumLevel.ARENA);
        currentMode = main.getConfigEnumValue("current-mode", EnumGameType.class, EnumGameType.FFA);
        
        // init skin effect manager
        skinEffectManager = new SkinEffectManager(main);
        
        // start auto save timer
        autoSave = new AutoSync(Tick.fromMinutes(10));
        
        fairMode = FairMode.UNFAIR;
        
        runTaskTimer(main, 0, 1);
    }
    
    public void overrideProjectileDamageCause(@Nonnull Projectile projectile, @Nonnull DamageCause cause) {
        if (cause.type() != DamageType.DIRECT_RANGE) {
            throw new IllegalArgumentException("Damage cause type must be DIRECT_RANGE, not %s!".formatted(cause.type()));
        }
        
        overrideProjectileDamageCauseMap.put(projectile, cause);
    }
    
    @Nonnull
    public DamageCause projectileCause(@Nonnull Projectile projectile) {
        final DamageCause cause = overrideProjectileDamageCauseMap.remove(projectile);
        
        return cause != null ? cause : DamageCause.PROJECTILE;
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
        
        entities.forEach(entity -> {
            // There was a Ticking instance check before,
            // but I really think that entities other than players should be ticked separately.
            if (entity instanceof Ticking ticking) {
                ticking.tick();
            }
        });
    }
    
    public void createStartCountdown() {
        if (!canStartGame()) {
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
                createNewGameInstance(() -> new GameInstance(currentMode, currentMap), false);
            }
        };
    }
    
    public void stopStartCountdown(@Nonnull Player player) {
        if (startCountdown != null) {
            startCountdown.cancel();
            startCountdown = null;
        }
        
        final PlayerProfileData playerData = getProfile(player).getPlayerData();
        final AchievementData data = playerData.getAchievementData(Registries.achievements().I_DONT_WANT_TO_PLAY);
        
        final int useTime = data.checkExpire(10000).increment(Type.USE_TIME, 1);
        
        if (useTime >= 10) {
            data.completeAchievement();
        }
    }
    
    public boolean isIgnoredType(@Nonnull LivingEntity entity) {
        return ignoredTypes.contains(entity.getType());
    }
    
    @Nullable
    public StartCountdown getStartCountdown() {
        return startCountdown;
    }
    
    /**
     * @deprecated prefer {@link CF#getProfile(Player)}
     */
    @ProgrammerShouldPreferCFCallInsteadOfCallingThisMethod
    @Nonnull
    @Deprecated
    public PlayerProfile getProfile(@Nonnull Player player) {
        final PlayerProfile profile = profiles.get(player.getUniqueId());
        
        if (profile == null) {
            Message.error(player, "There was an error grabbing your profile! This should not have happened!");
            throw new IllegalStateException("Illegal profile grab! If you /reload the server, ignore this. Otherwise this is a bug!");
        }
        
        return profile;
    }
    
    /**
     * @deprecated prefer {@link CF#getProfileOrNull(Player)}
     */
    @ProgrammerShouldPreferCFCallInsteadOfCallingThisMethod
    @Nullable
    @Deprecated
    public PlayerProfile getProfileOrNull(@NotNull Player player) {
        return profiles.get(player.getUniqueId());
    }
    
    /**
     * @deprecated prefer {@link CF#hasProfile(Player)}
     */
    @ProgrammerShouldPreferCFCallInsteadOfCallingThisMethod
    @Deprecated
    public boolean hasProfile(@Nonnull Player player) {
        return profiles.containsKey(player.getUniqueId());
    }
    
    @Nonnull
    public PlayerProfile createProfile(
            @Nonnull @SensitiveParameter(
                    throwsIllegalArgumentException = "If the player is offline.",
                    throwsIllegalStateException = "If the player already has profile.") Player player
    ) {
        final UUID uniqueId = player.getUniqueId();
        
        if (!player.isOnline()) {
            throw new IllegalArgumentException("Unable to create profile for offline player!");
        }
        
        if (profiles.containsKey(uniqueId)) {
            throw new IllegalStateException("Duplicate profile creation!");
        }
        
        final PlayerProfile profile = new PlayerProfile(player);
        profiles.put(uniqueId, profile);
        
        // Call event
        new ProfileInitializationEvent(profile).callEvent();
        
        return profile;
    }
    
    public void deleteProfile(@Nonnull @SensitiveParameter(throwsIllegalStateException = "If the profile doesn't exist.") Player player) {
        final PlayerProfile profile = profiles.remove(player.getUniqueId());
        
        if (profile == null) {
            throw new IllegalStateException("Unable to delete profile for %s because it doesn't exist!".formatted(player.getUniqueId()));
        }
        
        // Call event
        new ProfileDeinitializationEvent(profile).callEvent();
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
    public <E extends LivingEntity, T extends GameEntity> T createEntity(
            @Nonnull Location location,
            @Nonnull Entities<E> type,
            @Nonnull Function<E, T> consumer
    ) {
        final E entity = type.spawn(
                location, self -> {
                    // Mark 'ignored' so manager doesn't automatically create a handle to it
                    ignoredEntities.add(self.getUniqueId());
                }
        );
        
        return registerEntity(consumer.apply(entity));
    }
    
    @Nonnull
    public CommissionOverlayEntity createOverlayEntity(@Nonnull Location location, @Nonnull BiFunction<Location, Husk, CommissionOverlayEntity> fn) {
        // Overlay entities are stored by NPC id for faster lookup
        return createEntity(
                location, Entities.HUSK, self -> {
                    self.setVisibleByDefault(false);
                    self.setAdult();
                    self.setSilent(true);
                    
                    return fn.apply(location, self);
                }
        );
    }
    
    @Nonnull
    public <T extends GameEntity> T registerEntity(@Nonnull T entity) {
        final UUID uuid = entity.getUUID();
        
        if (entities.containsKey(uuid)) {
            entity.forceRemove();
            throw new IllegalArgumentException("Duplicate entity creation!");
        }
        
        // Remove from ignored
        ignoredEntities.remove(uuid);
        entities.put(uuid, entity);
        return entity;
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
    
    @Nonnull
    public PlayerUI getPlayerUI(Player player) {
        return getProfile(player).getPlayerUI();
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
        final GameInstance gameInstance = currentInstanceOrNull();
        
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
     * @implNote Use {@link #currentInstance()} to safely retrieve GameInstance.
     */
    @Nullable
    public GameInstance currentInstanceOrNull() throws RuntimeException {
        return gameInstance;
    }
    
    /**
     * @return game instance if present, else an abstract version.
     */
    @Nonnull
    public IGameInstance currentInstance() {
        return gameInstance == null ? IGameInstance.NULL_GAME_INSTANCE : gameInstance;
    }
    
    @Nonnull
    public Opt<GameInstance> currentInstanceOptional() {
        return Opt.of(gameInstance);
    }
    
    @Nonnull
    public EnumLevel currentEnumLevel() {
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
        return CF.environment().debug.isEnabled();
    }
    
    public void setCurrentMode(@Nonnull EnumGameType mode) {
        if (mode == currentEnumType()) {
            return;
        }
        
        currentMode = mode;
        Chat.broadcast("&aChanged current game mode to %s.".formatted(mode.getMode().getName()));
        
        // save to config
        Main.getPlugin().setConfigValue("current-mode", mode.name().toLowerCase(Locale.ROOT));
    }
    
    public boolean canStartGame() {
        if (gameInstance != null) {
            displayError("a game is already in progress!");
            return false;
        }
        
        // Pre-game start checks
        if ((!currentMap.isPlayable() || !currentMap.getLevel().hasLocation()) && !isDebug()) {
            displayError("illegal map");
            return false;
        }
        
        // Check if a player is in a trial
        for (PlayerProfile profile : profiles.values()) {
            if (profile.hasTrial()) {
                displayError(profile.getPlayer().getName() + " is in a Trial");
                return false;
            }
        }
        
        // TODO (Thu, Aug 29 2024 @xanyjl): !!! Make sure only one hero of the give type can be in a single team
        
        final GameType mode = currentEnumType().getMode();
        final int playerRequirements = mode.getPlayerRequirements();
        final int teamRequirements = mode.getTeamRequirements();
        
        final Collection<Player> nonSpectatorPlayers = getNonSpectatorPlayers();
        
        // Check for minimum players if not in debug
        if (CF.environment().debug.isEnabled()) {
            return true;
        }
        
        final List<GameTeam> teams = GameTeam.getPopulatedTeams();
        
        // Check teams
        if (teams.size() < teamRequirements) {
            displayError("there aren't enough teams (%s/%s)".formatted(teams.size(), teamRequirements));
            return false;
        }
        
        // Check players
        if (nonSpectatorPlayers.size() < playerRequirements) {
            displayError("there aren't enough players! &l(%s/%s)".formatted(nonSpectatorPlayers.size(), playerRequirements));
            return false;
        }
        
        // Check if there are duplicate heroes in teams
        for (GameTeam team : teams) {
            final Map<Hero, Set<Player>> playerHeroes = Maps.newHashMap();
            
            for (Player player : team.getBukkitPlayers()) {
                final PlayerProfile profile = CF.getProfile(player);
                
                playerHeroes.compute(profile.getHero(), Compute.setAdd(player));
            }
            
            for (Set<Player> players : playerHeroes.values()) {
                if (players.size() > 1) {
                    // Notify players
                    players.forEach(player -> {
                        final List<Player> others = players.stream().filter(p -> player != p).toList();
                        
                        Message.error(player, "Only one hero is allowed per one team!");
                        Message.error(
                                player, "You and {%s} have the same here selected.".formatted(Chat.makeStringCommaAnd(
                                        others,
                                        Player::getName
                                ))
                        );
                    });
                    
                    displayError("there are duplicate heroes in %s team".formatted(team.getName()));
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Creates a new game instance.
     * <p>
     * Only one game instance can be active at a time.
     */
    public void createNewGameInstance(@Nonnull Supplier<GameInstance> instance, boolean force) {
        if (!canStartGame() && !force) {
            return;
        }
        
        // Stop GuessWho
        stopGuessWhoGame();
        
        final ParkourManager parkourManager = Eterna.getManagers().parkour;
        final DialogManager dialogManager = Eterna.getManagers().dialog;
        
        //
        for (Player player : Bukkit.getOnlinePlayers()) {
            parkourManager.quit(player);
            
            final DialogInstance dialog = dialogManager.get(player);
            
            if (dialog != null) {
                dialog.cancel();
            }
        }
        
        // Create new instance and call onStart methods
        this.gameInstance = instance.get();
        this.gameInstance.onStart();
        
        // Init skin manager
        this.skinEffectManager.onStart();
        
        for (final GamePlayer gamePlayer : CF.getPlayers()) {
            final Player player = gamePlayer.getEntity();
            
            // Equip and hide players
            if (!gamePlayer.isSpectator()) {
                gamePlayer.prepare(gamePlayer.getHero());
                gamePlayer.hidePlayer();
                
                // Apply player skin if exists
                final PlayerSkin skin = gamePlayer.getHero().getSkin();
                
                if (EnumSetting.USE_SKINS_INSTEAD_OF_ARMOR.isEnabled(player) && skin != null) {
                    skin.apply(player);
                }
            }
            
            // Teleport to the map
            player.teleport(this.gameInstance.currentLevel().getLocation());
            
            // Glow teammates right after teleport
            gamePlayer.getTeam().glowTeammates();
        }
        
        final int revealIn = this.gameInstance.currentLevel().getTimeBeforeReveal();
        
        if (!CF.environment().debug.isEnabled()) {
            if (revealIn > 0) {
                Chat.broadcast("&a&l➺ &2All players have been hidden!");
                Chat.broadcast("&a&l➺ &2They have &a%ss &2to spread before being revealed.".formatted(Tick.round(revealIn)));
            }
        }
        else {
            this.gameInstance.setTimeLeft(10000000000L);
        }
        
        // On reveal
        GameTask.runLater(
                () -> {
                    if (revealIn > 0) {
                        Chat.broadcast("&a&l➺ &aPlayers have been revealed. &lFIGHT!");
                    }
                    
                    gameInstance.setGameState(State.IN_GAME);
                    
                    ElementCaller.CALLER.onPlayersRevealed(gameInstance);
                    
                    CF.getAlivePlayers().forEach(player -> {
                        final World world = player.getWorld();
                        
                        player.showPlayer();
                        
                        if (!isDebug()) {
                            world.strikeLightningEffect(player.getLocation().add(0, 2, 0));
                        }
                    });
                    
                    gameInstance.playStartAnimation();
                }, isDebug() ? 1 : revealIn
        );
        
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
        Entities.defaultCache().dispose();
        
        // Remove garbage entities
        SynchronizedGarbageEntityCollector.clearInAllWorlds();
        
        entities.forEach((uuid, entity) -> entity.onStop(gameInstance));
        entities.clear();
        
        if (isDebug()) {
            onStop();
            return;
        }
        
        // Spawn Fireworks
        gameInstance.executeWinCosmetic();
    }
    
    public boolean goldenGg(@Nonnull Player player) {
        final UUID uuid = player.getUniqueId();
        
        if (!goldenGg.contains(uuid)) {
            return false;
        }
        
        goldenGg.remove(uuid);
        
        CFUtils.later(() -> Award.GG.award(CF.getProfile(player)), 1);
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
            final List<Archetype> archetypes = hero.getProfile().getArchetypes();
            
            resetPlayer(player);
            allowGoldenGg(player);
            
            // Progress achievement
            Registries.achievements().PLAY_FIRST_GAME.complete(player);
            
            // Progress bonds
            ChallengeType.PLAY_GAMES.progress(player);
            
            // Progress archetype bond
            archetypes.forEach(archetype -> {
                ChallengeType.progressArchetypeBond(player, archetype);
            });
            
            // Give lobby items
            LobbyItems.giveAll(player);
            
            // Delay the eye text
            final TheEyeNPC theEye = Registries.npcs().THE_EYE;
            final TheEyeNPC.EyeNotification notification = theEye.getFirstEyeNotification(player);
            
            if (notification != null) {
                GameTask.runLater(() -> theEye.sendNpcMessage(player, notification.randomChatString()), 20);
            }
            
            // Delay rating because too much text
            GameTask.runLater(() -> RateHeroCommand.allowRatingHeroIfHasNotRatedAlready(player, hero), 60);
        }
        
        if (autoSave.scheduleSave) {
            autoSave.save();
        }
    }
    
    public void allowGoldenGg(@Nonnull Player player) {
        goldenGg.add(player.getUniqueId());
    }
    
    public void setSelectedHero(@Nonnull Player player, @Nonnull Hero hero) {
        setSelectedHero(player, hero, false);
    }
    
    public void setSelectedHero(@Nonnull Player player, @Nonnull Hero hero, boolean force) {
        if (Manager.current().isGameInProgress()) {
            Message.error(player, "Unable to change a hero during the game!");
            Message.sound(player, SoundEffect.ERROR);
            return;
        }
        
        if (hero.isLocked(player) && !force) {
            Message.error(player, "You haven't unlocked this hero!");
            Message.sound(player, SoundEffect.ERROR);
            return;
        }
        
        if (getSelectedLobbyHero(player) == hero) {
            Message.error(player, "Already selected!");
            Message.sound(player, SoundEffect.ERROR);
            return;
        }
        
        if (hero instanceof Disabled disabled) {
            if (!player.isOp()) {
                disabled.errorMessage(player, "hero");
                return;
            }
            
            if (!force && !CF.environment().allowDisabledHeroes.isEnabled()) {
                Message.error(player, "Not selecting a disabled hero without {%s} argument!".formatted(Hero.DISABLED_HERO_FLAG));
                Message.sound(player, SoundEffect.FAILURE);
                return;
            }
            
            Message.error(player, "You have selected a DISABLED hero which is either broken or not finished yet!");
            Message.error(player, "It might throw exceptions or break the game!");
            Message.error(player, "&lYOU HAVE BEEN WARNED!");
        }
        
        final PlayerProfile profile = getProfile(player);
        
        profile.setSelectedHero(hero);
        player.closeInventory();
        
        Message.success(player, "Selected {%s}!".formatted(hero.getFormatted(Color.SUCCESS)));
        Message.sound(player, SoundEffect.SUCCESS);
        
        final RandomHeroEntry entry = profile.getDatabase().randomHeroEntry;
        
        if (entry.isEnabled()) {
            entry.setEnabled(false);
            entry.setLastSelectedHero(null); // Forget last hero because yes
            
            Message.info(player, "&b&lRandom Hero Select &bwas &c&ndisabled&b because you selected a hero manually!");
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
        
        return getSelectedLobbyHero(player.getEntity());
    }
    
    public boolean isPlayerInGame(GamePlayer player) {
        return gameInstance != null && player != null && player.isAlive();
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
    
    public void removeEntity(@Nonnull LivingEntity entity) {
        final GameEntity gameEntity = entities.remove(entity.getUniqueId());
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
        return getProfile(player).getSelectedHero();
    }
    
    public boolean isInGameOrTrial(@Nullable Player player) {
        final PlayerProfile profile = player != null ? CF.getProfile(player) : null;
        
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
            Message.error(player, "Already set!");
            return;
        }
        
        this.fairMode = fairMode;
        
        Message.INFO.broadcast("&6\uD83E\uDD32 &lFAIR MODE &a{%s} has set fair mode to {%s}!".formatted(
                player.getName(),
                fairMode.getName()
        ));
        Message.INFO.broadcast(fairMode.getDescription());
    }
    
    public void doStartOrCancelCountdown(@Nonnull Player player) {
        if (isGameInProgress()) {
            Message.error(player, "Cannot use this right now!");
            return;
        }
        
        final StartCountdown countdown = getStartCountdown();
        
        if (countdown != null) {
            countdown.cancelByPlayer(player);
            stopStartCountdown(player);
            return;
        }
        
        createStartCountdown();
    }
    
    public void allowEveryoneGoldenGg() {
        Bukkit.getOnlinePlayers().forEach(this::allowGoldenGg);
    }
    
    public boolean isLobby() {
        return gameInstance == null;
    }
    
    @Nonnull
    public EnumGameType currentEnumType() {
        return currentMode;
    }
    
    @Nonnull
    public GameType currentType() {
        return currentMode.getMode();
    }
    
    private void displayError(@Nonnull String message) {
        Message.ERROR.broadcast("Unable to start the game because {%s}!".formatted(message));
    }
    
    private Collection<Player> getNonSpectatorPlayers() {
        return Bukkit.getOnlinePlayers().stream().filter(player -> !EnumSetting.SPECTATE.isEnabled(player))
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
