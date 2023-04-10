package me.hapyl.fight.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.Shortcuts;
import me.hapyl.fight.database.Award;
import me.hapyl.fight.exception.ClassesFightException;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.cosmetic.WinCosmetic;
import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.fight.game.gamemode.Modes;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.report.GameReport;
import me.hapyl.fight.game.setting.Setting;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Nulls;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class GameInstance implements IGameInstance, GameElement {

    private final Cosmetics DEFAULT_WIN_COSMETIC = Cosmetics.FIREWORKS;

    private final String hexCode;

    private final long startedAt;
    private final Map<UUID, GamePlayer> players;
    private final GameMaps currentMap;
    private final GameTask gameTask;
    private final Modes mode;
    private final GameReport gameReport;
    private final GameResult gameResult;
    private long timeLimit;
    private State gameState;
    private Set<Heroes> activeHeroes;

    public GameInstance(Modes mode, GameMaps map) {
        this.startedAt = System.currentTimeMillis();
        this.mode = mode;
        this.timeLimit = mode.getMode().getTimeLimit() * 1000L;
        this.players = Maps.newHashMap();
        this.createGamePlayers();

        this.gameResult = new GameResult(this);
        this.gameReport = new GameReport(this);
        this.gameState = State.PRE_GAME;
        this.hexCode = generateHexCode();
        this.currentMap = map;

        // This is a main ticker of the game.
        this.gameTask = startTask();
    }

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
        this.gameState = gameState;
    }

    public GameReport getGameReport() {
        return gameReport;
    }

    @Override
    public void calculateEverything() {
        gameResult.calculate();
        gameResult.awardWinners();
    }

    public void executeWinCosmetic() {
        Cosmetics cosmetic = DEFAULT_WIN_COSMETIC;
        Location location = currentMap.getMap().getLocation();
        Player winner = null;

        // Find winner's cosmetic and location
        if (gameResult.isWinners()) {
            for (final GamePlayer player : gameResult.getWinners()) {
                if (!player.isDead()) { // isDead to allow respawning players to have their cosmetics
                    cosmetic = Nulls.notNullOr(player.getDatabase().getCosmetics().getSelected(Type.WIN), DEFAULT_WIN_COSMETIC);
                    location = player.getPlayer().getLocation();
                    winner = player.getPlayer();
                    break;
                }
            }
        }

        if (!(cosmetic.getCosmetic() instanceof WinCosmetic winCosmetic)) {
            Manager.current().onStop();
            throw new ClassesFightException("Cosmetic is not a WinCosmetic!");
        }

        final int delay = winCosmetic.getDelay();
        winCosmetic.onDisplay(new Display(winner, location));

        final Location finalLocation = location;
        GameTask.runLater(() -> {
            winCosmetic.onStop(finalLocation);
            Manager.current().onStop();
        }, delay);
    }

    @Override
    public long getTimeLeftRaw() {
        return (timeLimit - (System.currentTimeMillis() - startedAt));
    }

    @Override
    public long getTimeLeft() {
        return getTimeLeftRaw() / 50;
    }

    public void setTimeLeft(long timeLeft) {
        this.timeLimit = timeLeft;
    }

    @Override
    public boolean isTimeIsUp() {
        return System.currentTimeMillis() >= startedAt + timeLimit;
    }

    @Override
    @Nullable
    public GamePlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    @Override
    @Nullable
    public GamePlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    @Nonnull
    @Override
    public Map<UUID, GamePlayer> getPlayers() {
        return players;
    }

    @Nonnull
    @Override
    public Collection<GamePlayer> getAllPlayers() {
        return players.values();
    }

    @Nonnull
    @Override
    public List<GamePlayer> getAlivePlayers(Heroes heroes) {
        return getAlivePlayers(gp -> gp.getHero() == heroes.getHero());
    }

    @Nonnull
    @Override
    public List<GamePlayer> getAlivePlayers() {
        return getAlivePlayers(gp -> gp.getPlayer().isOnline());
    }

    @Nonnull
    @Override
    public List<GamePlayer> getAlivePlayers(Predicate<GamePlayer> predicate) {
        final List<GamePlayer> players = new ArrayList<>();
        this.players.forEach((uuid, gp) -> {
            if (gp.isAlive() && predicate.test(gp)) {
                players.add(gp);
            }
        });
        return players;
    }

    @Nonnull
    @Override
    public List<Player> getAlivePlayersAsPlayers() {
        final List<Player> list = Lists.newArrayList();
        getAlivePlayers().forEach(player -> list.add(player.getPlayer()));
        return list;
    }

    @Nonnull
    @Override
    public List<Player> getAlivePlayersAsPlayers(Predicate<GamePlayer> predicate) {
        final List<GamePlayer> players = getAlivePlayers(predicate);
        final List<Player> list = Lists.newArrayList();
        for (GamePlayer player : players) {
            list.add(player.getPlayer());
        }
        return list;
    }

    @Nonnull
    @Override
    public Set<Heroes> getActiveHeroes() {
        if (activeHeroes == null) {
            activeHeroes = Sets.newHashSet();

            for (GamePlayer value : getPlayers().values()) {
                activeHeroes.add(value.getEnumHero());
            }
        }

        return activeHeroes;
    }

    @Nonnull
    public GamePlayer getOrCreateGamePlayer(Player player) {
        GamePlayer gamePlayer = getPlayer(player);

        // If player joined after the game started, create new
        if (gamePlayer == null) {
            gamePlayer = new GamePlayer(PlayerProfile.getProfile(player), getHero(player));
            players.put(player.getUniqueId(), gamePlayer);
        }

        // If player re-joined, change their handle and update it
        if (!gamePlayer.compare(player)) {
            gamePlayer.setHandle(player);
            gamePlayer.updateScoreboard(false);
        }

        return gamePlayer;
    }

    @Override
    public void checkWinCondition() {
        if (gameState == State.POST_GAME) {
            return;
        }

        if (mode.testWinCondition(this)) {
            Manager.current().stopCurrentGame();
        }
    }

    @Nonnull
    @Override
    public CFGameMode getMode() {
        return mode.getMode();
    }

    @Nonnull
    @Override
    public Modes getCurrentMode() {
        return mode;
    }

    @Override
    public boolean isWinner(Player player) {
        return gameResult.isWinner(player);
    }

    @Override
    public void onStart() {
        //Chat.broadcast("&7&oStarting game instance #%s...", this.hexCode());
    }

    @Override
    public void onStop() {
        //Chat.broadcast("&7&oStopping game instance #%s...".formatted(this.hexCode()));
    }

    @Nonnull
    public String hexCode() {
        return this.hexCode;
    }

    @Nonnull
    @Override
    public GameMaps getMap() {
        return currentMap;
    }

    @Override
    public GameTask getGameTask() {
        return gameTask;
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
        return startedAt == that.startedAt && timeLimit == that.timeLimit && Objects.equals(players, that.players);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startedAt, timeLimit, players);
    }

    public long getStartedAt() {
        return startedAt;
    }

    @Nonnull
    public Location getRandomPlayerLocationOrMapLocationIfThereAreNoPlayers() {
        if (players.size() != 0) {
            for (GamePlayer value : players.values()) {
                return value.getPlayer().getLocation();
            }
        }

        return currentMap.getMap().getLocation();
    }

    public void populateScoreboard(Player player) {
        players.values().forEach(gamePlayer -> {
            gamePlayer.getProfile().getScoreboardTeams().populateInGame(player);
        });
    }

    private void createGamePlayers() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            final Heroes hero = getHero(player);
            final PlayerProfile profile = Shortcuts.getProfile(player);
            final GamePlayer gamePlayer = new GamePlayer(profile, hero);

            // Spectate Setting
            if (Setting.SPECTATE.isEnabled(player)) {
                gamePlayer.setSpectator(true);
            }
            else {
                if (Setting.RANDOM_HERO.isEnabled(player)) {
                    gamePlayer.sendMessage("");
                    gamePlayer.sendMessage(
                            "&a&l%s &awas randomly selected as your hero!",
                            gamePlayer.getHero().getName()
                    );
                    gamePlayer.sendMessage("&e/setting &ato turn off this feature.");
                    gamePlayer.sendMessage("");
                }
                gamePlayer.resetPlayer();
            }

            gamePlayer.updateScoreboard(false);
            players.put(player.getUniqueId(), gamePlayer);
        });
    }

    private Heroes getHero(Player player) {
        return Setting.RANDOM_HERO.isEnabled(player) ? Heroes.randomHero() : Manager.current().getSelectedHero(player);
    }

    private String generateHexCode() {
        return Integer.toHexString(new Random().nextInt());
    }

    private GameTask startTask() {
        return new GameTask() {
            private int tick = (int) (timeLimit / 50);

            @Override
            public void run() {
                if (Manager.current().isDebug()) {
                    getAlivePlayers().forEach(player -> {
                        player.setUltPoints(player.getUltPointsNeeded());
                    });
                    return;
                }

                // AFK detection
                getAlivePlayers().forEach(player -> {
                    if (player.hasMovedInLast(15000)) { // 15s afk detection
                        return;
                    }

                    player.addPotionEffect(PotionEffectType.GLOWING, 20, 1);
                    player.sendTitle("&c&lYOU'RE AFK", "&aMove to return from afk!", 0, 10, 0);
                    if (tick % 10 == 0) {
                        player.playSound(Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f);
                    }

                });

                // Auto-Points
                if (tick % 20 == 0) {
                    getAlivePlayers().forEach(player -> {
                        player.addUltimatePoints(1);
                    });
                }

                // Award coins for minute played
                if (tick % 1200 == 0 && tick < (timeLimit / 50)) {
                    getAlivePlayers().forEach(Award.MINUTE_PLAYED::award);
                }

                // Game UI -> Moved to GamePlayerUI

                if (tick < 0) {
                    Chat.broadcast("&a&lTime is Up! &aGame Over.");
                    Manager.current().stopCurrentGame();
                    this.cancel();
                }

                --tick;
            }
        }.runTaskTimer(0, 1);
    }
}
