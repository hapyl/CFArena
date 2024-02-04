package me.hapyl.fight.game.talents.archive.witcher;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.entity.Entities;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;

public class AxiiData extends TickingGameTask {

    private final Akciy talent;
    private final LivingGameEntity entity;
    private final ArmorStand stand;

    private double theta = 0.0d;

    public AxiiData(Akciy talent, LivingGameEntity entity) {
        this.talent = talent;
        this.entity = entity;

        final Location location = entity.getLocationAnchored();

        this.stand = Entities.ARMOR_STAND_MARKER.spawn(location, self -> {
            self.setInvisible(true);
            self.addPassenger(entity.getEntity());

            if (entity instanceof GamePlayer player) {
                player.blockDismount = true;
            }
        });

        // Fx
        entity.playWorldSound(Sound.BLOCK_ANVIL_LAND, 1.25f);

        runTaskTimer(0, 1);
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

    @Override
    public void run(int tick) {
        if (tick > talent.getDuration()) {
            cancel();
            return;
        }

        final int timeLeft = talent.getDuration() - tick;

        // Display
        entity.sendTitle("&f&lsᴛᴜɴɴᴇᴅ", "&b" + CFUtils.decimalFormatTick(timeLeft), 0, 10, 0);

        // Fx
        final Location location = entity.getEyeLocation().add(0, 0.25, 0);

        final double x = Math.sin(theta) * 0.5d;
        final double y = Math.sin(Math.toRadians(tick)) * 0.25d;
        final double z = Math.cos(theta) * 0.5d;

        spawnBirds(location, x, y, z);
        spawnBirds(location, z, y, x);

        theta += Math.PI / 16;
    }

    private void spawnBirds(Location location, double x, double y, double z) {
        location.add(x, y, z);

        entity.spawnWorldParticle(location, Particle.CRIT, 1);

        location.subtract(x, y, z);
    }
}
