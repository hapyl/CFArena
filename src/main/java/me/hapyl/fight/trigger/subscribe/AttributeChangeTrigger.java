package me.hapyl.fight.trigger.subscribe;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.trigger.PlayerTrigger;
import org.bukkit.entity.Player;

public class AttributeChangeTrigger extends PlayerTrigger {

    /**
     * Type of the changed attribute.
     */
    public final AttributeType type;
    /**
     * Old value of the attribute.
     */
    public final double oldValue;
    /**
     * New value of the attribute.
     */
    public final double newValue;

    public AttributeChangeTrigger(Player player, AttributeType type, double oldValue, double newValue) {
        super(player);
        this.type = type;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public String toString() {
        return "AttributeChangeTrigger{" +
                "player=" + player +
                ", type=" + type +
                ", oldValue=" + oldValue +
                ", newValue=" + newValue +
                '}';
    }

}
