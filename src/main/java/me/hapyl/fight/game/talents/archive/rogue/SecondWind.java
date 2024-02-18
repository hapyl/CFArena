package me.hapyl.fight.game.talents.archive.rogue;

import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

public class SecondWind extends PassiveTalent {

    @DisplayField(percentage = true) public final double passiveHealing = 2.0d;

    public SecondWind() {
        super("Second Wind", """
                        When taking &4lethal damage&7, instead of dying, gain %s for short duration.
                                    
                        &6%s
                        • Increases %s.
                        • Decreases %s.
                        • Creates a &eshield&7.
                                    
                        If the &eshield&7 &cbreaks&7 before duration ends, you &cdie&7.
                        
                        If the &eshield&7 has &nnot&7 expired after the duration ends, convert &b{passiveHealing}&7 of remaining &eshield&7 into &chealing&7.
                        """.formatted(Named.SECOND_WIND, Named.SECOND_WIND.getName(), AttributeType.ATTACK, AttributeType.COOLDOWN_MODIFIER),
                Material.TOTEM_OF_UNDYING
        );

        setType(Type.ENHANCE);
    }
}
