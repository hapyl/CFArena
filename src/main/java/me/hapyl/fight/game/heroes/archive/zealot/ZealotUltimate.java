package me.hapyl.fight.game.heroes.archive.zealot;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;

public class ZealotUltimate extends UltimateTalent {

    @DisplayField public final int swingCooldown = 40;
    @DisplayField(percentage = true) public final double defenseDecrease = 0.5;
    @DisplayField(percentage = true) public final double ferocityIncrease = 2.5d;
    @DisplayField public final double psionicBladeDamage = 5;

    public ZealotUltimate() {
        super("Blade Barrage", """
                Command &ntwo&7 &egiant swords&7 to appear and &bfollow&7 you for {duration}.
                                
                Swinging &nyour&7 katana will also &nswing&7 the &egiant swords&7.
                &8;;The swords swing in turns.
                                
                &bPsionic Blade
                Decreases &cenemies %s &7and damages them.
                                
                &eSoul Blade
                Increases your %s by &c{ferocityIncrease}&7 and deals &cdamage&7 to enemies.
                """.formatted(AttributeType.DEFENSE, AttributeType.FEROCITY), 70);

        setType(Talent.Type.ENHANCE);
        setItem(Material.GOLDEN_SWORD);
        setDurationSec(12);

        setSound(Sound.ENTITY_HORSE_DEATH, 0.0f);
    }
}
