package me.hapyl.fight.game.talents.witcher;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.eterna.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class Igny extends Talent {

    @DisplayField private final double maximumDistance = 4.0d;

    @DisplayField private final double damageClosest = 5.0d;
    @DisplayField private final int fireDurationClosest = 60;
    @DisplayField private final double damageMedium = 3.5d;
    @DisplayField private final int fireTicksMedium = 40;
    @DisplayField private final double damageFurther = 2.0d;
    @DisplayField private final int fireTicksFurther = 20;

    public Igny(@Nonnull DatabaseKey key) {
        super(key, "Igni");

        setDescription("""
                Fire &cblazing spirits&7 in front of you that deal &cAoE damage&7 and set &cenemies&7 on &6fire&7.
                &8;;Damage and burning duration falls off with distance.
                """
        );

        setType(TalentType.DAMAGE);
        setItem(Material.BLAZE_POWDER);
        setCooldownSec(10);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        final Location targetLocation = location.add(player.getLocation().getDirection().multiply(3));

        Collect.nearbyEntities(targetLocation, maximumDistance).forEach(target -> {
            if (player.isSelfOrTeammate(target)) {
                return;
            }

            final double distance = targetLocation.distance(target.getLocation());

            if (isBetween(distance, 0, 1)) {
                target.damage(damageClosest, player, EnumDamageCause.ENTITY_ATTACK);
                target.setFireTicks(fireDurationClosest);
            }
            else if (isBetween(distance, 1, 2.5)) {
                target.damage(damageMedium, player, EnumDamageCause.ENTITY_ATTACK);
                target.setFireTicks(fireTicksMedium);
            }
            else if (isBetween(distance, 2.5, 4.1d)) {
                target.damage(damageFurther, player, EnumDamageCause.ENTITY_ATTACK);
                target.setFireTicks(fireTicksFurther);
            }
        });

        // fx
        PlayerLib.spawnParticle(targetLocation, Particle.FLAME, 20, 2.0, 0.5, 2.0, 0.01f);
        PlayerLib.playSound(targetLocation, Sound.ITEM_FLINTANDSTEEL_USE, 0.0f);
        PlayerLib.playSound(targetLocation, Sound.ITEM_FIRECHARGE_USE, 0.0f);

        return Response.OK;
    }

    private boolean isBetween(double a, double min, double max) {
        return a >= min && a < max;
    }

}
