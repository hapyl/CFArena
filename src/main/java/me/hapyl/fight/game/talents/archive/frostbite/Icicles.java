package me.hapyl.fight.game.talents.archive.frostbite;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.Set;

public class Icicles extends Talent {

    @DisplayField private final double distance = 4.0;
    @DisplayField private final double damage = 5.0d;
    @DisplayField(percentage = true) private final double critChanceReduction = 0.25d;
    @DisplayField(percentage = true) private final double critDamageReduction = 0.5d;
    @DisplayField(percentage = true) private final double attackReduction = 0.15d;
    @DisplayField private final int debuffDuration = Tick.fromSecond(10);

    private final Random random = new Random();
    private final ItemStack[] fxItems = {
            new ItemStack(Material.ICE),
            new ItemStack(Material.PACKED_ICE),
            new ItemStack(Material.BLUE_ICE)
    };

    public Icicles() {
        super("Frostfall");

        setDescription("""
                Summon a bunch of icicles at your &etarget&7 enemy.
                &8;;If there are no target enemies, the icicles will spawn in front of you.
                                
                After a short delay, the icicles fall down, dealing &cAoE damage&7 and:
                └ Decreases %s by &b{critChanceReduction}&7.
                └ Decreases %s by &b{critDamageReduction}&7.
                └ Decreases %s by &b{attackReduction}&7.
                """, AttributeType.CRIT_CHANCE, AttributeType.CRIT_DAMAGE, AttributeType.ATTACK);

        setType(Type.IMPAIR);
        setItem(Material.ICE);
        setCooldownSec(12);
    }

    public void affect(@Nonnull GamePlayer player, @Nonnull Location location) {
        Collect.nearbyEntities(location, distance).forEach(entity -> {
            if (entity.equals(player)) {
                return;
            }

            entity.damage(damage, player, EnumDamageCause.FREEZE);
            final EntityAttributes attributes = entity.getAttributes();

            attributes.decreaseTemporary(Temper.ICE_CAGE, AttributeType.CRIT_CHANCE, critChanceReduction, debuffDuration);
            attributes.decreaseTemporary(Temper.ICE_CAGE, AttributeType.CRIT_DAMAGE, critDamageReduction, debuffDuration);
            attributes.decreaseTemporary(Temper.ICE_CAGE, AttributeType.ATTACK, attackReduction, debuffDuration);

            entity.addEffect(Effects.SLOW, 1, 20);
        });

        // Fx
        player.playWorldSound(location, Sound.BLOCK_GLASS_BREAK, 0.0f);
        player.playWorldSound(location, Sound.ENTITY_BAT_DEATH, 0.0f);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final LivingGameEntity targetEntity = Collect.targetEntityDot(player, 10, 0.7d, entity -> !entity.equals(player));

        Location location;

        if (targetEntity != null) {
            location = targetEntity.getLocation();
        }
        else {
            final Location playerLocation = player.getLocation();
            final Vector vector = playerLocation.getDirection().normalize().setY(0.0d).multiply(8);

            location = playerLocation.add(vector);
        }

        final Set<ArmorStand> fallingBlocks = Sets.newHashSet();

        new TickingGameTask() {
            private final double yOffset = 1.5d;
            private double theta = 0.0d;
            private float pitch = 0.0f;

            @Override
            public void run(int tick) {
                pitch = (float) (2.0f / Math.PI * theta);

                for (int i = 0; i < 2; i++) {
                    if (run0()) {
                        cancel();
                        return;
                    }
                }

                // Fx
                player.playWorldSound(location, Sound.BLOCK_GLASS_BREAK, pitch);
                player.playWorldSound(location, Sound.BLOCK_GLASS_PLACE, pitch);
                player.playWorldSound(location, Sound.BLOCK_SNOW_PLACE, pitch);
            }

            @Override
            public void onTaskStop() {
                new TickingGameTask() {
                    private final double maxTick = yOffset / 0.1d;

                    @Override
                    public void run(int tick) {
                        if (tick > maxTick) {
                            fallingBlocks.forEach(armorStand -> {
                                final Location standLocation = armorStand.getLocation().add(0.0d, 3.5d, 0.0d);

                                // Fx
                                player.spawnWorldParticle(standLocation, Particle.EXPLOSION_NORMAL, 5, 0.25d, 0.0d, 0.25d, 0);
                                player.spawnWorldParticle(standLocation, Particle.CRIT_MAGIC, 5, 0.25d, 0.0d, 0.25d, 0);

                                armorStand.remove();
                            });
                            fallingBlocks.clear();

                            affect(player, location);
                            cancel();
                            return;
                        }

                        fallingBlocks.forEach(stand -> {
                            stand.teleport(stand.getLocation().subtract(0.0d, 0.125d * tick / Math.PI, 0.0d));
                        });
                    }
                }.runTaskTimer(0, 1);
            }

            private void spawnFallingBlock() {
                final double x = CFUtils.randomAxis(0.2d, distance / 1.5d);
                final double y = yOffset + random.nextDouble(0.0d, 0.25d);
                final double z = CFUtils.randomAxis(0.2d, distance / 1.5d);

                location.add(x, y, z);

                fallingBlocks.add(Entities.ARMOR_STAND_MARKER.spawn(location, self -> {
                    self.setInvisible(true);
                    self.setSilent(true);
                    self.setHelmet(fxItems[fallingBlocks.size() % fxItems.length]);
                    self.setHeadPose(CFUtils.randomEulerAngle());
                }));

                // Fx
                player.playWorldSound(location, Sound.ENTITY_CHICKEN_EGG, pitch);

                location.subtract(x, y, z);
            }

            private boolean run0() {
                if (theta > Math.PI) {
                    return true;
                }

                final double x = Math.sin(theta) * distance;
                final double z = Math.cos(theta) * distance;

                location.add(x, 0, z);
                spawnParticles();
                location.subtract(x, 0, z);

                location.subtract(x, 0, z);
                spawnParticles();
                location.add(x, 0, z);

                // Spawn twice as much armor stands
                for (int i = 0; i < 2; i++) {
                    spawnFallingBlock();
                }

                theta += Math.PI / 16;
                return false;
            }

            private void spawnParticles() {
                player.spawnWorldParticle(location, Particle.SNOWFLAKE, 1, 0, 0, 0, 0.025f);
                player.spawnWorldParticle(location, Particle.BLOCK_CRACK, 1, 0, 0, 0, Material.ICE.createBlockData());
            }
        }.runTaskTimer(0, 1);

        return Response.OK;
    }
}
