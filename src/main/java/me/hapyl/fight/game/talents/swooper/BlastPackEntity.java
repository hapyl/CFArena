package me.hapyl.fight.game.talents.swooper;

import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.witcher.Akciy;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.block.display.BlockStudioParser;
import me.hapyl.spigotutils.module.block.display.DisplayData;
import me.hapyl.spigotutils.module.block.display.DisplayEntity;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class BlastPackEntity extends TickingGameTask {

    public static final DisplayData data = BlockStudioParser.parse(
            "/summon block_display ~-0.5 ~-0.5 ~-0.5 {Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:detector_rail\",Properties:{powered:\"false\",shape:\"east_west\"}},transformation:[-0.0000f,0.0000f,-0.7500f,0.3750f,0.7500f,-0.0000f,-0.0000f,0.0000f,-0.0000f,-0.7500f,0.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f]}]}"
    );
    private static final double blockOffset = 0.37d;

    private final BlastPack talent;
    private final GamePlayer player;
    private final Item entity;
    private DisplayEntity wallEntity;
    private int aliveTime;
    private boolean isArmed;

    public BlastPackEntity(BlastPack talent, GamePlayer player) {
        this.talent = talent;
        this.player = player;

        final Location location = player.getEyeLocation();
        final Vector direction = location.getDirection();

        this.entity = player.getWorld().dropItem(location, new ItemStack(talent.getMaterial()));
        this.entity.setPickupDelay(Integer.MAX_VALUE);
        this.entity.setVelocity(direction);
        this.entity.setOwner(player.getUUID());

        runTaskTimer(0, 1);
    }

    @Nonnull
    public Location getLocation() {
        return wallEntity != null ? wallEntity.getHead().getLocation() : entity.getLocation();
    }

    @Override
    public void run(int tick) {
        if (aliveTime++ >= (isArmed ? talent.maxLifeTime : talent.maxAirTime)) {
            explode();
            return;
        }

        // Fx
        if (isArmed) {
            final int maxLifeTime = talent.maxLifeTime;

            if (isFibonacci(maxLifeTime - aliveTime, maxLifeTime)) {
                player.playWorldSound(entity.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.25f);
                player.playWorldSound(entity.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.75f + (0.75f / maxLifeTime * aliveTime));
            }
        }

        // Collision detection
        if (entity.isDead()) {
            return;
        }

        final Location location = entity.getLocation();

        for (WallSide side : WallSide.values()) {
            final double x = side.doubles[0] * blockOffset;
            final double y = side.doubles[1] * blockOffset;
            final double z = side.doubles[2] * blockOffset;

            location.add(x, y, z);

            final Material type = location.getBlock().getType();

            if (type.isOccluding()) {
                makeWallEntity(side);
                return;
            }

            location.subtract(x, y, z);
        }

        // Fx
        player.spawnWorldParticle(location, Particle.FLAME, 1);
    }

    public void explode() {
        final Location location = getLocation();

        Collect.nearbyEntities(location, talent.explosionRadius).forEach(entity -> {
            final Vector vector = entity.getLocation().toVector().subtract(location.toVector()).normalize();

            // Push the player
            if (player.equals(entity)) {
                player.setVelocity(vector.multiply(talent.selfMagnitude));
                player.addEffect(Effects.FALL_DAMAGE_RESISTANCE, 100, true);
                return;
            }

            // Damage and stun non-teammates
            if (!player.isTeammate(entity)) {
                entity.damageNoKnockback(talent.damage, player, EnumDamageCause.SATCHEL);

                if (shouldStun()) {
                    Talents.AKCIY.getTalent(Akciy.class).stun(entity, talent.stunDuration);
                }
            }

            // Push
            if (entity.hasEffectResistanceAndNotify(player)) {
                return;
            }

            entity.setVelocity(vector.multiply(talent.otherMagnitude));
        });

        cancel();
        talent.blastPacks.remove(player, this);

        // Fx
        player.playWorldSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.75f);

        Geometry.drawSphere(location, (talent.explosionRadius * 2) + 1, talent.explosionRadius, new WorldParticle(Particle.FIREWORK));
    }

    @Override
    public void onTaskStop() {
        if (entity != null) {
            entity.remove();
        }

        if (wallEntity != null) {
            wallEntity.remove();
        }
    }

    private boolean isFibonacci(int i, int max) {
        int a = 0;
        int b = 1;

        while (a <= max) {
            if (a == i) {
                return true;
            }

            int t = a + b;
            a = b;
            b = t;
        }

        return false;
    }

    private boolean shouldStun() {
        return isArmed && aliveTime >= (talent.maxLifeTime / 2);
    }

    private void makeWallEntity(WallSide side) {
        final Location location = entity.getLocation();

        entity.remove();
        aliveTime = 0;
        isArmed = true;

        wallEntity = data.spawn(side.mutate(location));

        // Fx
        player.playWorldSound(location, Sound.ITEM_ARMOR_EQUIP_CHAIN, 0.0f);
    }

    enum WallSide {
        FLOOR(
                0, -1, 0,
                0f, 90f
        ),
        CEILING(
                0, 1, 0,
                0f, 90f
        ),
        NORTH(
                0, 0, -1,
                0f, 0f
        ),
        EAST(
                1, 0, 0,
                90f, 0f
        ),
        SOUTH(
                0, 0, 1,
                180f, 0f
        ),
        WEST(
                -1, 0, 0,
                -90f, 0f
        );

        final double[] doubles;
        final float[] floats;

        WallSide(double dx, double dy, double dz, float yaw, float pitch) {
            this.doubles = new double[] { dx, dy, dz };
            this.floats = new float[] { yaw, pitch };
        }

        @Nonnull
        Location mutate(Location location) {
            location.setYaw(floats[0]);
            location.setPitch(floats[1]);

            return location;
        }

    }
}
