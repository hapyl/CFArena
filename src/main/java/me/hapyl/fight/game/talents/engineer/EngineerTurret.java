package me.hapyl.fight.game.talents.engineer;

import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.RaycastTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.block.display.BlockStudioParser;
import me.hapyl.spigotutils.module.block.display.DisplayData;
import me.hapyl.spigotutils.module.block.display.DisplayEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;

import javax.annotation.Nonnull;

public class EngineerTurret extends EngineerTalent {

    @DisplayField private final double damage = 3;
    @DisplayField private final double radius = 32;

    @DisplayField private int delayBetweenShots = 15;
    @DisplayField private int damageIncreasePerLevel = 2;

    // This is the base of the turret (the bottom side)
    // It will be spawned with the main block display.
    private final DisplayData turretBase = BlockStudioParser.parse(
            "{Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:grindstone\",Properties:{face:\"ceiling\",facing:\"east\"}},transformation:[-0.3536f,-0.3536f,0.0000f,1.0693f,-0.3536f,0.3536f,0.0000f,0.1844f,-0.0000f,0.0000f,-0.5000f,0.2463f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:diorite_wall\",Properties:{up:\"true\"}},transformation:[-0.3536f,-0.5303f,0.0000f,0.8505f,-0.3536f,0.5303f,0.0000f,0.4031f,-0.0000f,0.0000f,-0.5000f,0.2463f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:grindstone\",Properties:{face:\"ceiling\",facing:\"east\"}},transformation:[0.1768f,0.1768f,0.4330f,-0.7772f,-0.3536f,0.3536f,0.0000f,0.1844f,-0.3062f,-0.3062f,0.2500f,0.8122f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:diorite_wall\",Properties:{up:\"true\"}},transformation:[0.1768f,0.2652f,0.4330f,-0.6679f,-0.3536f,0.5303f,0.0000f,0.4031f,-0.3062f,-0.4593f,0.2500f,0.6228f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:grindstone\",Properties:{face:\"ceiling\",facing:\"east\"}},transformation:[0.1768f,0.1768f,-0.4330f,-0.3445f,-0.3536f,0.3536f,0.0000f,0.1844f,0.3062f,0.3062f,0.2500f,-1.0695f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:diorite_wall\",Properties:{up:\"true\"}},transformation:[0.1768f,0.2652f,-0.4330f,-0.2351f,-0.3536f,0.5303f,0.0000f,0.4031f,0.3062f,0.4593f,0.2500f,-0.8801f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:andesite_wall\",Properties:{up:\"true\"}},transformation:[0.2500f,0.0000f,0.0000f,-0.1313f,0.0000f,0.2500f,0.0000f,0.5594f,0.0000f,0.0000f,0.2500f,-0.1250f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:nether_star\",Count:1},item_display:\"none\",transformation:[0.6125f,0.0000f,0.0000f,0.0112f,0.0000f,-0.0000f,1.5000f,0.8406f,0.0000f,-0.6125f,-0.0000f,-0.0175f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:nether_star\",Count:1},item_display:\"none\",transformation:[0.6364f,-0.6364f,-0.0000f,-0.0137f,-0.0000f,-0.0000f,1.5000f,0.9344f,-0.6364f,-0.6364f,-0.0000f,-0.0263f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:nether_star\",Count:1},item_display:\"none\",transformation:[0.6125f,0.0000f,0.0000f,0.0112f,0.0000f,-0.0000f,1.5000f,1.0281f,0.0000f,-0.6125f,-0.0000f,-0.0175f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:stone_slab\",Properties:{type:\"bottom\"}},transformation:[0.7500f,0.0000f,0.0000f,-0.3887f,0.0000f,0.5000f,0.0000f,1.0281f,0.0000f,0.0000f,0.7500f,-0.3600f,0.0000f,0.0000f,0.0000f,1.0000f]}]}"
    );

    public EngineerTurret() {
        super("Sentry", 1);

        setDescription("""
                Create a &cSentry&7 that will shoot the &enearest &cenemy&7.
                """);

        setType(TalentType.DAMAGE);
        setItem(Material.NETHERITE_SCRAP);

        setCooldownSec(35);
        setUpgradeCost(3);

        // Set the TOP data here, per level.
        // It is up to modeler to make the model properly, so it rotates around itself.
        setDisplayData(
                0,
                "{Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.0000f,0.0000f,0.2500f,-0.5210f,0.0000f,0.2500f,0.0000f,1.2469f,-0.2500f,0.0000f,0.0000f,0.5447f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.0000f,0.0000f,0.2500f,0.2910f,0.0000f,0.2500f,0.0000f,1.2469f,-0.2500f,0.0000f,0.0000f,0.5447f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.0000f,0.0000f,0.2500f,0.2910f,0.0000f,0.2500f,0.0000f,1.2469f,-0.2500f,0.0000f,0.0000f,-0.2673f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.0000f,0.0000f,0.2500f,-0.5210f,0.0000f,0.2500f,0.0000f,1.2469f,-0.2500f,0.0000f,0.0000f,-0.2673f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.0000f,0.0000f,0.2500f,-0.5210f,0.0000f,0.2500f,0.0000f,2.0594f,-0.2500f,0.0000f,0.0000f,0.5447f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.0000f,0.0000f,0.2500f,0.2910f,0.0000f,0.2500f,0.0000f,2.0594f,-0.2500f,0.0000f,0.0000f,0.5447f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.0000f,0.0000f,0.2500f,0.2910f,0.0000f,0.2500f,0.0000f,2.0594f,-0.2500f,0.0000f,0.0000f,-0.2673f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.0000f,0.0000f,0.2500f,-0.5210f,0.0000f,0.2500f,0.0000f,2.0594f,-0.2500f,0.0000f,0.0000f,-0.2673f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:observer\",Properties:{facing:\"up\",powered:\"false\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.4900f,0.0000f,-0.0000f,1.0000f,1.2781f,0.0000f,-1.0000f,-0.0000f,0.5137f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:deepslate_tile_wall\",Properties:{up:\"true\"}},transformation:[-0.0000f,0.0000f,-0.5000f,0.2600f,0.5000f,0.0000f,0.0000f,1.5244f,0.0000f,-0.6500f,-0.0000f,1.4962f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:reinforced_deepslate\",Properties:{}},transformation:[0.0000f,-0.0000f,0.5000f,-0.2400f,0.5000f,0.0000f,0.0000f,1.5244f,-0.0000f,0.5000f,0.0000f,0.4025f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:cauldron\",Properties:{}},transformation:[-0.0000f,-0.0000f,0.3300f,-0.1562f,0.3300f,-0.0000f,0.0000f,1.6094f,0.0000f,0.3300f,0.0000f,1.2325f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:cauldron\",Properties:{}},transformation:[0.0000f,0.0500f,0.0000f,0.5000f,0.0000f,0.0000f,-0.1250f,1.8406f,-0.5000f,0.0000f,0.0000f,0.2637f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:comparator\",Properties:{facing:\"east\",mode:\"subtract\",powered:\"false\"}},transformation:[0.0000f,0.0000f,0.5000f,-0.2500f,0.0000f,0.5000f,0.0000f,2.2469f,-0.5000f,0.0000f,0.0000f,0.2637f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:tripwire_hook\",Properties:{attached:\"false\",facing:\"east\",powered:\"false\"}},transformation:[0.0000f,-1.0000f,0.0000f,0.1250f,0.0000f,0.0000f,1.0000f,1.1531f,-1.0000f,-0.0000f,0.0000f,-0.3300f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:tripwire_hook\",Properties:{attached:\"false\",facing:\"east\",powered:\"false\"}},transformation:[0.0000f,1.0000f,-0.0000f,-0.1250f,0.0000f,-0.0000f,-1.0000f,2.1531f,-1.0000f,0.0000f,-0.0000f,-0.3300f,0.0000f,0.0000f,0.0000f,1.0000f]}]}"
        );

        yOffset = 2.25d;
    }

    @Nonnull
    @Override
    public Construct create(@Nonnull GamePlayer player, @Nonnull Location location) {
        return new Construct(player, location, this) {

            // Create the turret base here.
            private final DisplayEntity turretBase = EngineerTurret.this.turretBase.spawnInterpolated(location);

            @Override
            public void onCreate() {
            }

            @Nonnull
            @Override
            public ImmutableArray<Double> healthScaled() {
                return ImmutableArray.of(40d, 60d, 80d);
            }

            @Nonnull
            @Override
            public ImmutableArray<Integer> durationScaled() {
                return ImmutableArray.of(30, 40, 50);
            }

            @Override
            public void onDestroy() {
                turretBase.remove();
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

                    location.setPitch(0.0f);
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
                        targetEntity.damage(damage + (getLevel() * damageIncreasePerLevel), EnumDamageCause.SENTRY_SHOT);
                        return true;
                    }
                }.setIterations(3).runTaskTimer(0, 1);

                // Fx
                player.playWorldSound(location, Sound.ENTITY_BLAZE_SHOOT, 1.25f);
                player.playWorldSound(location, Sound.ENTITY_BLAZE_SHOOT, 2.0f);
            }
        };

    }


}
