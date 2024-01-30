package me.hapyl.fight.game.talents.archive.heavy_knight;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.archive.heavy_knight.SwordMaster;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.List;

public class Slash extends Talent {

    @DisplayField private final double distance = 3.0d;
    @DisplayField private final int effectDuration = Tick.fromSecond(4);
    @DisplayField private final double damage = 10.0d;

    private final TemperInstance temperInstance = Temper.POWER_SLASH.newInstance()
            .decrease(AttributeType.SPEED, 0.25d)
            .decrease(AttributeType.DEFENSE, 0.25d);

    public Slash() {
        super("Slash");

        setDescription("""
                Perform a slash in front of you, &cdamaging&7 and &3knocking&7 all &cenemies&7 in small AoE.
                """);

        setType(Type.DAMAGE);
        setItem(Material.QUARTZ);
        setCooldownSec(8);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        final Vector direction = location.getDirection().normalize().setY(0.0d);

        location.add(direction.multiply(distance));

        final List<LivingGameEntity> entitiesHit = Collect.nearbyEntities(
                location,
                distance,
                entity -> entity.isValid(player)
        );

        boolean strongHit = false;

        for (LivingGameEntity entity : entitiesHit) {
            entity.damageNoKnockback(damage, player);

            if (SwordMaster.addSuccessfulTalent(player, this) && !strongHit) {
                strongHit = true;
            }

            if (strongHit) {
                temperInstance.temper(entity, effectDuration);
                entity.playWorldSound(location, Sound.BLOCK_ANVIL_LAND, 2.0f);
            }
        }

        // Fx
        player.spawnWorldParticle(location, Particle.SWEEP_ATTACK, 10, distance, 0.5d, distance, 0.0f);
        player.playWorldSound(location, Sound.BLOCK_ANVIL_PLACE, 0.75f);

        return strongHit ? Response.AWAIT : Response.OK;
    }

}
