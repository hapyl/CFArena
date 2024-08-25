package me.hapyl.fight.game.talents.nyx;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.util.BukkitUtils;

import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.EntityRandom;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TickingStepGameTask;
import me.hapyl.fight.registry.Key;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Set;

public class WitherRosePath extends Talent {

    @DisplayField private final double maxHealthDecrease = 10;
    @DisplayField private final double energyRechargeDecrease = 50;
    @DisplayField private final double maxDistance = 30;
    @DisplayField private final double damage = 5.0d;

    @DisplayField private final int impairDuration = Tick.fromSecond(6);
    @DisplayField private final int spikeDelay = 25;

    private final DisplayData spike = BDEngine.parse(
            "/summon block_display ~-0.5 ~-0.5 ~-0.5 {Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:obsidian\",Properties:{}},transformation:[0.8750f,0.0000f,0.0000f,-0.4375f,0.0000f,1.0000f,0.0000f,-0.5625f,0.0000f,0.0000f,0.8750f,-0.4375f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:obsidian\",Properties:{}},transformation:[0.6250f,0.0000f,0.0000f,-0.3125f,0.0000f,0.7500f,0.0000f,0.2500f,0.0000f,0.0000f,0.6250f,-0.3125f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:crying_obsidian\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,-0.2500f,0.0000f,0.6875f,0.0000f,0.8750f,0.0000f,0.0000f,0.5000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:obsidian\",Properties:{}},transformation:[0.1768f,0.0000f,0.1768f,-0.1875f,0.0000f,0.3750f,0.0000f,1.5625f,-0.1768f,0.0000f,0.1768f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:wither_rose\",Properties:{}},transformation:[0.3789f,-0.2187f,0.0000f,-0.5000f,0.2187f,0.3789f,0.0000f,0.5000f,0.0000f,0.0000f,0.4375f,-0.3125f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:wither_rose\",Properties:{}},transformation:[0.4705f,0.2717f,0.1456f,0.0000f,-0.2813f,0.4871f,0.0000f,0.5000f,-0.1261f,-0.0728f,0.5433f,-0.5625f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:wither_rose\",Properties:{}},transformation:[0.3062f,0.1768f,-0.3536f,0.2500f,-0.2500f,0.4330f,0.0000f,1.3125f,0.3062f,0.1768f,0.3536f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:obsidian\",Properties:{}},transformation:[0.6187f,0.0000f,0.6187f,-0.6250f,0.0000f,1.0000f,0.0000f,-0.6875f,-0.6187f,0.0000f,0.6187f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f]}]}"
    );

    private final TemperInstance temperInstance = Temper.NYX.newInstance()
            .decreaseScaled(AttributeType.MAX_HEALTH, maxHealthDecrease)
            .decreaseScaled(AttributeType.ENERGY_RECHARGE, energyRechargeDecrease);

    private final BlockData spikeBlockData = Material.OBSIDIAN.createBlockData();

    public WitherRosePath(@Nonnull Key key) {
        super(key, "Wither Path");

        setDescription("""
                Launch a path of &8spikes&7 and &8wither roses&7 in front of you that travel forward.
                
                Upon hitting an &cenemy&7, deals &cdamage&7 and &eimpairs&7 hit enemy's %s and %s.
                &8&o;;Gain one %s&8&o stack for each hit enemy.
                """.formatted(AttributeType.MAX_HEALTH, AttributeType.ENERGY_RECHARGE, Named.THE_CHAOS)
        );

        setItem(Material.WITHER_ROSE);
        setType(TalentType.IMPAIR);

        setCooldownSec(8);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Vector direction = player.getDirection().setY(0);
        final Location location = player.getLocation();

        final Set<LivingGameEntity> hitEntities = Sets.newHashSet();

        new TickingStepGameTask(3) {
            private double d = 0.0d;

            @Override
            public boolean tick(int tick) {
                if (d >= maxDistance) {
                    cancel();
                    return true;
                }

                final double x = direction.getX() * d;
                final double y = direction.getY() * d;
                final double z = direction.getZ() * d;

                location.add(x, y, z);
                createRose(player, hitEntities, BukkitUtils.anchorLocation(location), tick);
                location.subtract(x, y, z);

                d += 1.0d;
                return false;
            }
        }.runTaskTimer(0, 1);

        return Response.OK;
    }

    private void createRose(GamePlayer player, Set<LivingGameEntity> hitEntities, Location location, int tick) {
        final EntityRandom random = player.random;

        location.add(random.nextDouble(), random.nextDouble() * 0.5d, random.nextDouble());

        location.setYaw(random.nextFloat() * 180);
        location.setPitch(random.nextFloat() * 60);

        final DisplayEntity displayEntity = spike.spawnInterpolated(location);

        // Damage and debuff
        Collect.nearbyEntities(location, 1, player::isNotSelfOrTeammate)
                .forEach(entity -> {
                    entity.damage(damage, player, EnumDamageCause.NYX_SPIKE);

                    temperInstance.temper(entity, impairDuration, player);

                    // Give chaos stack
                    // Only one stack per enemy can be gained
                    if (!hitEntities.contains(entity)) {
                        player.getPlayerData(HeroRegistry.NYX).incrementChaosStacks(1);
                        hitEntities.add(entity);
                    }

                    // Fx
                    final Vector direction = entity.getDirection().multiply(-1).multiply(0.2d);

                    entity.setVelocity(new Vector(direction.getX(), 0.05d, direction.getZ()));
                });

        // Appear fx
        // Only spawn each 3rd tick cuz that's a lot of sounds
        if (tick % 3 == 0) {
            player.playWorldSound(location, Sound.ENTITY_EVOKER_FANGS_ATTACK, 0.75f);
        }

        // Remove
        GameTask.runLater(() -> {
            displayEntity.remove();

            // Fx
            player.spawnWorldParticle(location, Particle.BLOCK, 50, 0.5, 0.5, 0.5, 0.075f, spikeBlockData);
            player.playWorldSound(location, Sound.BLOCK_STONE_BREAK, 0.75f);
        }, spikeDelay);
    }

}
