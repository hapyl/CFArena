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
import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.setting.Settings;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.ShutdownAction;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.eterna.Eterna;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.math.nn.IntInt;
import me.hapyl.eterna.module.player.song.Song;
import me.hapyl.eterna.module.player.song.SongPlayer;
import me.hapyl.eterna.module.scoreboard.Scoreboarder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.DisplaySlot;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * This controls all UI-based elements such as scoreboard, tab-list, and actionbar (while in game).
 */
public class PlayerUI extends TickingGameTask {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yy");

    protected final PlayerProfile profile;
    private final Player player;
    private final Scoreboarder builder;
    private final UIFormat format = UIFormat.DEFAULT;
    private final PlayerTablist tablist;
    private final Manager manager;

    public PlayerUI(PlayerProfile profile) {
        this.profile = profile;
        this.player = profile.getPlayer();
        this.manager = Manager.current();

        // Create scoreboard
        this.builder = new Scoreboarder(Main.GAME_NAME);
        this.builder.setHideNumbers(true);

        this.updateScoreboard();
        this.builder.addPlayer(player);

        // Create tablist
        this.tablist = new PlayerTablist(this);
        this.tablist.show(player);

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

        // update a player list and scoreboard
        final String[] headerFooter = formatHeaderFooter();
        player.setPlayerListHeaderFooter(Chat.format(headerFooter[0]), Chat.format(headerFooter[1]));
        player.setPlayerListName(null); // BREAKS TABLIST THANKS SPIGOT
        //player.setDisplayName(null);

        // Reduce tablist updates
        if (modulo(20)) {
            updateTablist();
        }

        // Yes, I'm updating dailies here, so what?
        if (modulo(100)) {
            profile.getChallengeList().validateSameDay();
        }

        // Yes, I know it's not really a UI thing,
        // but I ain't making another ticker just for
        // debugging items.
        updateDebug();

        // Update above name
        if (modulo(10)) {
            profile.getLocalTeamManager().tick();
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

        // Set ultimate item
        final Hero hero = gamePlayer.getHero();
        final PlayerInventory inventory = player.getInventory();

        final ItemStack ultimateItem = getItemFromTalent(hero.getUltimate());
        final ItemStack passiveItem = getItemFromTalent(hero.getPassiveTalent());

        // Design changes if debug is enabled
        if (Settings.SEE_DEBUG_DATA.isEnabled(player)) {
            final EntityAttributes attributes = gamePlayer.getAttributes();
            final Attributes baseAttributes = attributes.getBaseAttributes();

            // Attributes
            final ItemBuilder baseBuilder = ItemBuilder.of(Material.COARSE_DIRT, "Base Attributes", "&8Debug").addLore();
            baseAttributes.forEach((type, value) -> baseBuilder.addLore(type.getFormatted(baseAttributes)));

            final ItemBuilder playerBuilder = ItemBuilder.of(Material.DIRT, "Player Attributes", "&8Debug").addLore();
            attributes.forEach((type, value) -> playerBuilder.addLore(type.getFormatted(attributes)));

            // Tempers
            // Set temper items
            final ItemBuilder temperBuilder = new ItemBuilder(Material.COMPARATOR).setName("Temper Data").addLore("&8Debug").addLore();
            if (!attributes.hasTempers()) {
                temperBuilder.addLore("&8Empty!");
            }
            else {
                attributes.forEachTempers(data -> {
                    temperBuilder.addLore("&a&l" + data.temper.name());
                    data.values.forEach((type, temper) -> {
                        temperBuilder.addLore(" " + type.toString());
                        temperBuilder.addLore(" %s for %s".formatted(temper.value, temper.toString()));
                    });
                });
            }

            inventory.setItem(19, baseBuilder.toItemStack());
            inventory.setItem(20, playerBuilder.toItemStack());
            inventory.setItem(21, temperBuilder.toItemStack());

            // Hero-related items
            inventory.setItem(23, ultimateItem);
            inventory.setItem(24, passiveItem);
        }
        else {
            inventory.setItem(21, ultimateItem);
            inventory.setItem(23, passiveItem);
        }
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

        builder.getLines().clear();
        builder.addLines("");

        // Trial
        if (profile.hasTrial()) {
            profile.getTrial().updateScoreboard(builder);
        }

        // Lobby
        else if (!manager.isGameInProgress()) {
            final CurrencyEntry currency = playerDatabase.currencyEntry;

            builder.addLine("&2🧑 &a&lYou, %s:".formatted(player.getName()));
            builder.addLines(
                    " &7ʀᴀɴᴋ: " + profile.getRank().getPrefixWithFallback(),
                    " &7ʜᴇʀᴏ: " + profile.getSelectedHeroString(),
                    " &7ᴄᴏɪɴs: " + Currency.COINS.getFormatted(player)
            );

            final long rubyCount = currency.get(Currency.RUBIES);
            if (rubyCount > 0) {
                final String rubyCountFormatted = Currency.RUBIES.getFormatted(player);
                builder.addLine((rubyCount == 1 ? " &7ʀᴜʙʏ: " : " &7ʀᴜʙɪᴇs: ") + rubyCountFormatted);
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
                        builder.addLine(" &6%s %s &f%s %s &c%.1f ❤".formatted(
                                alivePlayer.getHero().getNameSmallCaps(),
                                alivePlayer.getTeam().getFirstLetterCaps(),
                                alivePlayer.getName(),
                                UIFormat.DIV,
                                alivePlayer.getHealth()
                        ));
                    }
                }
                // In Game
                else {
                    // Per mode lines
                    game.getMode().formatScoreboard(builder, (GameInstance) game, gamePlayer);
                }
            }
        }

        builder.addLine("");
        builder.updateLines();
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

    protected String getTimeLeftString(IGameInstance game) {
        return manager.isDebug() ? "∞" : new SimpleDateFormat("mm:ss").format(game.getTimeLeftRaw());
    }

    private void updateTablist() {
        tablist.update();
    }

    private ItemStack getItemFromTalent(Talent talent) {
        return talent != null ? talent.getItem() : new ItemStack(Material.AIR);
    }

    private void animateScoreboard() {
    }

    private StringBuilder buildGameFooter() {
        final StringBuilder builder = new StringBuilder();

        // Display active effects
        builder.append("\n&e&lᴀᴄᴛɪᴠᴇ ᴇғғᴇᴄᴛs:\n");
        final GamePlayer gp = GamePlayer.getExistingPlayer(this.player);
        if (gp == null || gp.getActiveEffects().isEmpty()) {
            builder.append("&8None!");
        }
        else {
            // {Positive}{Name} - {Time}
            final IntInt i = new IntInt(0);

            gp.getActiveEffects().forEach((type, active) -> {
                final Effect effect = type.getEffect();

                builder.append(effect.getType().getColor());
                builder.append(effect.getName());
                builder.append(" &f- ");

                if (active.isInfiniteDuration()) {
                    builder.append("∞");
                }
                else {
                    builder.append(new SimpleDateFormat("mm:ss").format(active.getRemainingTicks() * 50));
                }

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

        if (manager.isGameInProgress()) {
            footer.append(buildGameFooter());
        }

        // Display NBS player if playing a song
        final SongPlayer songPlayer = Eterna.getRegistry().songPlayer;

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

        // Seasonal decoration
        footer.append(Season.currentSeason());

        return new String[] {
                """
                                       
                        %s
                        &8Version %s
                        """.formatted(
                        CF.getName(),
                        CF.getVersionNoSnapshot()
                ),
                footer.toString()
        };
    }

}
