package me.hapyl.fight.game;

import me.hapyl.fight.CF;
import me.hapyl.fight.database.Award;
import me.hapyl.fight.database.entry.RandomHeroEntry;
import me.hapyl.fight.event.custom.game.GameChangeStateEvent;
import me.hapyl.fight.event.custom.game.GameEndEvent;
import me.hapyl.fight.event.custom.game.GameStartEvent;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.cosmetic.WinCosmetic;
import me.hapyl.fight.game.cosmetic.crate.Crates;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.MoveType;
import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.fight.game.gamemode.Modes;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.PlayerDataHandler;
import me.hapyl.fight.game.heroes.TickingHero;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.report.GameReport;
import me.hapyl.fight.game.setting.Settings;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.ShutdownAction;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.util.Materials;
import me.hapyl.fight.util.Nulls;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class GameInstance extends TickingGameTask implements IGameInstance, GameElement {

    private final Cosmetics DEFAULT_WIN_COSMETIC = Cosmetics.FIREWORKS;

    private final String hexCode;
    private final long startedAt;
    //private final Map<UUID, GamePlayer> players;
    //private final Map<LivingEntity, EntityData> entityData;
    private final GameMaps currentMap;
    private final CFGameMode mode;
    private final GameReport gameReport;
    private final GameResult gameResult;

    private long timeLimitInTicks;
    private State gameState;
    private Set<Heroes> activeHeroes;

    public GameInstance(@Nonnull CFGameMode mode, @Nonnull GameMaps map) {
        this.startedAt = System.currentTimeMillis();
        this.mode = mode;

        final int modeLimit = mode.getTimeLimit();
        this.timeLimitInTicks = modeLimit == -1 ? modeLimit : modeLimit * 20L;

        this.createGamePlayers();

        this.gameResult = new GameResult(this);
        this.gameReport = new GameReport(this);
        this.gameState = State.PRE_GAME;
        this.hexCode = generateHexCode();
        this.currentMap = map;

        // This is a main ticker of the game.
        runTaskTimer(0, 1);
    }

    public GameInstance(@Nonnull Modes mode, @Nonnull GameMaps map) {
        this(mode.getMode(), map);
    }

    public void increaseTimeLimit(int increase) {
        this.timeLimitInTicks += increase;
    }

    @Nonnull
    public GameResult getGameResult() {
        return gameResult;
    }

    @Override
    public boolean isReal() {
        return true;
    }

    @Nonnull
    @Override
    public State getGameState() {
        return gameState;
    }

    @Override
    public void setGameState(State gameState) {
        if (new GameChangeStateEvent(this, this.gameState, gameState).callAndCheck()) {
            return;
        }

        this.gameState = gameState;
    }

    public GameReport getGameReport() {
        return gameReport;
    }

    @Override
    public void calculateEverything() {
        GameTask.runLater(() -> {
            gameResult.calculate();
            gameResult.awardWinners();
        }, 10).setShutdownAction(ShutdownAction.IGNORE);
    }

    public void executeWinCosmetic() {
        Cosmetics cosmetic = DEFAULT_WIN_COSMETIC;
        Location location = currentMap.getMap().getLocation();
        Player winner = null;

        // Find winner's cosmetic and location
        if (gameResult.isWinners()) {
            for (final GamePlayer player : gameResult.getWinners()) {
                if (!player.isDead()) { // isDead to allow respawning players to have their cosmetics
                    cosmetic = Nulls.notNullOr(player.getDatabase().cosmeticEntry.getSelected(Type.WIN), DEFAULT_WIN_COSMETIC);
                    location = player.getPlayer().getLocation();
                    winner = player.getPlayer();
                    break;
                }
            }
        }

        if (!(cosmetic.getCosmetic() instanceof WinCosmetic winCosmetic)) {
            Manager.current().onStop();
            throw new IllegalArgumentException("Cosmetic is not a WinCosmetic!");
        }

        final int delay = winCosmetic.getDelay();
        winCosmetic.onDisplay0(new Display(winner, location));

        final Location finalLocation = location;
        GameTask.runLater(() -> {
            winCosmetic.onStop(finalLocation);
            Manager.current().onStop();
        }, delay);
    }

    public long getTimeLimitMillis() {
        return timeLimitInTicks == -1 ? -1 : timeLimitInTicks * 50L;
    }

    @Override
    public long getTimeLeftRaw() {
        return (getTimeLimitMillis() - (System.currentTimeMillis() - startedAt));
    }

    @Override
    public long getTimeLeft() {
        return getTimeLeftRaw() / 50;
    }

    public void setTimeLeft(long timeLeft) {
        this.timeLimitInTicks = timeLeft;
    }

    @Override
    public boolean isTimeIsUp() {
        return System.currentTimeMillis() >= startedAt + getTimeLimitMillis();
    }

    @Override
    public boolean checkWinCondition() {
        if (gameState == State.POST_GAME) {
            return true;
        }

        if (mode.testWinCondition(this)) {
            Manager.current().stopCurrentGame();

            return true;
        }

        return false;
    }

    @Nonnull
    @Override
    public CFGameMode getMode() {
        return mode;
    }

    @Override
    public boolean isWinner(Player player) {
        return gameResult.isWinner(player);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onStart() {
        mode.onStart(this);

        for (final Heroes enumHero : Heroes.values()) {
            final Hero hero = enumHero.getHero();

            hero.onStart();
            hero.getWeapon().onStart();

            // Schedule task
            if (hero instanceof TickingHero tickingHero) {
                new TickingGameTask() {
                    @Override
                    public void run(int tick) {
                        tickingHero.tick(tick);
                    }
                }.runTaskTimer(1, 1);
            }
        }

        for (final Talents enumTalent : Talents.values()) {
            enumTalent.getTalent().onStart();
        }

        currentMap.getMap().onStart();

        // Call event
        new GameStartEvent(this).call();
    }

    // This while onStart() onStop() calls are fucking stupid, why not design using the existing event system
    @Override
    @OverridingMethodsMustInvokeSuper
    public void onStop() {
        // Generate crates
        for (final Material material : Material.values()) {
            if (material.isItem()) {
                Materials.setCooldown(material, 0);
            }
        }

        // call talents onStop and reset cooldowns
        for (final Talents enumTalent : Talents.values()) {
            enumTalent.getTalent().onStop();
        }

        // call heroes onStop
        for (final Heroes enumHero : Heroes.values()) {
            final Hero hero = enumHero.getHero();

            hero.onStop();
            hero.clearUsingUltimate();

            if (hero instanceof PlayerDataHandler<?> handler) {
                handler.resetPlayerData();
            }

            final Weapon weapon = hero.getWeapon();

            weapon.onStop();
            weapon.getAbilities().forEach(Ability::clearCooldowns);
        }

        // call maps onStop
        currentMap.getMap().onStop();

        CF.getPlayers().forEach(player -> {
            player.getHero().onStop(player);
            currentMap.getMap().onStop(player);

            if (player.isSpectator()) {
                return;
            }

            // Give crates to players
            Crates.grant(player, Crates.randomCrate());
        });

        // Call event
        new GameEndEvent(this).call();
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onPlayersRevealed() {
        for (final Heroes enumHero : Heroes.values()) {
            enumHero.getHero().onPlayersRevealed();
        }

        for (final Talents enumTalent : Talents.values()) {
            enumTalent.getTalent().onPlayersRevealed();
        }

        currentMap.getMap().onPlayersRevealed();
    }

    @Nonnull
    public String hexCode() {
        return this.hexCode;
    }

    @Nonnull
    @Override
    public GameMaps getEnumMap/*prefer adding enum to an enums*/() {
        return currentMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final GameInstance that = (GameInstance) o;
        return Objects.equals(hexCode, that.hexCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hexCode);
    }

    public long getStartedAt() {
        return startedAt;
    }

    @Nonnull
    public Location getRandomPlayerLocationOrMapLocationIfThereAreNoPlayers() {
        final Set<GamePlayer> players = CF.getPlayers();

        if (players.size() != 0) {
            for (GamePlayer value : players) {
                return value.getPlayer().getLocation();
            }
        }

        return currentMap.getMap().getLocation();
    }

    @Override
    public void run(final int tick) {
        mode.tick(this, tick);

        final Set<GamePlayer> alivePlayers = CF.getAlivePlayers();

        if (Manager.current().isDebug()) {
            alivePlayers.forEach(player -> {
                player.setUltPoints(player.getUltPointsNeeded());
            });
            return;
        }

        // AFK detection
        alivePlayers.forEach(player -> {
            if (player.hasMovedInLast(MoveType.MOUSE, 15000)) { // 15s afk detection
                return;
            }

            player.addEffect(Effects.GLOWING, 20);
            player.sendTitle("&c&lYOU'RE AFK", "&aMove to return from afk!", 0, 10, 0);

            if (tick % 10 == 0) {
                player.playSound(Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f);
            }

            Achievements.AFK.complete(player);
        });

        if (timeLimitInTicks == -1) {
            return;
        }

        // Auto-Points
        if (gameState == State.IN_GAME && modulo(20)) {
            alivePlayers.forEach(player -> {
                player.addUltimatePoints(1);
            });
        }

        // Award coins for minute played
        if (tick % 1200 == 0 && tick >= 60 * 50) {
            alivePlayers.forEach(Award.MINUTE_PLAYED::award);
        }

        if (tick >= timeLimitInTicks) {
            Chat.broadcast("&a&lTime is Up! &aGame Over.");
            Manager.current().stopCurrentGame();
            cancel();
        }
    }

    private void createGamePlayers() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            final PlayerProfile profile = PlayerProfile.getProfile(player);

            if (profile == null) {
                return;
            }

            final RandomHeroEntry entry = profile.getDatabase().randomHeroEntry;

            if (entry.isEnabled()) {
                final Heroes randomHero = entry.getRandomHero();

                entry.setLastSelectedHero(profile.getHero());
                profile.setSelectedHero(randomHero);

                Chat.sendMessage(player, "");
                Chat.sendMessage(player, "&6✒ %s &bwas randomly selected!".formatted(randomHero.getFormatted(Color.DARK_AQUA)));
                Chat.sendMessage(player, "");
            }

            final GamePlayer gamePlayer = profile.createGamePlayer();

            // Spectate Setting
            if (Settings.SPECTATE.isEnabled(player)) {
                gamePlayer.setSpectator(true);
            }
            else {
                gamePlayer.resetPlayer();
            }

            if (Settings.HIDE_UI.isEnabled(player)) {
                gamePlayer.sendMessage("&6Your UI is hidden!");
                gamePlayer.sendMessage("&6Use &e/settings &76 to turn enable the UI!");
            }

            gamePlayer.updateScoreboardTeams(false);
        });
    }

    private String generateHexCode() {
        return Integer.toHexString(new Random().nextInt());
    }

}

