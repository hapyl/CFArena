package me.hapyl.fight.vehicle;

import me.hapyl.eterna.module.event.protocol.PacketReceiveEvent;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import net.minecraft.network.protocol.game.PacketPlayInSteerVehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class VehiclePacketListener implements Listener {

    @EventHandler
    public void handlePacketReceiveEvent(PacketReceiveEvent ev) {
        final PacketPlayInSteerVehicle packet = ev.getPacket(PacketPlayInSteerVehicle.class);

        if (packet == null) {
            return;
        }

        final VehicleManager vehicleManager = CF.getVehicleManager();
        final Vehicle vehicle = vehicleManager.getVehicle(ev.getPlayer());

        if (vehicle == null) {
            return;
        }

        final float xxa = packet.b();
        final float zza = packet.e();
        final boolean isJumping = packet.f();
        final boolean isSneaking = packet.g();

        ev.setCancelled(true);

        // Synchronize
        new BukkitRunnable() {
            @Override
            public void run() {
                vehicle.move(VehicleDirection.of(xxa, zza, isJumping, isSneaking));
            }
        }.runTask(Main.getPlugin());
    }

}
