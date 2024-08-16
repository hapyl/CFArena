package me.hapyl.fight.game.talents.zealot;

import me.hapyl.fight.fx.beam.Quadrant;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class BrokenHeartRadiation extends Talent {

    @DisplayField private final double beamLength = 5;
    @DisplayField private final double beamDamage = 5.0d;

    @DisplayField(scaleFactor = 100.0d, suffix = "%", suffixSpace = false)
    private final double mendingReduction = 0.25d;
    @DisplayField(scaleFactor = 100.0d, suffix = "%", suffixSpace = false)
    private final double defenseReduction = 0.33d;

    @DisplayField private final int effectDuration = 180;

    private final TemperInstance temperInstance = Temper.RADIATION.newInstance()
            .decrease(AttributeType.VITALITY, mendingReduction)
            .decrease(AttributeType.DEFENSE, defenseReduction)
            .onApply(entity -> entity.spawnParticle(entity.getLocation(), Particle.ELDER_GUARDIAN, 1, 0, 0, 0, 0));

    public BrokenHeartRadiation() {
        super("Broken Heart Radiation");

        setDescription("""
                Create four radiation beams that spin around you for {duration}.
                                
                If a beam touches an enemy, it deals &c{beamDamage} ❤&7 damage and reduces %s by &c{mendingReduction}&7 and %s by &c{defenseReduction}&7 for &b{effectDuration}.
                """, AttributeType.VITALITY, AttributeType.DEFENSE);

        setType(TalentType.IMPAIR);
        setItem(Material.TWISTING_VINES);
        setDurationSec(3);
        setCooldownSec(20);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        final Quadrant quadrant = new Quadrant(player.getLocation()) {
            @Override
            public void onTouch(@Nonnull LivingGameEntity entity) {
                if (entity.equals(player)) {
                    return;
                }

                temperInstance.temper(entity, effectDuration, player);

                entity.damageNoKnockback(beamDamage, player, EnumDamageCause.RADIATION);
            }

            @Override
            public void onTick() {
                teleport(player.getLocation());

                if (player.isDeadOrRespawning() || getTick() >= getDuration()) {
                    cancel();
                }
            }

            @Override
            public void onTaskStop() {
                remove();
            }
        };

        quadrant.setDistance(beamLength);
        quadrant.runTaskTimer(0, 1);

        // Fx
        player.playWorldSound(location, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.0f);

        return Response.OK;
    }

}
