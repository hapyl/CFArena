package me.hapyl.fight.game.heroes.warden;

import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.mastery.HeroMastery;
import me.hapyl.fight.game.heroes.mastery.HeroMasteryLevel;

import javax.annotation.Nonnull;

public class WardenMastery extends HeroMastery {
    public WardenMastery(Hero hero) {
        super(hero);

        setLevel(new WardenMastery.MasteryLevelRift(1));
        setLevel(new WardenMastery.MasteryLevelChaos(2));
        setLevel(new WardenMastery.MasteryLevelOblivion(3));
        setLevel(new WardenMastery.MasteryLevelHopeless(4));
        setLevel(new WardenMastery.MasteryLevelEnd(5));

    }

    @Nonnull
    @Override
    public Warden getHero() {
        return (Warden) super.getHero();
    }

    //TODO: Example masteries to use.
    private class MasteryLevelRift extends HeroMasteryLevel {

        public MasteryLevelRift(int level) {
            super(level, "Rift", """
                    TODO
                    """);
        }
    }

    private class MasteryLevelChaos extends HeroMasteryLevel {

        public MasteryLevelChaos(int level) {
            super(level, "Chaos", """
                       TODO
                       """);
        }
    }

    private class MasteryLevelOblivion extends HeroMasteryLevel {


        public MasteryLevelOblivion(int level) {
            super(level, "Oblivion", """
                       TODO
                       """);
        }
    }

    private class MasteryLevelHopeless extends HeroMasteryLevel {
        public MasteryLevelHopeless(int level) {
            super(level, "Hopeless", """
                       TODO
                       """);
        }
    }
    private class MasteryLevelEnd extends HeroMasteryLevel {
        public MasteryLevelEnd(int level) {
            super(level, "End of Worlds", """
                       TODO
                       """);
        }
    }
}