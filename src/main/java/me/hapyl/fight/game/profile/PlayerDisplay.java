package me.hapyl.fight.game.profile;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.util.Bitmask;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CosmeticEntry;
import me.hapyl.fight.database.entry.ExperienceEntry;
import me.hapyl.fight.database.rank.RankFormatter;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.cosmetic.prefix.PrefixCosmetic;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.experience.Experience;
import me.hapyl.fight.game.heroes.Hero;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerDisplay {

    /**
     * Include player level.
     */
    public static final byte LEVEL = 0x1;
    
    /**
     * Include in game stuff, like a selected hero, status, etc.
     */
    public static final byte IN_GAME = 0x2;

    /**
     * Include status (cosmetic).
     */
    public static final byte STATUS = 0x4;

    /**
     * Include rank prefix.
     */
    public static final byte PREFIX = 0x8;

    /**
     * Include name.
     */
    public static final byte NAME = 0x10;

    public static final byte DEFAULT_BITMASK = LEVEL | IN_GAME | STATUS | PREFIX | NAME;

    public static final byte LOBBY_BITMASK = DEFAULT_BITMASK & ~IN_GAME & ~NAME;

    private final PlayerDatabase database;
    private final CosmeticEntry cosmeticEntry;
    private final long level;
    private final String prefix;
    private final RankFormatter rank;

    public PlayerDisplay(@Nonnull PlayerDatabase database) {
        this.database = database;
        this.cosmeticEntry = database.cosmeticEntry;
        this.level = database.experienceEntry.get(ExperienceEntry.Type.LEVEL);
        this.rank = database.getRank().getFormat();
        this.prefix = getStatus();
    }

    @Nonnull
    public String toString(@Nonnull byte... bits) {
        final byte bitmask = Bitmask.makeMask(bits);

        final StringBuilder builder = new StringBuilder();
        final Experience experience = Main.getPlugin().getExperience();

        // Append level
        if (Bitmask.isMasked(bitmask, LEVEL)) {
            builder.append(experience.getExpPrefix(level)).append(" ");
        }

        final GamePlayer gamePlayer = CF.getPlayer(database.getPlayer());

        // If gamePlayer is not null, it means player in game (Or in trial)
        if (gamePlayer != null && Bitmask.isMasked(bitmask, IN_GAME)) {
            if (gamePlayer.isDead()) {
                builder.append("&4☠☠☠ ");
            }
            else if (gamePlayer.isSpectator()) {
                builder.append(Color.SPECTATOR.bold()).append("🕶 ");
            }
            else {
                final Hero selectedHero = database.heroEntry.getSelectedHero();

                builder.append(ChatColor.GOLD).append(selectedHero.getNameSmallCaps()).append(" ");
            }
        }

        // Append prefix if present
        if (!prefix.isEmpty() && Bitmask.isMasked(bitmask, STATUS)) {
            builder.append(prefix).append(" ");
        }

        final String rankPrefix = rank.prefix();

        if (!rankPrefix.isEmpty() && Bitmask.isMasked(bitmask, PREFIX)) {
            builder.append(rankPrefix).append(" ");
        }

        builder.append(rank.nameColor());

        if (Bitmask.isMasked(bitmask, NAME)) {
            builder.append(database.getPlayerName());
        }

        return Chat.format(builder);
    }

    @Nonnull
    public String toString() {
        return getNamePrefixed();
    }

    @Nonnull
    public String toStringTab() {
        return Chat.format("%s %s".formatted(toString((byte) (LEVEL | STATUS | PREFIX | NAME)), formatPing()));
    }

    @Nonnull
    public String getPrefixPreview(@Nonnull PrefixCosmetic prefix) {
        final RankFormatter format = database.getRank().getFormat();
        final String playerName = database.getPlayerName();

        return prefix.getPrefix() + " " + format.prefix() + format.nameColor() + " " + playerName;
    }

    @Nonnull
    public Color getColor() {
        return rank.nameColor();
    }

    public void resetNick() {
        setNick(null);
    }

    public void setNick(@Nullable String newNick) {
        // TODO (hapyl): 001, Mar 1:
    }

    @Nonnull
    public String getNamePrefixed() {
        final String prefix = rank.prefix();

        return (!prefix.isEmpty() ? prefix + " " : "") + rank.nameColor() + database.getPlayerName();
    }

    private void updateName() {
        //final GameProfile gameProfile = Reflect.getGameProfile(player);

        //gameProfile.getName();
        // todo -> Idk might need to actually change players profile name
    }

    @Nonnull
    private String getStatus() {
        final Cosmetic cosmetic = cosmeticEntry.getSelected(Type.PREFIX);

        return cosmetic != null ? ((PrefixCosmetic) cosmetic).getPrefix() : "";
    }

    private String formatPing() {
        final Player player = database.getOnlinePlayer();
        final int ping = player != null ? player.getPing() : 0;

        // Loading or localhost
        if (ping == 0) {
            return "&e🔃";
        }

        if (ping <= 100) {
            return "&a" + ping + "ms";
        }
        else if (ping <= 150) {
            return "&e" + ping + "ms";
        }
        else if (ping <= 200) {
            return "&c" + ping + "ms";
        }
        else {
            return "&4" + ping + "ms";
        }
    }

}
