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
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.setting.Setting;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.ShutdownAction;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.spigotutils.EternaPlugin;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.math.nn.IntInt;
import me.hapyl.spigotutils.module.player.song.Song;
import me.hapyl.spigotutils.module.player.song.SongPlayer;
import me.hapyl.spigotutils.module.scoreboard.Scoreboarder;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;

/**
 * This controls all UI-based elements such as scoreboard, tab-list, and actionbar (while in game).
 */
public class GamePlayerUI {

    private final PlayerProfile profile;
    private final Player player;
    private final Scoreboarder builder;
    private final UIFormat format = UIFormat.DEFAULT;

    public GamePlayerUI(PlayerProfile profile) {
        this.profile = profile;
        this.player = profile.getPlayer();
        this.builder = new Scoreboarder(Main.GAME_NAME);
        this.updateScoreboard();

        if (Setting.HIDE_UI.isEnabled(player)) {
            hideScoreboard();
        }

        new GameTask() {
            private int tick;

            @Override
            public void run() {
                if (player == null || !player.isOnline()) {
                    this.cancel();
                    return;
                }

                tick = (tick >= 40) ? 0 : tick + 5;

                // update a player list and scoreboard
                final String[] headerFooter = formatHeaderFooter();
                player.setPlayerListHeaderFooter(Chat.format(headerFooter[0]), Chat.format(headerFooter[1]));
                player.setPlayerListName(profile.getDisplay().getDisplayNameTab());

                if (Setting.HIDE_UI.isEnabled(player)) {
                    return;
                }

                // Yes, I know it's not really a UI thing,
                // but I ain't making another ticker just for
                // debugging items.
                updateDebug();

                animateScoreboard();
                updateScoreboard();

                final GamePlayer gamePlayer = profile.getGamePlayer();

                if (gamePlayer != null) {
                    sendInGameUI(tick <= 20 ? ChatColor.AQUA : ChatColor.DARK_AQUA);
                }

                if (Setting.SPECTATE.isEnabled(player)) {
                    Chat.sendActionbar(
                            player,
                            gamePlayer == null
                                    ? "&aYou will spectate when the game starts."
                                    : "&aYou are currently spectating."
                    );
                }
            }
        }.runTaskTimer(0, 5).setShutdownAction(ShutdownAction.IGNORE);
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
        final Manager current = Manager.current();

        this.builder.getLines().clear();
        this.builder.addLines("");
        //        this.builder.addLines("", "Welcome %s to the".formatted(this.player.getName()), "&lClasses Fight &fArena!", "");

        if (current.isGameInProgress()) {
            final IGameInstance game = current.getCurrentGame();
            final GamePlayer gamePlayer = CF.getOrCreatePlayer(this.player);

            // Have to reduce this so everything fits
            if (!gamePlayer.isAlive() && !gamePlayer.isRespawning()) {
                this.builder.addLines("&6&lSpectator: &f%s".formatted(getTimeLeftString(game)));
                for (final GamePlayer alive : CF.getAlivePlayers()) {
                    this.builder.addLine(
                            " &6%s &e%s %s &c%.1f ❤",
                            alive.getHero().getName(),
                            alive.getPlayer().getName(),
                            UIFormat.DIV,
                            alive.getHealth()
                    );
                }
            }

            else {
                // Default Game Lines
                this.builder.addLines(
                        "&6&lGame: &8" + game.hexCode(),
                        " &e&lMap: &f%s".formatted(current.getCurrentMap().getMap().getName()),
                        " &e&lTime Left: &f%s".formatted(getTimeLeftString(game)),
                        " &e&lStatus: &f%s".formatted(gamePlayer.getStatusString())
                );

                game.getMode().formatScoreboard(builder, (GameInstance) game, gamePlayer);
            }

        }
        // Trial
        else if (Manager.current().getTrial().isInTrial(player)) {
            this.builder.addLines(
                    "&6&lTrial:",
                    " &e&lHero: &f%s &cTrial".formatted(profile.getHero().getName())
            );
        }
        else {
            final CurrencyEntry currency = playerDatabase.getCurrency();
            this.builder.addLines(
                    "&6&lLobby:",
                    " &e&lMap: &f%s".formatted(current.getCurrentMap().getMap().getName()),
                    " &e&lMode: &f%s".formatted(current.getCurrentMode().getMode().getName()),
                    " &e&lCoins: &f%s".formatted(currency.getFormatted(Currency.COINS)),
                    " &e&lRubies: &f%s".formatted(currency.getFormatted(Currency.RUBIES)),
                    " &e&lHero: &f%s".formatted(profile.getSelectedHeroString())
            );
        }

        this.builder.addLine("");
        this.builder.updateLines();
        this.builder.addPlayer(player);
    }

    public Player getPlayer() {
        return player;
    }

    public void hideScoreboard() {
        this.builder.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
    }

    public void showScoreboard() {
        this.builder.getObjective().setDisplaySlot(DisplaySlot.SIDEBAR);
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
            if ((team.getPlayers().size() == 1) && (Setting.SHOW_YOURSELF_AS_TEAMMATE.isDisabled(player))) {
                builder.append("&8None!");
            }
            else {
                int i = 0;
                for (GamePlayer teammate : team.getPlayers()) {
                    final boolean usingUltimate = teammate.getHero().isUsingUltimate(teammate.getPlayer());

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

        footer.append("\n&ehapyl.github.io/classes_fight");

        //"\n&e&lCLASSES FIGHT\n&c&lᴀʀᴇɴᴀ\n\n&fTotal Players: &l" + Bukkit.getOnlinePlayers().size()
        return new String[] {
                """
                                       
                        &e&lCLASSES FIGHT
                        &c&lᴀʀᴇɴᴀ
                                                
                        &fTotal Players: &l%s&f, Server TPS: &l%.1f
                        """.formatted(Bukkit.getOnlinePlayers().size(), MinecraftServer.getServer().recentTps[0]), footer.toString()
        };
    }
}
