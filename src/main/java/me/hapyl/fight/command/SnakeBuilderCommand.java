package me.hapyl.fight.command;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.chat.LazyEvent;
import me.hapyl.eterna.module.command.SimplePlayerAdminCommand;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.fight.CF;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class SnakeBuilderCommand extends SimplePlayerAdminCommand implements Listener {

    private final Map<UUID, LinkedList<Location>> builders = Maps.newHashMap();

    public SnakeBuilderCommand(String name) {
        super(name);

        CF.registerEvents(this);
    }

    @Override
    protected void execute(Player player, String[] args) {
        if (builders.containsKey(player.getUniqueId())) {
            final LinkedList<Location> locations = builders.remove(player.getUniqueId());

            final StringBuilder builder = new StringBuilder("Snake.builder()");
            final Location last = locations.pollLast();

            if (last == null) {
                Chat.sendMessage(player, "&cCannot create, no last location.");
                return;
            }

            for (Location location : locations) {
                builder.append(".next(%s, %s, %s)".formatted(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
            }

            builder.append(".end(%s, %s, %s);".formatted(last.getBlockX(), last.getBlockY(), last.getBlockZ()));

            final String string = builder.toString();

            Chat.sendClickableHoverableMessage(
                    player,
                    LazyEvent.copyToClipboard(string),
                    LazyEvent.showText("&eClick to copy code."),
                    "&6&lCLICK TO COPY CODE"
            );

            return;
        }

        builders.put(player.getUniqueId(), new LinkedList<>());
        Chat.sendMessage(player, "&aEnter snake builder, place &lgreen wool&a blocks.");
    }

    @EventHandler()
    public void handleBlockBreak(BlockBreakEvent ev) {
        final Player player = ev.getPlayer();
        final Block block = ev.getBlock();
        final LinkedList<Location> locations = builders.get(player.getUniqueId());

        if (locations == null || block.getType() != Material.GREEN_WOOL) {
            return;
        }

        locations.pollLast();
        Chat.sendMessage(player, "&aRemoved &llast&a block.");
        PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 0.0f);
    }

    @EventHandler()
    public void handleBlockPlace(BlockPlaceEvent ev) {
        final Player player = ev.getPlayer();
        final Block block = ev.getBlock();
        final LinkedList<Location> locations = builders.get(player.getUniqueId());

        if (locations == null || block.getType() != Material.GREEN_WOOL) {
            return;
        }

        locations.add(block.getLocation());
        Chat.sendMessage(player, "&aAdd snake location.");
        PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
    }
}
