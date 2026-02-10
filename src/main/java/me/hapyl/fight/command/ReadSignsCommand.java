package me.hapyl.fight.command;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.chat.LazyEvent;
import me.hapyl.eterna.module.util.ArgumentList;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.Message;
import me.hapyl.fight.build.NamedSignReader;
import me.hapyl.fight.database.rank.PlayerRank;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Queue;

public class ReadSignsCommand extends CFCommand {

    private Queue<Sign> queue;
    private int size;

    public ReadSignsCommand(@Nonnull String name) {
        super(name, PlayerRank.ADMIN);
    }

    @Override
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        if (queue == null) {
            final NamedSignReader reader = new NamedSignReader(player.getWorld());

            queue = reader.readAsQueue();
            size = queue.size();

            if (size == 0) {
                Message.error(player, "There aren't any sings in loaded chunks!");
                return;
            }

            Message.success(player, "Found {%s} sings. Run the command again to view them.".formatted(size));
            return;
        }

        // Manual reset
        final String argument = args.getString(0);

        if (argument.equalsIgnoreCase("reset")) {
            reset();
            Message.success(player, "Successfully reset read signs!");
            return;
        }

        final Sign sign = queue.poll();
        final String line = sign.getLine(0).replace("[", "").replace("]", "").toUpperCase();
        final Location location = sign.getLocation();
        final String locationString = BukkitUtils.locationToString(location);

        Message.info(player, "");
        Message.success(player, "Sign %s: &l{%s}".formatted(Chat.makeStringFractional(size - queue.size(), size), line.toUpperCase()));

        Chat.sendClickableHoverableMessage(
                player,
                LazyEvent.runCommand("/tp %s %s %s".formatted(location.getX(), location.getY(), location.getZ())),
                LazyEvent.showText("&6Click to teleport!"),
                "&8● &6&lCLICK TO TELEPORT"
        );

        Chat.sendClickableHoverableMessage(
                player,
                LazyEvent.copyToClipboard("%s, %s, %s".formatted(location.getX(), location.getY(), location.getZ())),
                LazyEvent.showText("&eClick to copy coordinates!"),
                "&8● &e&lCLICK TO COPY COORDINATES"
        );

        // TODO (Sun, Feb 16 2025 @xanyjl): If spawn sign, yaw should be the sign direction

        // FIXME @May 08, 2025 (xanyjl) -> Signs broke ig
        
        Chat.sendClickableHoverableMessage(
                player,
                LazyEvent.copyToClipboard("addLocation(%s, 0, 0);".formatted(locationString)),
                LazyEvent.showText("&dClick to copy code!"),
                "&8● &d&lCLICK TO COPY CODE"
        );

        Chat.sendClickableHoverableMessage(
                player,
                LazyEvent.runCommand(getUsage().toLowerCase()),
                LazyEvent.showText("&aClick to show the next sign!"),
                "&8● &a&lNEXT"
        );

        if (queue.isEmpty()) {
            Message.success(player, "That was the last sign!");
            Message.sound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
            reset();
        }
    }

    private void reset() {
        queue.clear();
        queue = null;
        size = 0;
    }

}
