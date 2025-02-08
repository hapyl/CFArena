package me.hapyl.fight.game.heroes.archer;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.mastery.HeroMastery;
import me.hapyl.fight.game.heroes.mastery.HeroMasteryLevel;
import me.hapyl.fight.game.heroes.mastery.NumberProvider;
import me.hapyl.fight.terminology.Terms;
import me.hapyl.fight.util.displayfield.DisplayField;

import javax.annotation.Nonnull;

public class ArcherMastery extends HeroMastery {
    public ArcherMastery(Archer hero) {
        super(hero);

        setLevels(
                new MasteryLevelLucky(1),
                new MasteryLevelLucky(1),
                new MasteryLevelLucky(1),
                new MasteryLevelExtraTripleShotArrows(2),
                new MasteryLevelFusion(5)
        );
    }

    @Nonnull
    @Override
    public Archer hero() {
        return (Archer) super.hero();
    }

    public double getPassiveChance(@Nonnull GamePlayer player) {
        final double base = hero().getPassiveTalent().chance;

        return base + levels(player).getDouble(MasteryLevelLucky.class);
    }

    public float getMaxFuse(@Nonnull GamePlayer player) {
        final float base = hero().getUltimate().baseFuse;

        return base + levels(player).getFloat(MasteryLevelFusion.class);
    }

    public int getTripleShotArrowCount(@Nonnull GamePlayer player) {
        final short base = hero().getFirstTalent().arrowCount();

        return base + levels(player).getInt(MasteryLevelExtraTripleShotArrows.class);
    }

    private class MasteryLevelLucky extends HeroMasteryLevel implements NumberProvider<Double> {

        @DisplayField(percentage = true)
        private final double chanceIncrease = 0.1d;

        public MasteryLevelLucky(int level) {
            super(
                    level, "Lucky!", """
                            Increases the %s of the %s to activate by &b{chanceIncrease}&7.
                            """.formatted(Terms.BASE_CHANCE, hero().getPassiveTalent().getName())
            );
        }

        @Nonnull
        @Override
        public Double getNumber() {
            return chanceIncrease;
        }
    }

    private class MasteryLevelFusion extends HeroMasteryLevel implements NumberProvider<Float> {

        @DisplayField
        private final float fuseIncrease = 30;

        public MasteryLevelFusion(int level) {
            super(
                    level, "Fusion", """
                            Increases the maximum &6&l%s &6fuse&7 by &b{fuseIncrease}&7.
                            """.formatted(hero().boomBow.getName())
            );
        }

        @Nonnull
        @Override
        public Float getNumber() {
            return fuseIncrease;
        }
    }

    private class MasteryLevelExtraTripleShotArrows extends HeroMasteryLevel implements NumberProvider<Integer> {
        @DisplayField
        private final int extraArrow = 2;

        public MasteryLevelExtraTripleShotArrows(int level) {
            super(
                    level, "Hurricane", """
                            Increases the number of arrows shot from &a%s&7 by &b{extraArrow}&7.
                            """.formatted(hero().getFirstTalent().getName())
            );
        }

        @Nonnull
        @Override
        public Integer getNumber() {
            return extraArrow;
        }
    }

}
