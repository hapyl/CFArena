package me.hapyl.fight.vehicle;

import me.hapyl.eterna.module.event.protocol.PacketReceiveEvent;
import me.hapyl.eterna.module.reflect.packet.wrapped.PacketWrapper;
import me.hapyl.eterna.module.reflect.packet.wrapped.PacketWrappers;
import me.hapyl.eterna.module.reflect.packet.wrapped.WrappedPacketPlayInSteerVehicle;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.util.ImmutableList;
import net.minecraft.network.protocol.game.PacketPlayInSteerVehicle;
import net.minecraft.world.entity.player.Input;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class VehiclePacketHandler implements Listener {

    @EventHandler
    public void handlePacketReceiveEvent(PacketReceiveEvent ev) {
        final WrappedPacketPlayInSteerVehicle packet = ev.getWrappedPacket(PacketWrappers.PACKET_PLAY_IN_STEER_VEHICLE);

        if (packet == null) {
            return;
        }

        final VehicleManager vehicleManager = CF.getVehicleManager();
        final Vehicle vehicle = vehicleManager.getVehicle(ev.getPlayer());

        if (vehicle == null) {
            return;
        }

        final WrappedPacketPlayInSteerVehicle.WrappedInput input = packet.getInput();

        ev.setCancelled(true);

        // Synchronize
        new BukkitRunnable() {
            @Override
            public void run() {
                vehicle.move(ImmutableList.of(input.getDirections()));
            }
        }.runTask(Main.getPlugin());
    }

    public static class InputObf {

    }

}
