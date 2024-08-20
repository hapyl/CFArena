package me.hapyl.fight.game.talents.healer;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.talents.PassiveTalent;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class RevivePassive extends PassiveTalent {
    public RevivePassive(@Nonnull DatabaseKey key) {
        super(key, "Revive");

        setDescription("""
                When taking lethal damage, instead of dying, become a ghost and seek placed &bRevive Catalyst&7 to revive yourself. Once you use &bRevive Catalyst&7, it will be destroyed. All your catalysts will be highlighted for enemy players.
                """
        );

        setItem(Material.GHAST_TEAR);
    }
}
