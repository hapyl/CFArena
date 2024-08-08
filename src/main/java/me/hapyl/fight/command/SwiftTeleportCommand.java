package me.hapyl.fight.command;

import me.hapyl.fight.fx.SwiftTeleportAnimation;
import me.hapyl.fight.ux.Notifier;
import me.hapyl.eterna.module.command.SimplePlayerAdminCommand;
import me.hapyl.eterna.module.entity.Entities;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public class SwiftTeleportCommand extends SimplePlayerAdminCommand {
    public SwiftTeleportCommand() {
        super("testSwiftTeleport");
    }

    @Override
    protected void execute(Player player, String[] args) {
        final Block block = player.getTargetBlockExact(50);

        if (block == null) {
            Notifier.error(player, "Invalid location!");
            return;
        }

        final Location initialLocation = player.getLocation();
        final Location destinationLocation = block.getLocation().add(0.5d, 0.5d, 0.5d);

        final ArmorStand stand = Entities.ARMOR_STAND.spawn(initialLocation, self -> {
            self.setGravity(false);
        });

        new SwiftTeleportAnimation(initialLocation, destinationLocation) {
            @Override
            public void onAnimationStep(Location location) {
                stand.teleport(location);
            }

            @Override
            public void onAnimationStop() {
                stand.remove();
            }

        }.start(0, 1);
    }

}
