package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.cosmetic.crate.Crates;
import me.hapyl.fight.game.reward.DailyReward;
import me.hapyl.fight.gui.styled.StyledTexture;

import javax.annotation.Nonnull;

public class DailyRewardEntry extends PlayerDatabaseEntry {
    public DailyRewardEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
    }

    public boolean canClaimAny() {
        final PlayerRank playerRank = getDatabase().getRank();

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
        return getValue("reward.daily.last_" + type.name(), 0L);
    }

    public int getStreak(@Nonnull Type type) {
        return getValue("reward.daily.streak_" + type.name(), 0);
    }

    public void increaseStreak(@Nonnull Type type) {
        final int value = getStreak(type) + 1;

        setValue("reward.daily.streak_" + type.name(), value);
    }

    public void setLastDaily(@Nonnull Type type, long l) {
        setValue("reward.daily.last_" + type.name(), l);
    }

    public void markLastDailyReward(@Nonnull Type type) {
        setLastDaily(type, System.currentTimeMillis());
    }

    public boolean isBonusReward(@Nonnull Type type) {
        final int streak = getStreak(type);

        return streak > 0 && streak % type.bonus == 0;
    }

    public enum Type {
        DEFAULT(PlayerRank.DEFAULT, StyledTexture.CHEST, new DailyReward(1000, 10, 1).setCrate(Crates.COMMON, 1)),
        VIP(PlayerRank.VIP, StyledTexture.CHEST_EMERALD, new DailyReward(2500, 25, 1).setCrate(Crates.UNCOMMON, 1)),
        PREMIUM(PlayerRank.PREMIUM, StyledTexture.CHEST_DIAMOND, new DailyReward(5000, 50, 1).setCrate(Crates.RARE, 2));

        public final PlayerRank rank;
        public final StyledTexture texture;
        public final DailyReward reward;

        public final int bonus = 7;

        Type(PlayerRank rank, StyledTexture texture, DailyReward reward) {
            this.rank = rank;
            this.texture = texture;
            this.reward = reward;
            this.reward.setType(this);
        }

        @Override
        public String toString() {
            return rank.getPrefixWithFallback();
        }
    }
}
