package me.hapyl.fight.command;

import me.hapyl.eterna.module.command.SimplePlayerAdminCommand;
import me.hapyl.fight.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.WorldInfo;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

public class GotoCommand extends SimplePlayerAdminCommand {
    public GotoCommand(String name) {
        super(name);
    }

    @Override
    protected void execute(Player player, String[] args) {
        final String worldName = getArgument(args, 0).toString();
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            // If the world is null, it means it's either not a world or no loaded
            // Try loading the world if the folder is in the folder.
            final File container = Bukkit.getWorldContainer();
            final File[] files = container.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory() && file.getName().equals(worldName)) {
                        Message.broadcastStaff("Loading '{%s}' world...".formatted(worldName));

                        world = Bukkit.createWorld(new WorldCreator(worldName));

                        Message.broadcastStaff("World '{%s}' loaded!".formatted(worldName));
                    }
                }
            }

            // If the world is still null, then it's invalid.
            if (world == null) {
                Message.error(player, "Invalid world '{%s}'!".formatted(worldName));
                return;
            }
        }

        if (player.getWorld() == world) {
            Message.error(player, "You are already in this world!");
            return;
        }

        moveToWorld(player, world);
    }

    @Nullable
    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        return completerSort(Bukkit.getWorlds().stream().map(WorldInfo::getName).toList(), args);
    }

    private void moveToWorld(Player player, World world) {
        final Location location = world.getSpawnLocation();

        player.teleport(location);

        Message.broadcastStaff("{%s} moved into '{%s}' world.".formatted(player.getName(), world.getName()));
    }

}
