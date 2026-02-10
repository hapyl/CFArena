package me.hapyl.fight.game.talents.warden;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.game.talents.TalentType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class Paradox extends PassiveTalent {

    public Paradox(@NotNull Key key) {
        super(key, "Paradox");

        setDescription("""
                Your hits alternate between the statuses of &4&l"Reality" &7 and &5&l"Abyss."
                
                &4&l"REALITY": &7 Your hit will heal you for 10 HP.
                &5&l"ABYSS": &7 Your hit will reduce the enemy's %s by 15%%.
                """.formatted(AttributeType.ATTACK));
        setMaterial(Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE);
        setType(TalentType.IMPAIR);
    }
}
