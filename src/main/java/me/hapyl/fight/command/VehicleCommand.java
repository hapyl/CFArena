package me.hapyl.fight.command;

import me.hapyl.eterna.module.command.SimplePlayerAdminCommand;
import me.hapyl.fight.CF;
import me.hapyl.fight.vehicle.VehicleManager;
import me.hapyl.fight.vehicle.VehicleType;
import org.bukkit.entity.Player;

public class VehicleCommand extends SimplePlayerAdminCommand {

    public VehicleCommand(String name) {
        super(name);
    }

    @Override
    protected void execute(Player player, String[] args) {
        // vehicle (ride) (vehicle)
        // vehicle (dismount)
        final VehicleManager vehicleManager = CF.getVehicleManager();

        final String arg0 = getArgument(args, 0).toString();

        if (arg0.equalsIgnoreCase("dismount")) {
            final boolean stoppedRiding = vehicleManager.stopRiding(player);

            if (stoppedRiding) {
                player.sendRichMessage("<green>Stopped riding.");
            }
            else {
                player.sendRichMessage("<dark_red>Not riding anything!");
            }
        }
        else if (arg0.equalsIgnoreCase("ride")) {
            final VehicleType vehicleType = getArgument(args, 1).toEnum(VehicleType.class);

            if (vehicleType == null) {
                player.sendRichMessage("<dark_red>Invalid vehicle!");
                return;
            }

            vehicleManager.startRiding(player, vehicleType.function);
            player.sendRichMessage("<dark_green>Start riding.");
        }
        else {
            player.sendRichMessage("<dark_red>Invalid usage!");
        }
    }
}
