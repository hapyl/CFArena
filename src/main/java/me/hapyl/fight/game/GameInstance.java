package me.hapyl.fight.game;

import me.hapyl.fight.CF;
import me.hapyl.fight.database.Award;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.cosmetic.WinCosmetic;
import me.hapyl.fight.game.cosmetic.crate.Crates;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.fight.game.gamemode.Modes;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.report.GameReport;
import me.hapyl.fight.game.setting.Setting;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Nulls;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.List;
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
        return mode;
    }

    @Override
    public boolean isWinner(Player player) {
        return gameResult.isWinner(player);
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
        // Generate crates
        if (Manager.current().isDebug()) {
            //return;
        }

        // Give crates to players
        CF.getPlayers().forEach(player -> {
            if (player.isSpectator()) {
                return;
            }

            Crates.grant(player, Crates.randomCrate());
        });
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

    public void populateScoreboard(Player player) {
        CF.getPlayers().forEach(gamePlayer -> {
            gamePlayer.getProfile().getScoreboardTeams().populateInGame(player);
        });
    }

    @Override
    public void run(final int tick) {
        mode.tick(this, tick);

        final List<GamePlayer> alivePlayers = CF.getAlivePlayers();

        if (Manager.current().isDebug()) {
            alivePlayers.forEach(player -> {
                player.setUltPoints(player.getUltPointsNeeded());
            });
            return;
        }

        // AFK detection
        alivePlayers.forEach(player -> {
            if (player.hasMovedInLast(15000)) { // 15s afk detection
                return;
            }

            player.addPotionEffect(PotionEffectType.GLOWING, 20, 1);
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
        if (tick % 20 == 0) {
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
            final PlayerProfile profile = PlayerProfile.getOrCreateProfile(player);
            final GamePlayer gamePlayer = CF.getOrCreatePlayer(player);

            // Spectate Setting
            if (Setting.SPECTATE.isEnabled(player)) {
                gamePlayer.setSpectator(true);
            }
            else {
                if (Setting.RANDOM_HERO.isEnabled(player)) {
                    profile.setSelectedHero(Heroes.randomHero());

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

            if (Setting.HIDE_UI.isEnabled(player)) {
                gamePlayer.sendMessage("&6Your UI is hidden!");
                gamePlayer.sendMessage("&6Use &e/settings &76 to turn enable the UI!");
            }

            gamePlayer.updateScoreboardTeams(false);
        });
    }

    private Heroes getHero(Player player) {
        return Setting.RANDOM_HERO.isEnabled(player) ? Heroes.randomHero() : Manager.current().getCurrentEnumHero(player);
    }

    private String generateHexCode() {
        return Integer.toHexString(new Random().nextInt());
    }

}
