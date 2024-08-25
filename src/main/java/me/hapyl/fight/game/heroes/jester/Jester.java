package me.hapyl.fight.game.heroes.jester;


import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.registry.Key;

public class Jester extends Hero implements Disabled {

    /**
     * <ul>
     *     <li>Talent 1:
     *     <br>
     *     Place a box with music. When enemy nearby you can tp.
     *     Confetti fx, damage, -DEFENSE
     *
     *     <li>Talent 2:
     *     <br>
     *     Throw a cake that deals damage ( 1 ), blindness and slow.
     *
     *     <li>Passive:
     *     <br>
     *     On kill enemies do the same as the box.
     *
     *     <li>Ultimate ( silent, only teammates can see )
     *     <br>
     *     Joker * ( silent )
     *     Killer is always jester for any kills other than self or teammate
     *
     *     <li>Weapon
     *     <br>
     *     Staff, random damage ( 6-12 )
     * </ul>
     */
    public Jester(Key key) {
        super(key, "Jester");
    }

    @Override
    public Talent getFirstTalent() {
        return null;
    }

    @Override
    public Talent getSecondTalent() {
        return null;
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }
}
