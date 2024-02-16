package me.hapyl.fight.game.heroes.archive.doctor;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.task.TickingGameTask;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class CaptureData extends TickingGameTask {

    private final PhysGun physGun;
    private final GamePlayer player;
    private final LivingGameEntity entity;
    private final boolean flight;

    public CaptureData(@Nonnull PhysGun physGun, @Nonnull GamePlayer player, @Nonnull LivingGameEntity entity) {
        this.physGun = physGun;
        this.player = player;
        this.entity = entity;
        this.flight = entity instanceof GamePlayer playerEntity && playerEntity.getAllowFlight();

        entity.setInvulnerable(true);
        runTaskTimer(0, 1);
    }

    @Override
    public void onTaskStop() {
        entity.setInvulnerable(false);

        if (entity instanceof GamePlayer capturedPlayer) {
            capturedPlayer.setAllowFlight(flight);
        }

        physGun.capturedEntity.remove(player, this);
    }

    @Nonnull
    public GamePlayer getPlayer() {
        return player;
    }

    @Nonnull
    public LivingGameEntity getEntity() {
        return entity;
    }

    @Override
    public void run(int tick) {
        if (player.isDeadOrRespawning() || entity.isDeadOrRespawning() || !player.isHeldSlot(HotbarSlots.HERO_ITEM)) {
            cancel();
            return;
        }

        final Location location = player.getEyeLocation();
        final Vector directionNormalized = location.getDirection().normalize().multiply(0.5d);
        final Vector direction = location.getDirection().normalize().multiply(0.1d);

        for (double d = 0.0d; d <= physGun.maxDistance; d += physGun.shift) {
            final double x = direction.getX() * d;
            final double y = direction.getY() * d;
            final double z = direction.getZ() * d;

            location.add(x, y, z);

            final boolean passable = location.getBlock().isPassable();

            if (!passable) {
                location.subtract(directionNormalized);
                break;
            }
        }

        final Location entityLocation = entity.getEyeLocation();
        final double eyeHeight = entity.getEyeHeight();

        location.subtract(0, eyeHeight, 0);
        location.setYaw(entityLocation.getYaw());
        location.setPitch(entityLocation.getPitch());

        // Check for the blocks above/below
        final Block block = location.getBlock();

        if (!block.isPassable()) {
            location.add(0, 1, 0);
        }

        // Sync to location
        entity.teleport(location);

        entity.sendSubtitle("&f&lCaptured by &a%s&f&l!".formatted(player.getName()), 0, 10, 0);
        player.sendSubtitle("&f&lCarrying &a%s".formatted(entity.getName()), 0, 10, 0);
    }

}
