package me.hapyl.fight.game.entity.custom;

import me.hapyl.fight.game.attribute.Attributes;
import me.hapyl.fight.game.entity.GameEntityType;
import org.bukkit.entity.Warden;

public class WardenDefender extends GameEntityType<Warden> {
    public WardenDefender() {
        super("&b&lWarden Defender", Warden.class);

        final Attributes attributes = getAttributes();
        attributes.setHealth(500);
        attributes.setDefense(150);
        attributes.setAttack(200);
    }
}
