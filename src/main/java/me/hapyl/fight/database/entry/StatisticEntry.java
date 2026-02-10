package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.stats.StatContainer;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.game.talents.Talent;

import javax.annotation.Nonnull;

public class StatisticEntry extends PlayerDatabaseEntry {

    public StatisticEntry(@Nonnull PlayerDatabase playerDatabase) {
        super(playerDatabase, "statistic");
    }

    public void setFromPlayerStatistic(@Nonnull Hero hero, @Nonnull StatContainer stat) {
        stat.nonNegativeValuesMapped().forEach((statType, value) -> {
            addStat(statType, value);
            addHeroStat(hero, statType, value);
        });

        addStat(StatType.PLAYED, 1);
        addHeroStat(hero, StatType.PLAYED, 1);

        if (stat.isWinner()) {
            addStat(StatType.WINS, 1);
            addHeroStat(hero, StatType.WINS, 1);
        }

        stat.getUsedAbilities().forEach((talent, value) -> addAbilityUsage(hero, talent, value));
    }

    public long getAbilityUsage(@Nonnull Hero hero, @Nonnull Talent talent) {
        return getValue("hero_stats.%s.ability_usage.%s".formatted(hero.getKeyAsString(), talent.getKeyAsString()), 0L);
    }

    public void setAbilityUsage(@Nonnull Hero hero, @Nonnull Talent talent, long value) {
        setValue("hero_stats.%s.ability_usage.%s".formatted(hero.getKeyAsString(), talent.getKeyAsString()), value);
    }

    public void addAbilityUsage(Hero hero, Talent talent, long value) {
        setAbilityUsage(hero, talent, getAbilityUsage(hero, talent) + value);
    }

    public double getHeroStat(@Nonnull Hero hero, @Nonnull StatType type) {
        return getValue("hero_stats.%s.%s".formatted(hero.getKeyAsString(), type.getKeyAsString()), 0.0d);
    }

    public void setHeroStat(@Nonnull Hero hero, @Nonnull StatType type, double value) {
        setValue("hero_stats.%s.%s".formatted(hero.getKeyAsString(), type.getKeyAsString()), value);
    }

    public void addHeroStat(@Nonnull Hero heroes, StatType type, double value) {
        setHeroStat(heroes, type, getHeroStat(heroes, type) + value);
    }

    public double getStat(@Nonnull StatType type) {
        return getValue(type.getKeyAsString(), 0.0d);
    }

    public void setStat(@Nonnull StatType type, double value) {
        setValue(type.getKeyAsString(), value);
    }

    public void addStat(@Nonnull StatType type, double value) {
        setStat(type, getStat(type) + value);
    }

}
