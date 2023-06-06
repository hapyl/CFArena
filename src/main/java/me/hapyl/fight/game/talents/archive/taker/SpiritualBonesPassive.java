package me.hapyl.fight.game.talents.archive.taker;

import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

public class SpiritualBonesPassive extends PassiveTalent {

    @DisplayField public final int MAX_BONES = 5;
    @DisplayField public final double DAMAGE_AMPLIFIER_PER_BONE = 2.0d;
    @DisplayField public final double DAMAGE_REDUCE_PER_BONE = 3.0d;
    @DisplayField public final double HEALING_PER_BONE = 3.0d;

    public SpiritualBonesPassive() {
        super("Spiritual Bones", Material.BONE);

        setDescription("""
                You may posses up to &b{MAX_BONES}&7 &eSpiritual Bones&7 that orbit around you.
                                
                &l&oEach &7bone provides the following effects:
                                
                """);

        addDescription("""
                &b• &7Deal &c&l{DAMAGE_AMPLIFIER_PER_BONE}%% &7more damage &oper bone&7.
                &b• &7Take &b&l{DAMAGE_REDUCE_PER_BONE}%% &7less damage &oper bone&7.
                &b• &7Heal for &c&l{HEALING_PER_BONE}%% &c❤&7 &oper bone&7 of the damage dealt.
                """);
    }

}
