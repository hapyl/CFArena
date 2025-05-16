package me.hapyl.fight.game.talents.shark;


import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SubmergeTalent extends Talent {

    @DisplayField protected final double damage = 10.0d;
    @DisplayField protected final double magnitude = 0.85d;
    @DisplayField protected final double range = 1.0d;

    protected final ModifierSource modifierSource = new ModifierSource(Key.ofString("submerge"), true);
    
    public SubmergeTalent(@Nonnull Key key) {
        super(key, "Submerge");

        setDescription("""
                Swiftly &bsubmerge&7 underground and &bdash forward&7, revealing a &cfierce &3shark fin&7 that deals &cdamage&7 and &eimpairs&7 hit &cenemies&7.
                """
        );

        setMaterial(Material.PRISMARINE_SHARD);
        setDuration(20);
        setCooldownSec(5);
    }

    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        new Submerge(this, player);
        return Response.OK;
    }

}
