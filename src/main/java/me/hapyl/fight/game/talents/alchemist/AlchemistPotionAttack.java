package me.hapyl.fight.game.talents.alchemist;

import me.hapyl.fight.game.attribute.AttributeType;
import org.bukkit.Color;

public class AlchemistPotionAttack extends AlchemistAttributePotion {

    public AlchemistPotionAttack() {
        super("Potion of Strength", 25, Color.fromRGB(148, 16, 23), AttributeType.ATTACK, 1.0d);
    }
}
