package me.hapyl.fight.game.talents.alchemist;

import me.hapyl.fight.game.attribute.AttributeType;
import org.bukkit.Color;

public class AlchemistPotionDefense extends AlchemistAttributePotion {

    public AlchemistPotionDefense() {
        super("Potion of Defense", 20, Color.fromRGB(76, 105, 64), AttributeType.DEFENSE, 0.5d);
    }

}
