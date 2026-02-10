package me.hapyl.fight.game.talents.shadow_assassin;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShroudedStep extends Talent {

    @DisplayField private final short maxDistance = 100;
    @DisplayField private final int decoyDuration = 30;
    @DisplayField private final double decoyExplosionRadius = 1.5d;
    @DisplayField private final double decoyExplosionDamage = 10.0d;

    public ShroudedStep(@Nonnull Key key) {
        super(key, "Shrouded Step");

        setDescription("""
                While in Dark Cover, deploy a decoy footprints that travel in a straight line.
                
                Leave Dark Cover to create a decoy that explodes after being hit or after a short duration damaging nearby enemies.
                """
        );

        setCooldown(600);
        setMaterial(Material.NETHERITE_BOOTS);
    }

    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        if (!player.isSneaking()) {
            return Response.error("You must be in &lDark Cover &cto use this!");
        }

        return Response.deprecatedDoNoUseThis();
    }
}
