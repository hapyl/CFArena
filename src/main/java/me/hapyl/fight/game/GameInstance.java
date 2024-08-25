package me.hapyl.fight.game;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.Award;
import me.hapyl.fight.database.entry.RandomHeroEntry;
import me.hapyl.fight.event.custom.game.GameChangeStateEvent;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.cosmetic.WinCosmetic;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.element.ElementCaller;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.MoveType;
import me.hapyl.fight.game.type.GameType;
import me.hapyl.fight.game.type.EnumGameType;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.maps.EnumLevel;
import me.hapyl.fight.game.maps.Level;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.report.GameReport;
import me.hapyl.fight.game.setting.Settings;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.ShutdownAction;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.util.Lifecycle;
import me.hapyl.fight.util.Nulls;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class GameInstance extends TickingGameTask implements IGameInstance, Lifecycle {

    private final Cosmetics DEFAULT_WIN_COSMETIC = Cosmetics.FIREWORKS;

    private final String hexCode;
    private final long startedAt;
    //private final Map<UUID, GamePlayer> players;
    //private final Map<LivingEntity, EntityData> entityData;
    private final EnumLevel currentMap;
    private final GameType mode;
    private final GameReport gameReport;
    private final GameResult gameResult;

    private long timeLimitInTicks;
    private State gameState;
    private Set<Hero> activeHeroes;

    public GameInstance(@Nonnull GameType mode, @Nonnull EnumLevel map) {
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

    public GameInstance(@Nonnull EnumGameType mode, @Nonnull EnumLevel map) {
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

    @Nonnull
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
        Location location = currentMap.getLevel().getLocation();
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
        if (gameState != State.IN_GAME) {
            return false;
        }

        if (mode.testWinCondition(this)) {
            Manager.current().stopCurrentGame();
            return true;
        }

        return false;
    }

    @Nonnull
    @Override
    public GameType getMode() {
        return mode;
    }

    @Override
    public boolean isWinner(Player player) {
        return gameResult.isWinner(player);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onStart() {
        ElementCaller.CALLER.onStart(this);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onStop() {
        ElementCaller.CALLER.onStop(this);
    }

    @Nonnull
    public String hexCode() {
        return this.hexCode;
    }

    @Nonnull
    @Override
    public EnumLevel getEnumMap/*prefer adding enum to an enums*/() {
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

        if (!players.isEmpty()) {
            for (GamePlayer value : players) {
                return value.getPlayer().getLocation();
            }
        }

        return currentMap.getLevel().getLocation();
    }

    @Override
    public void run(final int tick) {
        mode.tick(this, tick);

        final Set<GamePlayer> alivePlayers = CF.getAlivePlayers();

        if (Manager.current().isDebug()) {
            alivePlayers.forEach(player -> {
                player.setEnergy(player.getUltimateCost());
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

            Registries.getAchievements().AFK.complete(player);
        });

        if (timeLimitInTicks == -1) {
            return;
        }

        // Auto-Points
        if (gameState == State.IN_GAME && modulo(20)) {
            alivePlayers.forEach(player -> {
                player.addEnergy(1);
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

    @Nonnull
    public Level currentLevel() {
        return currentMap.getLevel();
    }

    private void createGamePlayers() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            final PlayerProfile profile = PlayerProfile.getProfile(player);

            if (profile == null) {
                return;
            }

            final RandomHeroEntry entry = profile.getDatabase().randomHeroEntry;

            if (entry.isEnabled()) {
                final Hero randomHero = entry.getRandomHero();

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

