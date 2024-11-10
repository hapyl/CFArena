package me.hapyl.fight.game.talents.troll;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.talents.PassiveTalent;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class LastLaughPassive extends PassiveTalent {
    public LastLaughPassive(@Nonnull Key key) {
        super(key, "Last Laugh");

        setDescription("""
                Your hits have &b0.1%% &7chance to instantly kill enemy.
                """
        );

        setItem(Material.BLAZE_POWDER);
    }
}
