package me.hapyl.fight.game.talents.spark;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.talents.PassiveTalent;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class FireGuyPassive extends PassiveTalent {
    public FireGuyPassive(@Nonnull DatabaseKey key) {
        super(key, "Fire Guy");

        setDescription("""
                You're completely immune to &clava &7and &cfire &7damage.
                """
        );

        setItem(Material.LAVA_BUCKET);
    }
}
