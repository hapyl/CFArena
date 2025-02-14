package me.hapyl.fight.game.talents.himari;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class DeadEye extends HimariTalent {

    private final char[] charge = {'ᚠ', 'ᛁ', 'ᚱ', 'ᛖ'};

    @DisplayField private final double baseDamage = 7;
    @DisplayField private final double maxDistance = 40;
    @DisplayField private final double defenseIgnore = 0.8d;

    private final TemperInstance temperInstance = Temper.DEAD_EYE.newInstance()
                                                                 .increase(AttributeType.DEFENSE_IGNORE, defenseIgnore)
                                                                 .increase(AttributeType.CRIT_CHANCE, 1.0d);


    public DeadEye(@Nonnull Key key) {
        super(key, "Dead Eye");

        setDescription("""
                &8;;This Talent is unlocked only if you roll it out from Lucky Day.
                
                Charges "Dead Eye" effect.
                If the target you're pointing at is still in your sight after the duration ends,
                You'll deal %s that will ignore 80 percent of victim's %s.
                """.formatted(AttributeType.CRIT_DAMAGE, AttributeType.DEFENSE));

        setItem(Material.SPECTRAL_ARROW);
        setType(TalentType.DAMAGE);
        setDurationSec(3);
    }

    @Nonnull
    @Override
    public Response executeHimari(@NotNull GamePlayer player) {
        final LivingGameEntity target = Collect.targetEntityRayCast(player, 40, 1.0d, player::isNotSelfOrTeammate);

        if (target == null) {
            return Response.error("No valid target for dead eye!");
        }

        new TickingGameTask() {
            @Override
            public void run(int tick) {
                if (tick >= getDuration()) {
                    deadShot();
                    cancel();
                    return;
                }

                // Check for line of sight
                if (!player.hasLineOfSight(target)) {
                    cancel();
                    return;
                }

                final Location location = player.getLocation();
                final Vector vector = target.getEyeLocation().toVector().subtract(player.getEyeLocation().toVector()).normalize();

                location.setDirection(vector);
                player.teleport(location);

                // Fx
            }

            private void deadShot() {
                temperInstance.temper(player, 5);
                target.damage(baseDamage, player, EnumDamageCause.DEAD_EYE);

                // Fx
                player.playWorldSound(Sound.ENTITY_ZOMBIE_INFECT, 0.34f);
            }

        }.runTaskTimer(0, 1);

        // Fx
        player.playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 2.0f);

        return Response.OK;
    }
}
