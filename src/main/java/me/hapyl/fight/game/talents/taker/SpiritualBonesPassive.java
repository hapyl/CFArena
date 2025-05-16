package me.hapyl.fight.game.talents.taker;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class SpiritualBonesPassive extends PassiveTalent {

    @DisplayField public final short maxBones = 6;
    @DisplayField public final short startBones = 1;
    
    @DisplayField(percentage = true) public final double damageAmplifierPerBone = 0.05;
    @DisplayField(percentage = true) public final double damageReductionPerBone = 0.03;
    @DisplayField(percentage = true) public final double healingPerBone = 0.03;

    public SpiritualBonesPassive(@Nonnull Key key) {
        super(key, "Spiritual Bones");

        setDescription("""
                You may possess up to &b{maxBones}&7 %s that orbit around you.
                
                Each bone provides the following effects:
                 &8├ &7Deal &c{damageAmplifierPerBone}&7 more damage.
                 &8├ &7Take &b{damageReductionPerBone}&7 less damage.
                 &8└ &7Heal for &c{healingPerBone} &c❤&7 of the damage dealt.
                """.formatted(Named.SPIRITUAL_BONES)
        );

        setMaterial(Material.BONE);
    }
    
    @Override
    public boolean isDisplayAttributes() {
        return true;
    }
}
