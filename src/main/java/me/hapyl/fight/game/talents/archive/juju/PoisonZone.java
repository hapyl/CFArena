package me.hapyl.fight.game.talents.archive.juju;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.Temper;
import me.hapyl.fight.game.entity.EntityData;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.math.geometry.Draw;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class PoisonZone extends Talent {

    @DisplayField private final double radius = 5.0d;
    @DisplayField private final double damagePerTick = 1.0d;
    @DisplayField private final int damageTick = 10;
    @DisplayField(scaleFactor = 100) private final double defenseReduction = 0.4d;
    @DisplayField private final int defenseReductionDuration = Tick.fromSecond(5);

    public PoisonZone() {
        super("Poison Zone");

        setDescription("""
                Creates a deadly zone filled with the world's most toxic poison that rapidly damages and reduces %s of everyone who is in range.
                                
                &2;;Even Juju cannot handle his own creation.
                """, AttributeType.DEFENSE);

        setDurationSec(6);
    }

    @Override
    public final Response execute(Player player) {
        return execute(player, player.getLocation());
    }

    public Response execute(Player player, Location location) {
        GameTask.runDuration(this, (task, tick) -> {
            Collect.nearbyEntities(location, radius).forEach(living -> {
                living.damageTick(damagePerTick, player, EnumDamageCause.POISON_IVY, damageTick);

                if (living instanceof Player target) {
                    final GamePlayer gamePlayer = CF.getOrCreatePlayer(target);
                    final EntityAttributes attributes = gamePlayer.getAttributes();

                    attributes.decreaseTemporary(Temper.POISON_IVY, AttributeType.DEFENSE, defenseReduction, defenseReductionDuration);
                }

                living.addPotionEffect(PotionEffectType.POISON.createEffect(defenseReductionDuration, 1));
            });

            // Fx
            Geometry.drawCircle(location, radius, Quality.VERY_HIGH, new Draw(Particle.TOTEM) {
                @Override
                public void draw(Location location) {
                    PlayerLib.spawnParticle(location, Particle.TOTEM, 1, 0, 0, 0, 0.025f);
                    PlayerLib.spawnParticle(location, Particle.VILLAGER_HAPPY, 1, 0, 0, 0, 0.025f);
                }
            });

            // scatter particles
            PlayerLib.spawnParticle(location, Particle.TOTEM, 10, radius, 0.25d, radius, 0.1f);

        }, 0, 1);

        return Response.OK;
    }
}
