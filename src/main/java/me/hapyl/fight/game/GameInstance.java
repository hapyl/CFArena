package me.hapyl.fight.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.fight.Shortcuts;
import me.hapyl.fight.game.database.entry.CurrencyEntry;
import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.fight.game.gamemode.Modes;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.report.GameReport;
import me.hapyl.fight.game.setting.Setting;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.ShutdownAction;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;

public class GameInstance extends AbstractGameInstance implements GameElement {

    private final String hexCode;

    private final long startedAt;
    private final long timeLimit;
    private final Map<UUID, GameTeam> teams;
    private final Map<UUID, GamePlayer> players;
    private final Set<GamePlayer> winners;
    private final GameMaps currentMap;
    private final GameTask gameTask;
    private final Modes mode;
    private final GameReport gameReport;

    private State gameState;

    public GameInstance(Modes mode, GameMaps map) {
        this.startedAt = System.currentTimeMillis();
        this.mode = mode;
        this.timeLimit = mode.getMode().getTimeLimit() * 1000L;
        this.teams = Maps.newHashMap();
        this.players = Maps.newHashMap();
        this.createGamePlayers();

        this.gameReport = new GameReport(this);
        this.winners = new HashSet<>();
        this.gameState = State.PRE_GAME;
        this.hexCode = generateHexCode();
        this.currentMap = map;

        // This is a main ticker of the game.
        this.gameTask = startTask();
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
        final String gameDuration = new SimpleDateFormat("mm:ss").format(System.currentTimeMillis() - this.startedAt);

        // show the winners
        Chat.broadcast("&6&l▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");

        Bukkit.getOnlinePlayers().forEach(player -> {
            Chat.sendCenterMessage(player, "&a&lGAME OVER");
            Chat.sendCenterMessage(player, "&8" + gameDuration);
            Chat.sendMessage(player, "");

            if (winners.size() > 0) {
                Chat.sendCenterMessage(player, "&a&lWINNER" + (winners.size() > 1 ? "S" : "") + ":");
                for (final GamePlayer winner : winners) {
                    Chat.sendCenterMessage(player, formatWinnerName(winner));
                    if (winner.compare(player)) {
                        Chat.sendTitle(player, "&6&lVICTORY", "&eYou're the winner!", 10, 60, 5);
                    }
                    else {
                        Chat.sendTitle(
                                player,
                                "&c&lDEFEAT",
                                "&e%s is the winner!".formatted(winner.getPlayer().getName()),
                                10,
                                60,
                                5
                        );
                    }
                }
            }
            else {
                Chat.sendTitle(player, "&6&lGAME OVER", "&eThere is no winners!", 10, 60, 5);
            }
        });

        Chat.broadcast("&6&l▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");

        // show each player their game report
        GameTask.runLater(() -> {
            for (final GamePlayer gamePlayer : players.values()) {
                final Player player = gamePlayer.getPlayer();
                final StatContainer stat = gamePlayer.getStats();

                Chat.sendMessage(player, "&a&lGame Report:");
                Chat.sendMessage(player, stat.getString(StatContainer.Type.COINS));
                Chat.sendMessage(player, stat.getString(StatContainer.Type.KILLS));
                Chat.sendMessage(player, stat.getString(StatContainer.Type.DEATHS));
            }
        }, 20).setShutdownAction(ShutdownAction.IGNORE);

    }

    private Location getFireworkSpawnLocation() {
        Location location = null;
        if (winners.isEmpty()) {
            location = currentMap.getMap().getLocation();
        }
        else {
            for (final GamePlayer winner : winners) {
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
    public String formatWinnerName(GamePlayer gp) {
        final Player player = gp.getPlayer();
        final StatContainer stats = gp.getStats();

        return Chat.bformat(
                "&6{Hero} &e&l{Name} &8(&c&l{Health} &c❤&8, &b&l{Kills} &b🗡&8, &c&l{Deaths} &c☠&8)",
                gp.getHero().getName(),
                player.getName(),
                BukkitUtils.decimalFormat(gp.getHealth()),
                stats.getValue(StatContainer.Type.KILLS),
                stats.getValue(StatContainer.Type.DEATHS)
        );
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
    public List<GamePlayer> getAlivePlayers(Heroes heroes) {
        return getAlivePlayers(gp -> gp.getHero() == heroes.getHero());
    }

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

            gamePlayer.updateScoreboard(true);
            players.put(player.getUniqueId(), gamePlayer);
        });
    }

    private Heroes getHero(Player player) {
        return Setting.RANDOM_HERO.isEnabled(player) ? Heroes.randomHero() : Manager.current().getSelectedHero(player);
    }

    // TODO: 018. 09/18/2021 -> impl teams
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
        if (winners.isEmpty()) {
            return false;
        }
        for (final GamePlayer winner : winners) {
            if (winner.compare(player)) {
                return true;
            }
        }
        return false;
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
                    getAlivePlayers().forEach(player -> {
                        final CurrencyEntry.Award award = CurrencyEntry.Award.MINUTE_PLAYED;
                        player.getDatabase().getCurrency().awardCoins(award);
                        player.getStats().addValue(StatContainer.Type.COINS, award.getAmount());
                    });
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

    @Override
    public Set<GamePlayer> getWinners() {
        return winners;
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
}
