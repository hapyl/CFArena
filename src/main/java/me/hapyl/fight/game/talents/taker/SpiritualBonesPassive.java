package me.hapyl.fight.game.talents.taker;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class SpiritualBonesPassive extends PassiveTalent {

    @DisplayField public final short MAX_BONES = 6;
    @DisplayField public final short START_BONES = 1;
    @DisplayField public final double DAMAGE_AMPLIFIER_PER_BONE = 2.0d;
    @DisplayField public final double DAMAGE_REDUCE_PER_BONE = 3.0d;
    @DisplayField public final double HEALING_PER_BONE = 3.0d;

    public SpiritualBonesPassive(@Nonnull Key key) {
        super(key, "Spiritual Bones");

        setDescription("""
                You may possess up to &b{MAX_BONES}&7 %s that orbit around you.
                
                &nEach&7 bone provides the following effects:
                """.formatted(Named.SPIRITUAL_BONES)
        );

        addDescription("""
                &b└ &7Deal &c&l{DAMAGE_AMPLIFIER_PER_BONE}%% &7more damage &nper&7 bone.
                &b└ &7Take &b&l{DAMAGE_REDUCE_PER_BONE}%% &7less damage &nper&7 bone.
                &b└ &7Heal for &c&l{HEALING_PER_BONE}%% &c❤&7 &nper&7 bone of the damage dealt.
                
                &8;;You will start with {START_BONES} bone.
                """
        );

        setItem(Material.BONE);
    }

}
