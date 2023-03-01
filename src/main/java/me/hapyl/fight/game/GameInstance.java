package me.hapyl.fight.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.fight.Shortcuts;
import me.hapyl.fight.game.database.Award;
import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.fight.game.gamemode.Modes;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.report.GameReport;
import me.hapyl.fight.game.setting.Setting;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class GameInstance extends AbstractGameInstance implements GameElement {

    private final String hexCode;

    private final long startedAt;
    private final long timeLimit;
    private final Map<UUID, GamePlayer> players;
    private final GameMaps currentMap;
    private final GameTask gameTask;
    private final Modes mode;
    private final GameReport gameReport;
    private final GameResult gameResult;

    private State gameState;

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
    public void setGameState(State gameState) {
        this.gameState = gameState;
    }

    @Override
    public State getGameState() {
        return gameState;
    }

    public GameReport getGameReport() {
        return gameReport;
    }

    @Override
    public void calculateEverything() {
        gameResult.calculate();
        gameResult.awardWinners();
    }

    private Location getFireworkSpawnLocation() {
        Location location = null;
        if (!gameResult.isWinners()) {
            location = currentMap.getMap().getLocation();
        }
        else {
            for (final GamePlayer winner : gameResult.getWinners()) {
                if (winner.isAlive()) {
                    location = winner.getPlayer().getLocation();
                    break;
                }
            }
        }

        return location;
    }

    @Override
    public void spawnFireworks(boolean flag) {
        final Location location = getFireworkSpawnLocation();
        final Set<Firework> fireworks = new HashSet<>();
        final int maxTimes = 18;
        final int delayPer = 5;

        if (location != null) {
            new GameTask() {
                int currentTimes = 0;

                @Override
                public void run() {

                    if (++currentTimes >= (maxTimes + 1)) {
                        fireworks.forEach(Entity::remove);
                        fireworks.clear();
                        this.cancel();
                    }
                    else {
                        final int randomX = new Random().nextInt(10);
                        final int randomY = new Random().nextInt(5);
                        final int randomZ = new Random().nextInt(10);

                        final boolean negativeX = new Random().nextBoolean();
                        final boolean negativeZ = new Random().nextBoolean();

                        final Location cloned = location.clone().add(
                                negativeX ? -randomX : randomX,
                                randomY,
                                negativeZ ? -randomZ : randomZ
                        );
                        if (cloned.getWorld() == null) {
                            return;
                        }

                        fireworks.add(cloned.getWorld().spawn(cloned, Firework.class, me -> {
                            final FireworkMeta meta = me.getFireworkMeta();
                            meta.setPower(2);
                            //new FireworkEffect(true, true, getRandomColors(), getRandomColors(), FireworkEffect.Type.BURST))
                            meta.addEffect(FireworkEffect.builder()
                                                   .with(FireworkEffect.Type.BURST)
                                                   .withColor(getRandomColor())
                                                   .withFade(getRandomColor())
                                                   .withTrail()
                                                   .build());
                            me.setFireworkMeta(meta);
                        }));
                    }

                }
            }.runTaskTimer(0, delayPer);
        }

        if (flag) {
            GameTask.runLater(() -> {
                fireworks.forEach(Entity::remove);
                fireworks.clear();

                Manager.current().onStop();
            }, (maxTimes * delayPer) + 20);
        }

    }

    private Color getRandomColor() {
        return Color.fromRGB(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255));
    }

    @Override
    public long getTimeLeftRaw() {
        return (timeLimit - (System.currentTimeMillis() - startedAt));
    }

    @Override
    public long getTimeLeft() {
        return getTimeLeftRaw() / 50;
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

    @Override
    public Map<UUID, GamePlayer> getPlayers() {
        return players;
    }

    @Override
    public Collection<GamePlayer> getAllPlayers() {
        return players.values();
    }

    @Override
    public List<GamePlayer> getAlivePlayers(Heroes heroes) {
        return getAlivePlayers(gp -> gp.getHero() == heroes.getHero());
    }

    @Nonnull
    @Override
    public List<GamePlayer> getAlivePlayers() {
        return getAlivePlayers(gp -> gp.getPlayer().isOnline());
    }

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

    @Override
    public List<Player> getAlivePlayersAsPlayers() {
        final List<Player> list = Lists.newArrayList();
        getAlivePlayers().forEach(player -> list.add(player.getPlayer()));
        return list;
    }

    @Override
    public List<Player> getAlivePlayersAsPlayers(Predicate<GamePlayer> predicate) {
        final List<GamePlayer> players = getAlivePlayers(predicate);
        final List<Player> list = Lists.newArrayList();
        for (GamePlayer player : players) {
            list.add(player.getPlayer());
        }
        return list;
    }

    private void createGamePlayers() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            final Heroes hero = getHero(player);
            final PlayerProfile profile = Shortcuts.getProfile(player);
            final GamePlayer gamePlayer = new GamePlayer(profile, hero.getHero());

            // Spectate Setting
            if (Setting.SPECTATE.isEnabled(player)) {
                gamePlayer.setSpectator(true);
                player.setGameMode(GameMode.SPECTATOR);
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

    @Override
    public void checkWinCondition() {
        if (gameState == State.POST_GAME) {
            return;
        }

        if (mode.testWinCondition(this)) {
            Manager.current().stopCurrentGame();
        }
    }

    @Override
    public CFGameMode getMode() {
        return mode.getMode();
    }

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

    public String hexCode() {
        return this.hexCode;
    }

    private String generateHexCode() {
        return Integer.toHexString(new Random().nextInt());
    }

    private GameTask startTask() {
        return new GameTask() {
            private int tick = (int) (timeLimit / 50);

            @Override
            public void run() {

                // AFK detection
                if (!Manager.current().isDebug()) {
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
                }

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

                if (tick < 0 && !Manager.current().isDebug()) {
                    Chat.broadcast("&a&lTime is Up! &aGame Over.");
                    Manager.current().stopCurrentGame();
                    this.cancel();
                }

                --tick;
            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public GameMaps getCurrentMap() {
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
}
