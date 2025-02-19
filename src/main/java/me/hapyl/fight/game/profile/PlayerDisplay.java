package me.hapyl.fight.game.profile;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CosmeticEntry;
import me.hapyl.fight.database.entry.ExperienceEntry;
import me.hapyl.fight.database.rank.RankFormatter;
import me.hapyl.fight.game.EntityState;
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

public class PlayerDisplay {

    private final PlayerDatabase database;
    private final CosmeticEntry cosmeticEntry;
    private final long level;
    private final String status;
    private final RankFormatter rank;

    public PlayerDisplay(@Nonnull PlayerDatabase database) {
        this.database = database;
        this.cosmeticEntry = database.cosmeticEntry;
        this.level = database.experienceEntry.get(ExperienceEntry.Type.LEVEL);
        this.rank = database.getRank().getFormat();
        this.status = getStatus();
    }

    /**
     * Constructs the display based on the given parts.
     *
     * @param parts - The parts.
     * @return the display.
     */
    @Nonnull
    public String toString(@Nonnull Part... parts) {
        final StringBuilder builder = new StringBuilder();

        // Append level
        if (Part.LEVEL.contains(parts)) {
            final Experience experience = CF.getPlugin().getExperience();

            builder.append(experience.getExpPrefix(level)).append(" ");
        }

        // If gamePlayer is not null, it means player in game (Or in trial)
        if (Part.IN_GAME.contains(parts)) {
            final GamePlayer gamePlayer = CF.getPlayer(database.getPlayer());

            if (gamePlayer != null) {
                final EntityState state = gamePlayer.getState();

                switch (state) {
                    case DEAD -> builder.append("&4☠☠☠ ");
                    case SPECTATOR -> builder.append(Color.SPECTATOR.bold()).append("🕶 ");
                    default -> {
                        final Hero selectedHero = database.heroEntry.getSelectedHero();

                        builder.append(ChatColor.GOLD).append(selectedHero.getNameSmallCaps()).append(" ");
                    }
                }
            }
        }

        // Append status if present
        if (!status.isEmpty() && Part.STATUS.contains(parts)) {
            builder.append(status).append(" ");
        }

        final String rankPrefix = rank.prefix();

        if (!rankPrefix.isEmpty() && Part.PREFIX.contains(parts)) {
            builder.append(rankPrefix).append(" ");
        }

        if (Part.NAME.contains(parts)) {
            builder.append(rank.nameColor());
            builder.append(database.getPlayerName());
        }

        return Chat.format(builder);
    }

    /**
     * Gets the default display of the player, including the player's rank and name.
     * @return the default display of the player, including the player's rank and name.
     */
    @Nonnull
    public String toString() {
        final String prefix = rank.prefix();

        return (!prefix.isEmpty() ? prefix + " " : "") + rank.nameColor() + database.getPlayerName();
    }

    @Nonnull
    public String toStringTab() {
        return Chat.format("%s %s".formatted(toString(Part.LEVEL, Part.STATUS, Part.PREFIX, Part.NAME), formatPing()));
    }

    @Nonnull
    public String getStatusPreview(@Nonnull PrefixCosmetic prefix) {
        final RankFormatter format = database.getRank().getFormat();
        final String playerName = database.getPlayerName();

        return prefix.getPrefix() + " " + format.prefix() + format.nameColor() + " " + playerName;
    }

    @Nonnull
    public Color getColor() {
        return rank.nameColor();
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

    public enum Part {
        LEVEL,
        IN_GAME,
        STATUS,
        PREFIX,
        NAME;

        public boolean contains(@Nonnull Part[] parts) {
            for (Part part : parts) {
                if (this == part) {
                    return true;
                }
            }

            return false;
        }
    }

}
