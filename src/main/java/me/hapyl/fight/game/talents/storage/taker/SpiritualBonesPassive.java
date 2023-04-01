package me.hapyl.fight.game.talents.storage.taker;

import me.hapyl.fight.game.talents.PassiveTalent;
import org.bukkit.Material;

public class SpiritualBonesPassive extends PassiveTalent {

    public final int MAX_BONES = 5;
    public final double DAMAGE_AMPLIFIER_PER_BONE = 2.0d;
    public final double DAMAGE_REDUCE_PER_BONE = 3.0d;
    public final double HEALING_PER_BONE = 3.0d;

    public SpiritualBonesPassive() {
        super("Spiritual Bones", Material.BONE);

        setDescription(
                "You may posses up to &b%s&7 &eSpiritual Bones&7 that orbit around you.__&l&oEach &7bone provides the following effects:",
                MAX_BONES
        );

        addDescription("__");
        addDescription("&b• &7Deal &c&l%s%%&7 more damage &oper bone&7.", DAMAGE_AMPLIFIER_PER_BONE);
        addDescription("&b• &7Take &b&l%s%%&7 less damage &oper bone&7.", DAMAGE_REDUCE_PER_BONE);
        addDescription("&b• &7Heal for &c&l%s%% &c❤&7 &oper bone&7 of the damage dealt.", HEALING_PER_BONE);

        addDescription("__Some abilities might cost bones to use.");
    }
}
