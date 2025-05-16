package me.hapyl.fight.game.heroes.nyx;

import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.talents.ChargeType;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.task.TickingStepGameTask;
import me.hapyl.fight.util.Collect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Display;

public class VoidPortal extends TickingGameTask implements Removable {

    private final GamePlayer player;
    private final Location location;
    private final Nyx.NyxUltimate ultimate;
    private final ChargeType type;

    private final double distance;
    private final double damage;
    private final double energyDecrease;

    private final Display voidEntity;

    private final double radiusIncrease;
    private final double durationWithCast;


    VoidPortal(GamePlayer player, Location location, Nyx.NyxUltimate ultimate, ChargeType type) {
        this.player = player;
        this.location = location;
        this.ultimate = ultimate;
        this.type = type;

        this.distance = ultimate.distance.value(type);
        this.damage = ultimate.damage.value(type);
        this.energyDecrease = ultimate.energyDecrease.value(type);

        final int duration = ultimate.duration.value(type);
        final int chaosRegen = ultimate.chaosRegen.value(type);

        this.radiusIncrease = distance / ultimate.castDuration;
        this.durationWithCast = duration + ultimate.castDuration;

        // Spawn the entity
        this.voidEntity = Entities.TEXT_DISPLAY.spawn(location, self -> {
            self.setShadowStrength(64.0f);
            self.setShadowRadius(0.0f);

            // FIXME: I think client saves the last shadow strength or something or its fucking sodium
            // FIXME (Tue, Aug 27 2024 @xanyjl): Neither, I think it's spigot bug
        });

        // Add chaos stacks
        HeroRegistry.NYX.getPlayerData(player).incrementChaosStacks(chaosRegen);

        // Fx
        player.playWorldSound(Sound.ENTITY_WARDEN_ROAR, 1.25f);

        // Do start the task
        runTaskTimer(0, 1);
    }

    @Override
    public void remove() {
        voidEntity.setShadowRadius(0.0f);
        voidEntity.setShadowStrength(1.0f);
        voidEntity.remove();

        cancel();
    }

    @Override
    public void run(int tick) {
        // Cancel if player has died
        if (player.isDeadOrRespawning()) {
            remove();
            return;
        }

        // If tick > castDuration then the animation is finished
        if (tick > ultimate.castDuration) {
            if (tick > durationWithCast) {
                // Final slash
                if (type == ChargeType.OVERCHARGED) {
                    performFinalSlash();
                }

                remove();
                return;
            }

            // Affect
            if (!modulo(ultimate.hitDelay)) {
                return;
            }

            affect();
            return;
        }

        // Fx
        voidEntity.setShadowRadius((float) (tick * radiusIncrease));
        player.spawnWorldParticle(location, Particle.ASH, 100, distance * 0.8, 0.1, distance * 0.8d, 0.02f);
    }

    private void affect() {
        Collect.nearbyEntities(location, distance, player::isNotSelfOrTeammate)
                .forEach(entity -> {
                    entity.damageNoKnockback(damage, player, DamageCause.CHAOS);

                    if (entity instanceof GamePlayer playerEntity) {
                        playerEntity.decrementEnergy(energyDecrease, player);
                    }
                });

        // Spear fx
        final double x = player.random.nextDoubleBool(distance - 1.0d);
        final double z = player.random.nextDoubleBool(distance - 1.0d);

        final Location startLocation = BukkitUtils.newLocation(location).add(x, 0, z);
        final Location endLocation = BukkitUtils.newLocation(startLocation).add(0, player.random.nextDouble(5, 7), 0);

        final DisplayEntity spearEntity = ultimate.spear.spawnInterpolated(startLocation, endLocation, self -> {
            self.setShadowStrength(0);
            self.setShadowRadius(0);
            self.setTeleportDuration(2);
        });

        GameTask.runLater(spearEntity::remove, 5);

        player.playWorldSound(location, Sound.ITEM_FLINTANDSTEEL_USE, 1.75f);
        player.playWorldSound(location, Sound.ENTITY_WARDEN_HURT, 1.25f);
        player.playWorldSound(location, Sound.ENTITY_BLAZE_HURT, 1.75f);
    }

    private void performFinalSlash() {
        Collect.nearbyEntities(location, distance, player::isNotSelfOrTeammateOrHasEffectResistance)
                .forEach(entity -> {
                    if (entity instanceof GamePlayer playerEnemy) {
                        playerEnemy.decrementEnergy(ultimate.overchargedEnergyDecrease, player);
                    }
                });

        // Fx
        new TickingStepGameTask(16) {
            private double d = 0.0d;

            @Override
            public boolean tick(int tick, int step) {
                final double x = Math.sin(d) * distance - 0.5d;
                final double y = Math.atan(d / (Math.PI * 2)) * distance - 0.5d;
                final double z = Math.cos(d) * distance - 0.5d;

                location.add(x, y, z);
                HeroRegistry.NYX.drawParticle(location);
                location.subtract(x, y, z);

                d += Math.PI / 32;

                return d >= Math.PI * (Math.PI / 2);
            }
        }.runTaskTimer(0, 1);

        // Fx
        player.playWorldSound(location, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.0f);
        player.playWorldSound(location, Sound.ENTITY_WARDEN_DEATH, 2.0f);
    }

}
