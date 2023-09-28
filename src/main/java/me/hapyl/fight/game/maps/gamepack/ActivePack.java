package me.hapyl.fight.game.maps.gamepack;

import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.garbage.CFGarbageCollector;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class ActivePack extends TickingGameTask {

    public static final int SPAWN_THRESHOLD = Tick.fromSecond(30);

    private final GamePack pack;
    private final Location location;
    private final ArmorStand platform;

    private ArmorStand entity;
    protected int nextSpawn;

    public ActivePack(GamePack pack, Location location) {
        this.pack = pack;
        this.location = location;
        this.platform = Entities.ARMOR_STAND.spawn(
                location.clone().subtract(0.0d, 1.6d, 0.0d),
                self -> {
                    self.setInvulnerable(true);
                    self.setVisible(false);
                    self.setGravity(false);
                    self.setHelmet(new ItemStack(Material.SMOOTH_STONE_SLAB));

                    CFGarbageCollector.add(self);
                    CFUtils.lockArmorStand(self);
                }
        );

        runTaskTimer(1, 1);
    }

    public void next() {
        this.next(false);
    }

    @Nullable
    public ArmorStand getEntity() {
        return entity;
    }

    @Override
    public void run(int tick) {
        // If pack is not spawned, display the time left before spawn
        if (entity == null) {
            if (nextSpawn > 0 && nextSpawn <= SPAWN_THRESHOLD) {
                platform.setCustomName(ChatColor.AQUA + CFUtils.decimalFormatTick(nextSpawn));
                platform.setCustomNameVisible(true);
            }
            return;
        }

        platform.setCustomNameVisible(false);

        final Location entityLocation = entity.getLocation();

        entityLocation.setYaw(entityLocation.getYaw() + 5);

        final double newY = Math.sin(Math.toRadians(tick * 2)) / 8;

        entityLocation.setY(location.getY() + newY);
        entity.teleport(entityLocation);

        entityLocation.add(0.0d, 1.0d, 0.0d); // add for particle

        pack.displayParticle(entityLocation);
    }

    public final void pickup0(Player player) {
        pack.onPickup(player);

        remove();
        next();
    }

    public void createEntity() {
        remove();

        entity = Entities.ARMOR_STAND_MARKER.spawn(location, self -> {
            self.addScoreboardTag("GamePack");
            self.setSmall(true);
            self.setVisible(false);
            CFUtils.setEquipment(self, equipment -> equipment.setHelmet(pack.getTexture()));
            CFGarbageCollector.add(self);
        });
    }

    public void next(boolean start) {
        nextSpawn = start ? pack.getSpawnPeriod() / 2 : pack.getSpawnPeriod();

        new GameTask() {
            @Override
            public void run() {
                nextSpawn--;
                if (nextSpawn == 0) {
                    createEntity();
                    cancel();
                }
            }
        }.runTaskTimer(0, 1);
    }

    private void remove() {
        if (entity != null) {
            entity.remove();
            entity = null;
        }
    }

}
