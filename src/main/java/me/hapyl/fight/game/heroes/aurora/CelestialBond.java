package me.hapyl.fight.game.heroes.aurora;

import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.task.TickingGameTask;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class CelestialBond extends TickingGameTask {

    private final Aurora.AuroraUltimate ultimate;
    private final AuroraData data;
    private final GamePlayer player;
    private final LivingGameEntity entity;
    private final AuroraVehicle vehicle;

    private double theta;

    CelestialBond(Aurora.AuroraUltimate ultimate, AuroraData data, GamePlayer player, LivingGameEntity entity) {
        this.ultimate = ultimate;
        this.data = data;
        this.player = player;
        this.entity = entity;

        // Ride vehicle
        this.vehicle = CF.getVehicleManager().startRiding(player.getPlayer(), AuroraVehicle::new);

        // Buff
        data.buffMap.replace(entity, new CelestialBondSpirit(player, entity), EtherealSpirit::remove);

        runTaskTimer(0, 1);
    }

    @Override
    public void onTaskStop() {
        // Remove entity buff
        data.buffMap.remove(entity, EtherealSpirit::remove);

        // Stop riding
        CF.getVehicleManager().stopRiding(player.getPlayer(), vehicle);
    }

    @Override
    public void run(int tick) {
        // Check for line of sight
        if (!player.hasLineOfSight(entity)) {
            data.breakBond("Line of sight was broken!");
            return;
        }

        // Lock to hero item
        player.snapTo(HotbarSlots.HERO_ITEM);

        // Affect
        entity.heal(ultimate.healing, player);

        // Update entity info
        if (entity instanceof GamePlayer entityPlayer) {
            entityPlayer.ui(CelestialBond.class, "&b₰ &d&n" + player.getName());
        }

        final Location location = player.getLocation();
        final int aliveTicks = player.ticker.aliveTicks.getTick();

        // Don't allow being too far away
        final Location entityLocation = entity.getLocation();
        final double distance = location.distanceSquared(entityLocation);

        if (distance >= ultimate.maxStayDistance) {
            final Vector pushVector = entityLocation.toVector().subtract(location.toVector()).normalize();

            vehicle.move(pushVector);
        }

        if (aliveTicks % player.random.nextInt(15, 30) == 0) {
            player.playWorldSound(location, Sound.ENTITY_ALLAY_AMBIENT_WITH_ITEM, player.random.nextFloat(0.75f, 1.25f));
            player.playWorldSound(location, Sound.ENTITY_ALLAY_ITEM_GIVEN, player.random.nextFloat(0.8f, 1.1f));
        }

        // Fx
        final double x = Math.sin(theta) * 1.25d;
        final double y = Math.atan(theta * 5) * 0.1d;
        final double z = Math.cos(theta) * 1.25d;

        LocationHelper.modify(location, x, y, z, then -> {
            HeroRegistry.AURORA.spawnParticles(location, 5, 0.2f, 0.1f, 0.2f);
        });

        LocationHelper.modify(location, z, y, x, then -> {
            HeroRegistry.AURORA.spawnParticles(location, 5, 0.2f, 0.1f, 0.2f);
        });

        theta += Math.PI / 16;
    }

    @Nonnull
    public GamePlayer getPlayer() {
        return player;
    }

    @Nonnull
    public LivingGameEntity getEntity() {
        return entity;
    }
}
