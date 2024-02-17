package me.hapyl.fight.game.talents.archive.ender;

import me.hapyl.fight.event.custom.EnderPearlTeleportEvent;
import me.hapyl.fight.event.custom.PlayerClickAtEntityEvent;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import me.hapyl.spigotutils.module.util.LinkedKeyValMap;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class TransmissionBeacon extends Talent implements Listener {

    private final LinkedKeyValMap<GamePlayer, Entity> beaconLocation = new LinkedKeyValMap<>();
    @DisplayField private final int cooldownIfDestroyed = 600;

    public TransmissionBeacon() {
        super("Transmission Beacon", """
                Place the beacon somewhere hidden from your opponents.
                Use your &bultimate&7 to instantly teleport to its location and collect it.
                                
                &c&l;;The beacon can be destroyed!
                """);

        setItem(Material.BEACON);
        setType(Type.MOVEMENT);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        if (hasBeacon(player)) {
            return Response.error("The beacon is already placed!");
        }

        final Block block = player.getTargetBlockExact(5);

        if (block == null || !isSafeLocation(block)) {
            return Response.error("Location is not safe!");
        }

        final Location location = block.getRelative(BlockFace.UP).getLocation();
        setBeaconLocation(player, location);

        return Response.OK;
    }

    @Override
    public void onStart() {
        new TickingGameTask() {
            @Override
            public void run(int tick) {
                // Tick beacons
                beaconLocation.forEach((owner, beacon) -> {
                    final Location location = beacon.getLocation();
                    final World world = location.getWorld();

                    if (world == null) {
                        return;
                    }

                    final double y = Math.sin(Math.toRadians(tick * 5)) / 50;

                    location.setYaw(location.getYaw() + 5);
                    location.setY(location.getY() + y);

                    beacon.teleport(location);

                    // Fx
                    location.add(0.0d, 1.75d, 0.0d);

                    // Sound Fx
                    if (tick % 40 == 0) {
                        world.playSound(location, Sound.BLOCK_BEACON_POWER_SELECT, 1, 0.0f);
                    }

                    PlayerLib.spawnParticle(location, Particle.SPELL_WITCH, 1, 0.1d, 0.1d, 0.1d, 0.05f);
                    PlayerLib.spawnParticle(location, Particle.PORTAL, 1, 0.1d, 0.1d, 0.1d, 0.01f);
                    PlayerLib.spawnParticle(location, Particle.REVERSE_PORTAL, 1, 0.1d, 0.1d, 0.1d, 0.01f);
                    PlayerLib.spawnParticle(location, Particle.DRAGON_BREATH, 1, 0.1d, 0.1d, 0.1d, 0.025f);
                });
            }
        }.runTaskTimer(0, 1);
    }

    @EventHandler()
    public void handlePlayerClickAtEntityEvent(PlayerClickAtEntityEvent ev) {
        final GamePlayer owner = beaconLocation.getKey(ev.getEntity());

        if (owner == null || !ev.isLeftClick()) {
            return;
        }

        final Entity beacon = beaconLocation.remove(owner);

        if (beacon == null) {
            return;
        }

        ev.setCancelled(true);

        beacon.remove();
        startCd(owner, cooldownIfDestroyed);

        // Fx
        ev.getPlayer().sendMessage("&aYou broke %s's %s!", owner.getName(), getName());
        PlayerLib.playSound(beacon.getLocation(), Sound.BLOCK_GLASS_BREAK, 0.0f);

        owner.sendSubtitle("&cBeacon Destroyed!", 10, 20, 10);
        owner.playSound(Sound.BLOCK_GLASS_BREAK, 0.0f);
    }

    public boolean hasBeacon(GamePlayer player) {
        return beaconLocation.containsKey(player);
    }

    public void teleportToBeacon(GamePlayer player) {
        final Entity entity = beaconLocation.getValue(player);

        if (player == null || entity == null) {
            return;
        }

        beaconLocation.remove(player);
        entity.remove();

        final Location location = entity.getLocation().add(0.0d, 1.75d, 0.0d);

        BukkitUtils.mergePitchYaw(player.getLocation(), location);
        player.teleport(location);

        new EnderPearlTeleportEvent(player, location).call();

        player.addEffect(Effects.BLINDNESS, 1, 20);
        player.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.75f);
    }

    @Override
    public void onStop() {
        beaconLocation.values().forEach(Entity::remove);
        beaconLocation.clear();
    }

    public void setBeaconLocation(GamePlayer player, Location location) {
        if (hasBeacon(player)) {
            return;
        }

        location.add(0.5d, -0.8d, 0.5d);

        beaconLocation.put(player, Entities.ARMOR_STAND.spawn(location, self -> {
            self.setSilent(true);
            self.setInvulnerable(true);
            self.setVisible(false);
            self.setGravity(false);

            if (self.getEquipment() != null) {
                self.getEquipment().setHelmet(new ItemStack(Material.BEACON));
            }

            CFUtils.lockArmorStand(self);
        }));
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        beaconLocation.useValueAndRemove(player, Entity::remove);
    }

    private boolean isSafeLocation(Block block) {
        final Block relative = block.getRelative(BlockFace.UP);
        return relative.getType().isAir() && relative.getRelative(BlockFace.UP).getType().isAir();
    }

}
