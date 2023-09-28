package me.hapyl.fight.game.profile;

import me.hapyl.fight.Main;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CosmeticEntry;
import me.hapyl.fight.database.entry.ExperienceEntry;
import me.hapyl.fight.database.rank.RankFormatter;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.PrefixCosmetic;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.experience.Experience;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class ProfileDisplay {

    private final PlayerProfile profile;
    private final CosmeticEntry cosmetics;

    private final Player player;
    private final long level;
    private final String customName;
    private final String prefix;
    private final RankFormatter rank;

    public ProfileDisplay(@Nonnull PlayerProfile profile) {
        this.profile = profile;
        this.player = profile.getPlayer();
        this.customName = player.getDisplayName();

        final PlayerDatabase database = profile.getDatabase();
        this.cosmetics = database.getCosmetics();
        this.level = database.getExperienceEntry().get(ExperienceEntry.Type.LEVEL);
        this.rank = database.getRank().getFormat();
        this.prefix = getPrefix();
    }

    @Override
    public String toString() {
        return getFormat() + customName;
    }

    @Nonnull
    public String getFormat() {
        final StringBuilder builder = new StringBuilder();
        final Experience experience = Main.getPlugin().getExperience();

        // Append level
        builder.append(experience.getExpPrefix(level)).append(" ");

        final GamePlayer gamePlayer = profile.getGamePlayer();

        // If gamePlayer is not null, it means player in game (Or in trial)
        if (gamePlayer != null) {
            if (gamePlayer.isDead()) {
                builder.append("&4☠☠☠ ");
            }
            else if (gamePlayer.isSpectator()) {
                builder.append(Color.SPECTATOR.bold()).append("🕶 ");
            }
            else {
                builder.append(ChatColor.GOLD).append(profile.getHero().getNameSmallCaps()).append(" ");
            }
        }

        // Append prefix if present
        if (!prefix.isEmpty()) {
            builder.append(prefix).append(" ");
        }

        final String rankPrefix = rank.prefix();

        if (!rankPrefix.isEmpty()) {
            builder.append(rankPrefix).append(" ");
        }

        builder.append(rank.nameColor());

        return Chat.format(builder);
    }

    public String getDisplayName() {
        return toString();
    }

    public String getDisplayNameTab() {
        return Chat.format("%s %s", this, formatPing());
    }

    @Nonnull
    public String getPrefixPreview(@Nonnull PrefixCosmetic prefix) {
        final RankFormatter format = profile.getRank().getFormat();

        return prefix.getPrefix() + " " + format.prefix() + format.nameColor() + " " + profile.getPlayer().getName();
    }

    @Nonnull
    public ChatColor getColor() {
        return rank.nameColor();
    }

    @Nonnull
    private String getPrefix() {
        final Cosmetics cosmetic = cosmetics.getSelected(Type.PREFIX);
        return cosmetic == null ? "" : ((PrefixCosmetic) cosmetic.getCosmetic()).getPrefix();
    }

    private String formatPing() {
        final int ping = profile.getPlayer().getPing();

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
