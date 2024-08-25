package me.hapyl.fight.game.talents.ninja;


import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.registry.Key;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class NinjaStylePassive extends PassiveTalent {
    public NinjaStylePassive(@Nonnull Key key) {
        super(key, "Ninja Style");

        setDescription("""
                Ninjas are fast and agile.
                
                You gain %s &7boost, can &bdouble jump&7 and don't take &3fall&7 damage!
                """.formatted(AttributeType.SPEED)
        );

        setItem(Material.ELYTRA);
    }
}
