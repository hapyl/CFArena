package me.hapyl.fight.game.heroes.archer;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.mastery.HeroMastery;
import me.hapyl.fight.game.heroes.mastery.HeroMasteryLevel;
import me.hapyl.fight.game.heroes.mastery.NumberProvider;
import me.hapyl.fight.terminology.EnumTerm;
import me.hapyl.fight.util.displayfield.DisplayField;

import javax.annotation.Nonnull;

public class ArcherMastery extends HeroMastery {
    public ArcherMastery(Archer hero) {
        super(hero);

        setLevels(
                MasteryLevelLucky::new,
                MasteryLevelFasterTripleShot::new,
                MasteryLevelShockingPower::new,
                MasteryLevelExtraTripleShotArrows::new,
                MasteryLevelFusion::new
        );
    }

    @Nonnull
    @Override
    public Archer hero() {
        return (Archer) super.hero();
    }

    public double getPassiveChance(@Nonnull GamePlayer player, double base) {
        return base + unlockedLevels(player).getDouble(MasteryLevelLucky.class);
    }

    public double getTripleShotArrowSpeed(@Nonnull GamePlayer player, double base) {
        return base + unlockedLevels(player).getDouble(MasteryLevelFasterTripleShot.class);
    }

    public int getMaxFuse(@Nonnull GamePlayer player, int base) {
        return base + unlockedLevels(player).getShort(MasteryLevelFusion.class);
    }

    public int getShockDartChargingSpeed(@Nonnull GamePlayer player, int base) {
        return (int) (base * unlockedLevels(player).getOrDefault(MasteryLevelShockingPower.class, m -> m.chargingSpeed, 1.0));
    }

    public double getShockDartRadius(@Nonnull GamePlayer player, double base) {
        return base + unlockedLevels(player).getDouble(MasteryLevelShockingPower.class, m -> m.radiusIncrease);
    }

    public int getTripleShotArrowCount(@Nonnull GamePlayer player, int base) {
        return base + unlockedLevels(player).getInteger(MasteryLevelExtraTripleShotArrows.class);
    }

    private class MasteryLevelLucky extends HeroMasteryLevel implements NumberProvider<Double> {

        @DisplayField(percentage = true)
        private final double chanceIncrease = 0.1d;

        public MasteryLevelLucky(int level) {
            super(
                    level, "Lucky!", """
                            Increases the %s of &a%s&7 to activate by &b{chanceIncrease}&7.
                            """.formatted(EnumTerm.BASE_CHANCE, hero().getPassiveTalent().getName())
            );
        }

        @Nonnull
        @Override
        public Double provideNumber() {
            return chanceIncrease;
        }
    }

    private class MasteryLevelFusion extends HeroMasteryLevel implements NumberProvider<Short> {

        @DisplayField
        private final short fuseIncrease = 30;

        public MasteryLevelFusion(int level) {
            super(
                    level, "Fusion", """
                            Increases the maximum &6&l%s&7 fuse&7 by &b{fuseIncrease}&7.
                            """.formatted(hero().boomBow.getName())
            );
        }

        @Nonnull
        @Override
        public Short provideNumber() {
            return fuseIncrease;
        }
    }

    private class MasteryLevelExtraTripleShotArrows extends HeroMasteryLevel implements NumberProvider<Integer> {
        @DisplayField
        private final short extraArrow = 2;

        public MasteryLevelExtraTripleShotArrows(int level) {
            super(
                    level, "Hurricane", """
                            Increases the number of arrows shot from &a%s&7 by &b{extraArrow}&7.
                            """.formatted(hero().getFirstTalent().getName())
            );
        }

        @Nonnull
        @Override
        public Integer provideNumber() {
            return (int) extraArrow;
        }
    }

    private class MasteryLevelShockingPower extends HeroMasteryLevel {

        @DisplayField
        private final double radiusIncrease = 1.5d;

        @DisplayField(scaleFactor = 25, suffix = "%", suffixSpace = false)
        private final double chargingSpeed = 0.8d;

        public MasteryLevelShockingPower(int level) {
            super(
                    level, "Shocking Power", """
                            Increase the &aradius&7 and &bcharging speed&7 of &a%s&7 by &b{radiusIncrease}&7 and &b{chargingSpeed}&7 respectively.
                            """.formatted(hero().getSecondTalent().getName())
            );
        }
    }

    private class MasteryLevelFasterTripleShot extends HeroMasteryLevel implements NumberProvider<Double> {

        @DisplayField
        private final double speedIncrease = 1.15d;

        public MasteryLevelFasterTripleShot(int level) {
            super(
                    level, "Tailwind", """
                            Increases the &barrow speed&7 of &a%s&7.
                            """.formatted(hero().getFirstTalent().getName())
            );
        }

        @Nonnull
        @Override
        public Double provideNumber() {
            return speedIncrease;
        }
    }
}
