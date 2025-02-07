package me.hapyl.fight.game.talents.witcher;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.TickingGameTask;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;

public class AxiiData extends TickingGameTask {

    private final Akciy talent;
    private final LivingGameEntity entity;
    private final GamePlayer stunner;
    private final ArmorStand stand;
    private final int duration;

    private double theta = 0.0d;

    public AxiiData(Akciy talent, LivingGameEntity entity, GamePlayer stunner, int duration) {
        this.talent = talent;
        this.entity = entity;
        this.stunner = stunner;
        this.duration = duration;

        final Location location = entity.getLocationAnchored();

        this.stand = Entities.ARMOR_STAND_MARKER.spawn(location, self -> {
            self.setInvisible(true);

            if (entity instanceof GamePlayer player) {
                player.blockDismount = true;
            }
        });

        entity.triggerDebuff(stunner);

        mountIfNotMounted();

        // Fx
        entity.playWorldSound(Sound.BLOCK_ANVIL_LAND, 1.25f);

        runTaskTimer(0, 1);
    }

    @Nonnull
    public GamePlayer getWhoStunned() {
        return stunner;
    }

    @Override
    public void run(int tick) {
        if (tick > duration) {
            cancel();
            return;
        }

        // Player can sometimes dismount somehow, even though the packet is cancelled
        mountIfNotMounted();

        final int timeLeft = duration - tick;

        // Display
        entity.sendTitle("&f&lsᴛᴜɴɴᴇᴅ", "&b%.1fs".formatted(timeLeft / 20.d), 0, 10, 0);

        // Fx
        final Location location = entity.getEyeLocation().add(0, 0.25, 0);

        final double x = Math.sin(theta) * 0.5d;
        final double y = Math.sin(Math.toRadians(tick)) * 0.25d;
        final double z = Math.cos(theta) * 0.5d;

        spawnBirds(location, x, y, z);
        spawnBirds(location, z, y, x);

        theta += Math.PI / 16;
    }

    @Override
    public void onTaskStop() {
        if (entity instanceof GamePlayer player) {
            player.blockDismount = false;
        }

        stand.remove();
        talent.axiiDatamap.remove(entity, this);

        // Fx
        entity.playWorldSound(Sound.ENTITY_HORSE_SADDLE, 0.0f);
    }

    private void mountIfNotMounted() {
        final LivingEntity bukkitEntity = entity.getEntity();

        if (!stand.getPassengers().contains(bukkitEntity)) {
            stand.addPassenger(bukkitEntity);
        }
    }

    private void spawnBirds(Location location, double x, double y, double z) {
        location.add(x, y, z);

        entity.spawnWorldParticle(location, Particle.CRIT, 1);

        location.subtract(x, y, z);
    }
}
