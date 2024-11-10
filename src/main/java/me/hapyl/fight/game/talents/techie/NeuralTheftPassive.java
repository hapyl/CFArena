package me.hapyl.fight.game.talents.techie;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.game.talents.TalentType;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class NeuralTheftPassive extends PassiveTalent {
    public NeuralTheftPassive(@Nonnull Key key) {
        super(key, "Neural Theft");

        setDescription("""
                At &bintervals&7, &bhack&7 all &fbugged&7 opponents and send the data to &nyou&7 and your &nteammates&7.
                
                &oThe data includes:
                └ Enemy's &blocation&7.
                └ Enemy's &c❤ Health&7.
                └ Enemy's %1$s.
                
                Also, &4steal&7 a small amount of %1$s from each hacked enemy.
                """.formatted(Named.ENERGY)
        );

        setItem(Material.CHAINMAIL_HELMET);
        setType(TalentType.IMPAIR);
    }
}
