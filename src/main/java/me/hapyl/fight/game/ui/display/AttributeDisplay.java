package me.hapyl.fight.game.ui.display;

import me.hapyl.fight.game.attribute.AttributeType;
import org.bukkit.Location;

public class AttributeDisplay {

    // A little cheat but whatever
    public AttributeDisplay(final AttributeType type, boolean isBuff, Location location) {
        if (isBuff) {
            new BuffDisplay("&a↑ %s &aIncrease".formatted(type), 30).display(location);
            return;
        }

        new DebuffDisplay("&c↓ %s &cDecrease".formatted(type), 30).display(location);
    }

}
