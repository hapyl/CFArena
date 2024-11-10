package me.hapyl.fight.database.entry;

import me.hapyl.eterna.module.registry.KeyedEnum;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.reward.DailyReward;
import me.hapyl.fight.gui.styled.StyledTexture;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class DailyRewardEntry extends PlayerDatabaseEntry {
    public DailyRewardEntry(@Nonnull PlayerDatabase playerDatabase) {
        super(playerDatabase, "reward");
    }

    public boolean canClaimAny() {
        for (Type type : Type.values()) {
            if (canClaim(type)) {
                return true;
            }
        }

        return false;
    }

    public boolean canClaim(@Nonnull Type type) {
        final PlayerRank rank = getDatabase().getRank();

        if (!rank.isOrHigher(rank)) {
            return false;
        }

        final long lastDaily = lastDaily(type);
        return lastDaily == 0L || System.currentTimeMillis() >= (lastDaily + DailyReward.MILLIS_WHOLE_DAY);
    }

    public long nextDaily(@Nonnull Type type) {
        return DailyReward.MILLIS_WHOLE_DAY - (System.currentTimeMillis() - lastDaily(type));
    }

    public long lastDaily(@Nonnull Type type) {
        return getValue("daily.last_" + type.getKeyAsString(), 0L);
    }

    public int getStreak(@Nonnull Type type) {
        return getValue("daily.streak_" + type.getKeyAsString(), 0);
    }

    public void increaseStreak(@Nonnull Type type) {
        final int value = getStreak(type) + 1;

        setValue("daily.streak_" + type.getKeyAsString(), value);
    }

    public void setLastDaily(@Nonnull Type type, long l) {
        setValue("daily.last_" + type.getKeyAsString(), l);
    }

    public void markLastDailyReward(@Nonnull Type type) {
        setLastDaily(type, System.currentTimeMillis());
    }

    public boolean isBonusReward(@Nonnull Type type) {
        final int streak = getStreak(type);

        return streak > 0 && streak % type.bonus == 0;
    }

    public enum Type implements KeyedEnum {
        DEFAULT(
                PlayerRank.DEFAULT,
                StyledTexture.CHEST,
                type -> new DailyReward(type, 1000, 10, 1)
        ),

        VIP(
                PlayerRank.VIP,
                StyledTexture.CHEST_EMERALD,
                type -> new DailyReward(type, 2500, 25, 1)
        ),

        PREMIUM(
                PlayerRank.PREMIUM,
                StyledTexture.CHEST_DIAMOND,
                type -> new DailyReward(type, 5000, 50, 1)
        );

        public final PlayerRank rank;
        public final StyledTexture texture;
        public final DailyReward reward;

        public final int bonus = 7;

        Type(PlayerRank rank, StyledTexture texture, Function<Type, DailyReward> fn) {
            this.rank = rank;
            this.texture = texture;
            this.reward = fn.apply(this);
        }

        @Override
        public String toString() {
            return rank.getPrefixWithFallback();
        }
    }
}
