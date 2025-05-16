package me.hapyl.fight.game.talents.techie;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.heroes.ultimate.EnumResource;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.game.talents.TalentType;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class NeuralTheftPassive extends PassiveTalent {
    public NeuralTheftPassive(@Nonnull Key key) {
        super(key, "Neural Theft");
        
        setDescription("""
                       At intervals, &3hack&7 all &fbugged&7 opponents, &bhighlight&7 them and send their data to you and your teammates.
                       
                       The data includes:
                        &8├&7 Enemy's current &blocation&7.
                        &8├&7 Enemy's &c❤ Health&7.
                        &8└&7 Enemy's %1$s.
                       
                       Also &4steal&7 a small amount of %1$s from each hacked enemy.
                       """.formatted(EnumResource.ENERGY)
        );
        
        setMaterial(Material.CHAINMAIL_HELMET);
        setType(TalentType.IMPAIR);
    }
}
