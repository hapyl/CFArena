package me.hapyl.fight.game.talents.alchemist;

import me.hapyl.fight.game.attribute.AttributeType;
import org.bukkit.Color;

public class AlchemistPotionSpeed extends AlchemistAttributePotion {

    public AlchemistPotionSpeed() {
        super("Potion of Speed", 15, Color.fromRGB(78, 160, 204), AttributeType.SPEED, 0.08d);
    }

}
