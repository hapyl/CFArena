package me.hapyl.fight.game.ui;

import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.database.entry.CurrencyEntry;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.IGameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.attribute.Attributes;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.setting.Settings;
import me.hapyl.fight.game.task.ShutdownAction;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.team.LocalTeamManager;
import me.hapyl.spigotutils.EternaPlugin;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.math.nn.IntInt;
import me.hapyl.spigotutils.module.player.song.Song;
import me.hapyl.spigotutils.module.player.song.SongPlayer;
import me.hapyl.spigotutils.module.scoreboard.Scoreboarder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * This controls all UI-based elements such as scoreboard, tab-list, and actionbar (while in game).
 */
public class GamePlayerUI extends TickingGameTask {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yy");

    private final PlayerProfile profile;
    private final Player player;
    private final Scoreboarder builder;
    private final UIFormat format = UIFormat.DEFAULT;
    private final String[] clocks = {
            "🕛", "🕐", "🕑", "🕒", "🕓", "🕔", "🕕", "🕖", "🕗", "🕘", "🕙", "🕚"
    };
    private int clock;

    public GamePlayerUI(PlayerProfile profile) {
        this.profile = profile;
        this.player = profile.getPlayer();
        this.builder = new Scoreboarder(Main.GAME_NAME);
        this.updateScoreboard();

        if (Settings.HIDE_UI.isEnabled(player)) {
            hideScoreboard();
        }

        setShutdownAction(ShutdownAction.IGNORE);

        setIncrement(5);
        runTaskTimer(0, 5);
    }

    @Override
    public void run(int tick) {
        if (player == null || !player.isOnline()) {
            cancel();
            return;
        }

        final int mod40 = tick % 40;

        if (modulo(20)) {
            clock = (clock + 1 >= clocks.length) ? 0 : clock + 1;
        }

        // update a player list and scoreboard
        final String[] headerFooter = formatHeaderFooter();
        player.setPlayerListHeaderFooter(Chat.format(headerFooter[0]), Chat.format(headerFooter[1]));
        player.setPlayerListName(profile.getDisplay().getDisplayNameTab());

        // Yes, I know it's not really a UI thing,
        // but I ain't making another ticker just for
        // debugging items.
        updateDebug();

        // Update above name
        if (modulo(10) && !Manager.current().isGameInProgress()) {
            final LocalTeamManager teamManager = profile.getLocalTeamManager();
            teamManager.tickAll();
        }

        final GamePlayer gamePlayer = profile.getGamePlayer();

        // Update UI if enabled
        if (Settings.HIDE_UI.isDisabled(player)) {
            animateScoreboard();
            updateScoreboard();

            if (gamePlayer != null) {
                sendInGameUI(mod40 < 20 ? ChatColor.AQUA : ChatColor.DARK_AQUA);
            }
        }

        if (Settings.SPECTATE.isEnabled(player)) {
            Chat.sendActionbar(
                    player,
                    gamePlayer == null
                            ? "&aYou will spectate when the game starts."
                            : "&aYou are currently spectating."
            );
        }
    }

    public void updateDebug() {
        final GamePlayer gamePlayer = profile.getGamePlayer();

        if (gamePlayer == null) {
            return;
        }

        final EntityAttributes attributes = gamePlayer.getAttributes();
        final Attributes baseAttributes = attributes.getBaseAttributes();

        final ItemBuilder baseBuilder = ItemBuilder.of(Material.COARSE_DIRT, "Base Attributes", "&8Debug").addLore();
        baseAttributes.forEach((type, value) -> baseBuilder.addLore(type.getFormatted(baseAttributes)));

        final ItemBuilder playerBuilder = ItemBuilder.of(Material.DIRT, "Player Attributes", "&8Debug").addLore();
        attributes.forEach((type, value) -> playerBuilder.addLore(type.getFormatted(attributes)));

        player.getInventory().setItem(21, baseBuilder.toItemStack());
        player.getInventory().setItem(23, playerBuilder.toItemStack());
    }

    public void sendInGameUI(@Nonnull ChatColor ultimateColor) {
        final GamePlayer gamePlayer = profile.getGamePlayer();
        if (gamePlayer == null) {
            return;
        }

        // Send UI information
        if (gamePlayer.isAlive() && !gamePlayer.isSpectator()) {
            Chat.sendActionbar(player, format.format(gamePlayer, ultimateColor));
        }
    }

    public void updateScoreboard() {
        final PlayerDatabase playerDatabase = profile.getDatabase();
        final Manager manager = Manager.current();

        builder.getLines().clear();
        builder.addLines("");

        final String mapName = manager.getCurrentMap().getName();

        // Lobby
        if (!manager.isGameInProgress()) {
            final CurrencyEntry currency = playerDatabase.getCurrency();
            final String currentClock = getClock(clock);

            builder.addLines(
                    "&b&l" + currentClock + " Lobby:",
                    " &7ᴍᴀᴘ: &f" + mapName,
                    " &7ᴍᴏᴅᴇ: &f" + manager.getCurrentMode().getName()
            );

            builder.addLine("");
            builder.addLine("&2🧑 &a&lYou, %s:", player.getName());
            builder.addLines(
                    " &7ʀᴀɴᴋ: " + profile.getRank().getPrefixWithFallback(),
                    " &7ʜᴇʀᴏ: " + profile.getSelectedHeroString(),
                    " &7ᴄᴏɪɴs: " + Currency.COINS.getFormatted(player)
            );

            final long rubyCount = currency.get(Currency.RUBIES);
            if (rubyCount > 0) {
                final String rubyCountFormatted = Currency.RUBIES.getFormatted(player);
                builder.addLine(rubyCount == 1 ? "ʀᴜʙʏ" : " &7ʀᴜʙɪᴇs: " + rubyCountFormatted);
            }
        }
        // In Game
        else {
            final IGameInstance game = manager.getCurrentGame();
            final GamePlayer gamePlayer = CF.getPlayer(player);

            if (gamePlayer != null) {
                // Spectator
                if (!gamePlayer.isAlive() && !gamePlayer.isRespawning()) {
                    builder.addLine(
                            Color.SPECTATOR.bold() + "🕶 " + Color.SPECTATOR + "Spectator:"
                    );

                    for (GamePlayer alivePlayer : CF.getAlivePlayers()) {
                        builder.addLine(
                                " &6%s %s &f%s %s &c.1f ❤",
                                alivePlayer.getHero().getNameSmallCaps(),
                                alivePlayer.getTeam().getFirstLetterCaps(),
                                alivePlayer.getName(),
                                UIFormat.DIV,
                                alivePlayer.getHealth()
                        );
                    }
                }
                // In Game
                else {
                    // Default game lines
                    builder.addLines(
                            "&6&l🎮 Game: &8" + game.hexCode(),
                            " &7ᴍᴀᴘ: &f" + mapName,
                            " &7ᴛɪᴍᴇ ʟᴇғᴛ: &f" + getTimeLeftString(game),
                            " &7sᴛᴀᴛᴜs: &f" + gamePlayer.getStatusString()
                    );

                    // Per mode lines
                    game.getMode().formatScoreboard(builder, (GameInstance) game, gamePlayer);
                }
            }
        }

        builder.addLine("");
        builder.updateLines();
        builder.addPlayer(player);
    }

    @Nonnull
    public String getDateFormatted() {
        return DATE_FORMAT.format(LocalDate.now());
    }

    @Nonnull
    public Player getPlayer() {
        return player;
    }

    public void hideScoreboard() {
        this.builder.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
    }

    public void showScoreboard() {
        this.builder.getObjective().setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    private String getClock(int tick) {
        int wrappedIndex = Math.min(tick % clocks.length, clocks.length);

        return clocks[wrappedIndex];
    }

    private void animateScoreboard() {
    }

    private String getTimeLeftString(IGameInstance game) {
        return Manager.current().isDebug() ? "∞" : new SimpleDateFormat("mm:ss").format(game.getTimeLeftRaw());
    }

    private StringBuilder buildGameFooter() {
        final GameTeam team = GameTeam.getPlayerTeam(player);
        final StringBuilder builder = new StringBuilder();

        // Display teammate information:
        if (team != null) {
            builder.append("\n&e&lTeammates:\n");
            if ((team.getPlayers().size() == 1) && (Settings.SHOW_YOURSELF_AS_TEAMMATE.isDisabled(player))) {
                builder.append("&8None!");
            }
            else {
                int i = 0;
                for (GamePlayer teammate : team.getPlayers()) {
                    final boolean usingUltimate = teammate.getHero().isUsingUltimate(teammate);

                    if (i != 0) {
                        builder.append("\n");
                    }

                    builder.append("&a%s &7⁑ &c&l%s &c❤  &b%s".formatted(
                            teammate.getName(),
                            teammate.getHealthFormatted(),
                            usingUltimate ? "&b&lIN USE" : teammate.isUltimateReady() ? "&b&lREADY" : ("&b%s/%s &l※".formatted(
                                    teammate.getUltPoints(),
                                    teammate.getUltPointsNeeded()
                            ))
                    ));

                    i++;
                }
            }
        }

        // Display active effects
        builder.append("\n\n&e&lActive Effects:\n");
        final GamePlayer gp = GamePlayer.getExistingPlayer(this.player);
        if (gp == null || gp.getActiveEffects().isEmpty()) {
            builder.append("&8None!");
        }
        else {
            // {Positive}{Name} - {Time}
            final IntInt i = new IntInt(0);

            gp.getActiveEffects().forEach((type, active) -> {
                final GameEffect gameEffect = type.getGameEffect();
                builder.append(gameEffect.isPositive() ? "&a" : "&c");
                builder.append(gameEffect.getName());
                builder.append(" &f- ");
                builder.append(new SimpleDateFormat("mm:ss").format(active.getRemainingTicks() * 50));
                builder.append(" ");

                i.increment();
                if (i.get() >= 2) {
                    builder.append("\n");
                    i.set(0);
                }
            });
        }

        return builder.append("\n");
    }

    private String[] formatHeaderFooter() {
        final StringBuilder footer = new StringBuilder();

        if (Manager.current().isGameInProgress()) {
            footer.append(buildGameFooter());
        }

        // Display NBS player if playing a song
        final SongPlayer songPlayer = EternaPlugin.getPlugin().getSongPlayer();
        if (songPlayer.getCurrentSong() != null) {
            final Song song = songPlayer.getCurrentSong();
            final StringBuilder builder = new StringBuilder();
            final int frame = (int) (songPlayer.getCurrentFrame() * 30 / songPlayer.getMaxFrame());

            for (int i = 0; i < 30; i++) {
                builder.append(i < frame ? ChatColor.DARK_AQUA : ChatColor.DARK_GRAY);
                builder.append(UIFormat.DIV_RAW);
            }

            footer.append("\n\n&e&lSong Player:\n");
            footer.append("&f%s - %s\n&8%s".formatted(
                    song.getOriginalAuthor(),
                    song.getName(),
                    songPlayer.isPaused() ? "&e&lPAUSE" : builder.toString()
            ));
        }

        footer.append("\n");
        footer.append(Season.WINTER.format(Color.ICY_BLUE, Color.FROSTY_GRAY, Color.ARCTIC_TEAL, Color.SILVER))
                .append(" ")
                .append(Season.WINTER.format(Color.SILVER, Color.ARCTIC_TEAL, Color.FROSTY_GRAY, Color.ICY_BLUE));

        return new String[] {
                """
                                       
                        %s
                        &8%s
                                                
                        &7ᴘʟᴀʏᴇʀs: &f%s&7, ᴛᴘs: %s
                        """.formatted(
                        CF.getName(),
                        CF.getVersion(),
                        CF.getOnlinePlayerCount(),
                        CF.getTpsFormatted()
                ),
                footer.toString()
        };
    }

    private enum Season {
        WINTER("❄");

        private final String string;

        Season(String string) {
            this.string = string;
        }

        @Nonnull
        public String format(@Nonnull Color... colors) {
            final StringBuilder builder = new StringBuilder();
            for (Color color : colors) {
                builder.append(color).append(string);
            }

            return builder.toString();
        }

        @Nonnull
        public String formatBackAndForth(@Nonnull Color... colors) {
            final StringBuilder builder = new StringBuilder();
            builder.append(format(colors));

            for (int i = colors.length - 1; i >= 0; i--) {
                builder.append(colors[i]).append(string);
            }

            return builder.toString();
        }

    }

}
