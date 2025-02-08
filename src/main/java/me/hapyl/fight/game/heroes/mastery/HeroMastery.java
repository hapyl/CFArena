package me.hapyl.fight.game.heroes.mastery;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.eterna.module.util.MapWrap;
import me.hapyl.fight.annotate.MapGuide;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.stats.StatContainer;
import me.hapyl.fight.game.stats.StatType;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.*;

public class HeroMastery implements Iterable<HeroMasteryLevel> {

    public static final int MAX_LEVEL;
    public static final int UNFAIR_LEVEL;

    public static final long EXP_WIN;
    public static final long EXP_ELIMINATION;

    public static final String PREFIX;

    @MapGuide(key = "level", value = "experience")
    private static final TreeMap<Integer, Long> EXP_MAP;
    private static final Map<Integer, LevelDisplay> MASTERY_STRING_MAP;

    static {
        MAX_LEVEL = 5;
        UNFAIR_LEVEL = -1;

        EXP_WIN = 10;
        EXP_ELIMINATION = 1;

        PREFIX = (Color.GOLD.bold() + "\uD83C\uDFC5 "
                + Color.DARK_ORANGE.bold() + "MASTERY!"
                + Color.GOLD.bold() + " \uD83C\uDFC5 ")
                + Color.GRAY;

        // Assign exp
        EXP_MAP = new TreeMap<>();

        long exp = 100L;
        for (int i = 1; i <= MAX_LEVEL; i++) {
            EXP_MAP.put(i, exp);

            exp += (int) (i * (Math.pow(i, 2) + 250));
            exp -= exp % 50;
        }

        // Mastery Strings
        MASTERY_STRING_MAP = Map.ofEntries(
                Map.entry(0, new LevelDisplay("&8", "Rookie")),
                Map.entry(1, new LevelDisplay("&a", "I, Noob")),
                Map.entry(2, new LevelDisplay("&e", "II, Not Pro")),
                Map.entry(3, new LevelDisplay("&6", "III, Almost Pro")),
                Map.entry(4, new LevelDisplay("&c", "IV, Pro")),
                Map.entry(5, new LevelDisplay("&4", "V, Master"))
        );
    }

    private final Hero hero;
    private final HeroMasteryLevel[] levels;

    public HeroMastery(Hero hero) {
        this.hero = hero;
        this.levels = new HeroMasteryLevel[MAX_LEVEL];

        for (int i = 0; i < this.levels.length; i++) {
            this.levels[i] = new HeroMasteryLevel(i + 1, "Does Nothing", "Does Nothing");
        }
    }

    @Nonnull
    public Hero getHero() {
        return this.hero;
    }

    @Nonnull
    @Override
    public Iterator<HeroMasteryLevel> iterator() {
        return List.of(this.levels).iterator();
    }

    protected List<HeroMasteryLevel> unlockedLevels(GamePlayer player) {
        final int level = getLevel(player);

        return Arrays.asList(this.levels).subList(0, level);
    }

    protected void setLevel(@Nonnull HeroMasteryLevel masteryLevel) {
        this.levels[masteryLevel.getLevel() - 1] = masteryLevel;
    }

    protected int getLevel(@Nonnull GamePlayer player) {
        final GameInstance gameInstance = Manager.current().getGameInstance();

        return gameInstance != null ? gameInstance.heroMastery().getMastery(player) : 0;
    }

    /**
     * Gets a {@link LevelGetter} for the given player.
     *
     * @param player - Player.
     * @return a level getter.
     */
    protected LevelGetter levels(@Nonnull GamePlayer player) {
        return new LevelGetter(player, this);
    }

    @Nonnull
    public static String getLevelString(int level) {
        return getLevelDisplay(level).toString();
    }

    @Nonnull
    public static LevelDisplay getLevelDisplay(int level) {
        final LevelDisplay levelDisplay = MASTERY_STRING_MAP.get(level);

        if (levelDisplay != null) {
            return levelDisplay;
        }

        throw new IllegalArgumentException("Illegal level: " + level);
    }

    public static int getLevel(long experience) {
        for (Integer lvl : EXP_MAP.descendingKeySet()) {
            final Long exp = EXP_MAP.get(lvl);

            if (experience >= exp) {
                return lvl;
            }
        }

        return 0;
    }

    public static void awardPlayer(@Nonnull GamePlayer player) {
        final StatContainer stats = player.getStats();
        long earnedExp = 0;

        if (stats.isWinner()) {
            earnedExp += EXP_WIN;
        }

        earnedExp += (long) (stats.getValue(StatType.KILLS) * EXP_ELIMINATION);

        if (earnedExp <= 0) {
            return;
        }

        final Hero playerHero = player.getHero();

        player.sendMessage(PREFIX + "&aEarned %s &6Mastery Exp&a for %s!".formatted(earnedExp, playerHero.getNameSmallCaps()));
    }

    public static long getExpRequiredForLevel(int level) {
        if (level < 1) {
            return 0; // for progress calculation
        }

        final Long exp = EXP_MAP.get(level);

        return exp != null ? exp : Long.MAX_VALUE; // allow exp overflow
    }

    public static void dumpExpMap(Player player) {
        Chat.sendMessage(
                player, CollectionUtils.wrapToString(
                        EXP_MAP, new MapWrap<>() {
                            @Override
                            public String keyToValue(Integer key, Long value) {
                                return key + "=" + value;
                            }

                            @Override
                            public String start() {
                                return "{";
                            }

                            @Override
                            public String between() {
                                return ", ";
                            }

                            @Override
                            public String end() {
                                return "}";
                            }
                        }
                )
        );
    }

    public record LevelDisplay(String color, String string) {
        @Override
        public String toString() {
            return color + string;
        }
    }
}
