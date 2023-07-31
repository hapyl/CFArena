package me.hapyl.fight.game.talents.archive.swooper;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class BlastPack extends ChargedTalent {

    @DisplayField(suffix = "blocks") private final double explosionRadius = 4.0d;

    private final Map<Player, Entity> blastPackMap = new HashMap<>();

    public BlastPack() {
        super("Blast Pack", """
                &b1) &7Throw the blast pack!
                &b2) &7Click again to explode!
                &b3) &7???
                &b4) &7Fly!
                """, 2);

        setItem(Material.DETECTOR_RAIL);
        setCooldown(3);
        setRechargeTimeSec(12);
    }

    @Override
    public void onStopCharged() {
        blastPackMap.values().forEach(Entity::remove);
        blastPackMap.clear();
    }

    @Override
    public void onDeathCharged(Player player) {
        Nulls.runIfNotNull(blastPackMap.get(player), Entity::remove);
        blastPackMap.remove(player);
    }

    @Nullable
    public Entity getBlastPack(Player player) {
        return blastPackMap.get(player);
    }

    @Override
    public Response execute(Player player) {
        final Entity blastPack = getBlastPack(player);

        // Explode blast pack
        if (blastPack != null) {
            explodeSatchel(player, blastPack);
            return Response.OK;
        }

        // Throw blast pack
        final Item item = player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(getMaterial()));
        final Vector vector = player.getEyeLocation().getDirection().normalize();
        blastPackMap.put(player, item);

        item.setPickupDelay(Integer.MAX_VALUE);
        item.setVelocity(vector);
        item.setThrower(player.getUniqueId());

        new GameTask() {
            private int maxAirTime = 300;

            @Override
            public void run() {

                if (maxAirTime-- < 0 || item.isDead() || getBlastPack(player) != item || item.isOnGround()) {
                    this.cancel();
                    return;
                }

                PlayerLib.spawnParticle(item.getLocation(), Particle.FLAME, 1, 0, 0, 0, 0);

            }
        }.runTaskTimer(0, 1);

        PlayerLib.playSound(item.getLocation(), Sound.ENTITY_SLIME_JUMP, 0.75f);

        return Response.AWAIT;
    }

    public void explodeSatchel(Player player, Entity entity) {
        entity.remove();
        blastPackMap.remove(player);

        final Location location = entity.getLocation();

        // Explosion
        Collect.nearbyEntities(location, explosionRadius).forEach(gameEntity -> {
            if (gameEntity.is(player)) {
                gameEntity.addEffect(GameEffectType.FALL_DAMAGE_RESISTANCE, 40);
            }
            else {
                gameEntity.damage(5.0d, player, EnumDamageCause.SATCHEL);
            }

            final Vector vector = gameEntity.getLocation().toVector().subtract(location.toVector()).normalize();
            gameEntity.setVelocity(vector.multiply(gameEntity.is(player) ? 1.35d : 0.35d));
        });

        // FX
        PlayerLib.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.75f);
        Geometry.drawSphere(location, (explosionRadius * 2) + 1, explosionRadius, new WorldParticle(Particle.FIREWORKS_SPARK));

    }

}
