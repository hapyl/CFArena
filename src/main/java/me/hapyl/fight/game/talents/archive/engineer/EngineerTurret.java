package me.hapyl.fight.game.talents.archive.engineer;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.RaycastTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.block.display.DisplayEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;

import javax.annotation.Nonnull;

public class EngineerTurret extends EngineerTalent {

    @DisplayField private final double damage = 5;
    @DisplayField private final double radius = 16;
    @DisplayField private int delayBetweenShots = 20;

    public EngineerTurret() {
        super("Sentry", 6);

        setDescription("""
                Create a &cSentry&7 that will shoot the &enearest &cenemy&7.
                """);

        setItem(Material.NETHERITE_SCRAP);
        setCooldownSec(35);

        setDisplayData(
                0,
                "/summon block_display ~-0.5 ~-0.5 ~-0.5 {Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:grindstone\",Properties:{face:\"ceiling\",facing:\"east\"}},transformation:[-0.3536f,-0.3536f,0.0000f,1.3818f,-0.3536f,0.3536f,0.0000f,0.1844f,-0.0000f,0.0000f,-0.5000f,0.2463f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:diorite_wall\",Properties:{up:\"true\"}},transformation:[-0.3536f,-0.5303f,0.0000f,1.1630f,-0.3536f,0.5303f,0.0000f,0.4031f,-0.0000f,0.0000f,-0.5000f,0.2463f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:grindstone\",Properties:{face:\"ceiling\",facing:\"east\"}},transformation:[0.1768f,0.1768f,0.4330f,-0.4647f,-0.3536f,0.3536f,0.0000f,0.1844f,-0.3062f,-0.3062f,0.2500f,0.8122f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:diorite_wall\",Properties:{up:\"true\"}},transformation:[0.1768f,0.2652f,0.4330f,-0.3554f,-0.3536f,0.5303f,0.0000f,0.4031f,-0.3062f,-0.4593f,0.2500f,0.6228f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:grindstone\",Properties:{face:\"ceiling\",facing:\"east\"}},transformation:[0.1768f,0.1768f,-0.4330f,-0.0320f,-0.3536f,0.3536f,0.0000f,0.1844f,0.3062f,0.3062f,0.2500f,-1.0695f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:diorite_wall\",Properties:{up:\"true\"}},transformation:[0.1768f,0.2652f,-0.4330f,0.0774f,-0.3536f,0.5303f,0.0000f,0.4031f,0.3062f,0.4593f,0.2500f,-0.8801f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:andesite_wall\",Properties:{up:\"true\"}},transformation:[0.2500f,0.0000f,0.0000f,0.1812f,0.0000f,0.2500f,0.0000f,0.5594f,0.0000f,0.0000f,0.2500f,-0.1250f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:nether_star\",Count:1},item_display:\"none\",transformation:[0.6125f,0.0000f,0.0000f,0.3237f,0.0000f,-0.0000f,1.5000f,0.8406f,0.0000f,-0.6125f,-0.0000f,-0.0175f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:nether_star\",Count:1},item_display:\"none\",transformation:[0.6364f,-0.6364f,-0.0000f,0.2988f,-0.0000f,-0.0000f,1.5000f,0.9344f,-0.6364f,-0.6364f,-0.0000f,-0.0263f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:nether_star\",Count:1},item_display:\"none\",transformation:[0.6125f,0.0000f,0.0000f,0.3237f,0.0000f,-0.0000f,1.5000f,1.0281f,0.0000f,-0.6125f,-0.0000f,-0.0175f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:stone_slab\",Properties:{type:\"bottom\"}},transformation:[0.7500f,0.0000f,0.0000f,-0.0762f,0.0000f,0.5000f,0.0000f,1.0281f,0.0000f,0.0000f,0.7500f,-0.3600f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.2500f,0.0000f,0.0000f,-0.2323f,0.0000f,0.2500f,0.0000f,1.2469f,0.0000f,0.0000f,0.2500f,-0.5210f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.2500f,0.0000f,0.0000f,-0.2323f,0.0000f,0.2500f,0.0000f,1.2469f,0.0000f,0.0000f,0.2500f,0.2910f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.2500f,0.0000f,0.0000f,0.5797f,0.0000f,0.2500f,0.0000f,1.2469f,0.0000f,0.0000f,0.2500f,0.2910f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.2500f,0.0000f,0.0000f,0.5797f,0.0000f,0.2500f,0.0000f,1.2469f,0.0000f,0.0000f,0.2500f,-0.5210f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.2500f,0.0000f,0.0000f,-0.2323f,0.0000f,0.2500f,0.0000f,2.0594f,0.0000f,0.0000f,0.2500f,-0.5210f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.2500f,0.0000f,0.0000f,-0.2323f,0.0000f,0.2500f,0.0000f,2.0594f,0.0000f,0.0000f,0.2500f,0.2910f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.2500f,0.0000f,0.0000f,0.5797f,0.0000f,0.2500f,0.0000f,2.0594f,0.0000f,0.0000f,0.2500f,0.2910f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.2500f,0.0000f,0.0000f,0.5797f,0.0000f,0.2500f,0.0000f,2.0594f,0.0000f,0.0000f,0.2500f,-0.5210f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:observer\",Properties:{facing:\"up\",powered:\"false\"}},transformation:[0.0000f,1.0000f,0.0000f,-0.2012f,0.0000f,-0.0000f,1.0000f,1.2781f,1.0000f,0.0000f,0.0000f,-0.4900f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:deepslate_tile_wall\",Properties:{up:\"true\"}},transformation:[-0.0000f,0.6500f,0.0000f,-1.1837f,0.5000f,0.0000f,0.0000f,1.5244f,-0.0000f,0.0000f,-0.5000f,0.2600f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:reinforced_deepslate\",Properties:{}},transformation:[0.0000f,-0.5000f,0.0000f,-0.0900f,0.5000f,0.0000f,0.0000f,1.5244f,0.0000f,0.0000f,0.5000f,-0.2400f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:cauldron\",Properties:{}},transformation:[-0.0000f,-0.3300f,0.0000f,-0.9200f,0.3300f,-0.0000f,0.0000f,1.6094f,0.0000f,0.0000f,0.3300f,-0.1563f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:cauldron\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,0.0488f,0.0000f,0.0000f,-0.1250f,1.8406f,0.0000f,0.0500f,0.0000f,0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:comparator\",Properties:{facing:\"east\",mode:\"subtract\",powered:\"false\"}},transformation:[0.5000f,0.0000f,0.0000f,0.0488f,0.0000f,0.5000f,0.0000f,2.2469f,0.0000f,0.0000f,0.5000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:tripwire_hook\",Properties:{attached:\"false\",facing:\"east\",powered:\"false\"}},transformation:[1.0000f,0.0000f,0.0000f,0.6425f,0.0000f,0.0000f,1.0000f,1.1531f,0.0000f,-1.0000f,0.0000f,0.1250f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:tripwire_hook\",Properties:{attached:\"false\",facing:\"east\",powered:\"false\"}},transformation:[1.0000f,0.0000f,0.0000f,0.6425f,0.0000f,-0.0000f,-1.0000f,2.1531f,0.0000f,1.0000f,-0.0000f,-0.1250f,0.0000f,0.0000f,0.0000f,1.0000f]}]}"
        );

        yOffset = 2.25d;
    }

    @Nonnull
    @Override
    public Construct create(@Nonnull GamePlayer player, @Nonnull Location location) {
        return new Construct(player, location, this) {
            @Override
            public void onCreate() {
            }

            @Nonnull
            @Override
            public ImmutableArray<Double> healthScaled() {
                return ImmutableArray.of(15d, 25d, 35d);
            }

            @Nonnull
            @Override
            public ImmutableArray<Integer> durationScaled() {
                return ImmutableArray.of(15, 25, 35);
            }

            @Override
            public void onDestroy() {
            }

            @Override
            public void onTick() {
                final LivingGameEntity nearestEntity = Collect.nearestEntity(location, radius, entity -> {
                    if (player.isSelfOrTeammate(entity)) {
                        return false;
                    }

                    return entity.hasLineOfSight(this.entity.getEntity());
                });

                // Rotate if there are no entities
                if (nearestEntity == null) {
                    final DisplayEntity displayEntity = entity.getDisplayEntity();
                    final BlockDisplay head = displayEntity.getHead();
                    final Location location = head.getLocation();

                    location.setYaw(location.getYaw() + 5);
                    displayEntity.teleport(location);
                    return;
                }

                if (!modulo(delayBetweenShots)) {
                    return;
                }

                entity.lookAt(nearestEntity.getLocation());

                new RaycastTask(entity.getLocation().add(0.00d, 1.5d, 0.00d)) {

                    @Override
                    public boolean predicate(@Nonnull Location location) {
                        final Block block = location.getBlock();
                        final Material type = block.getType();

                        return !type.isOccluding();
                    }

                    @Override
                    public boolean step(@Nonnull Location location) {
                        player.spawnWorldParticle(location, Particle.CRIT_MAGIC, 1);

                        // Hit detection
                        final LivingGameEntity targetEntity = Collect.nearestEntity(location, 1, entity -> {
                            return !player.isSelfOrTeammate(entity);
                        });

                        if (targetEntity == null) {
                            return false;
                        }

                        targetEntity.setLastDamager(player);
                        targetEntity.damage(damage, EnumDamageCause.SENTRY_SHOT);
                        return true;
                    }
                }.runTaskTimer(0, 1);

                // Fx
                player.playWorldSound(location, Sound.ENTITY_BLAZE_SHOOT, 1.25f);
                player.playWorldSound(location, Sound.ENTITY_BLAZE_SHOOT, 2.0f);
            }
        };

    }


}
