package me.hapyl.fight.game.talents.shadow_assassin;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.talents.PassiveTalent;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class DarkCoverPassive extends PassiveTalent {
    public DarkCoverPassive(@Nonnull Key key) {
        super(key, "Dark Cover");

        setDescription("""
                As an assassin, you have mastered the ability to stay in the shadows.____While &e&lSNEAKING&7, you become completely invisible, but cannot deal damage and your footsteps are visible.
                """
        );

        setItem(Material.NETHERITE_CHESTPLATE);
    }
}
