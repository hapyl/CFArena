package me.hapyl.fight.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

// handles server-related events
public class ServerHandler implements Listener {

    private final Set<String> forceRemovedCommands = Sets.newHashSet(
            "tps",
            "tell", "w", "msg",
            "me",
            "teammsg", "tm",
            "list",
            "?"
    );

    private final Map<Player, Collection<String>> playerCommands = Maps.newHashMap();

    @EventHandler()
    public void handlePlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent ev) {
        final Player player = ev.getPlayer();
        final String message = ev.getMessage();
        final String commandName = message.split(" ")[0];

        if (isValidCommand(player, commandName)) {
            return;
        }

        Message.error(player, "Unknown or incomplete command! ({%s})".formatted(message));
        ev.setCancelled(true);
    }

    @EventHandler()
    public void handlePlayerPlayerCommandSendEvent(PlayerCommandSendEvent ev) {
        final Player player = ev.getPlayer();
        final Collection<String> commands = ev.getCommands();

        final List<String> cfCommands = Lists.newArrayList(commands);
        cfCommands.removeIf(string -> !string.contains("cfarena:"));
        cfCommands.replaceAll(string -> string.replace("cfarena:", ""));

        commands.removeIf(command -> {
            if (command.contains(":")) {
                return true;
            }

            // Remove force removed commands unless they belong to CF
            for (String string : forceRemovedCommands) {
                if (command.equals(string)) {
                    // Make sure there is no cf command with the same name
                    if (cfCommands.contains(command)) {
                        return false;
                    }

                    return true;
                }
            }

            return false;
        });

        playerCommands.put(player, commands);
    }

    private boolean isValidCommand(Player player, String command) {
        // Fallback commands are not valid
        if (command.contains(":")) {
            return false;
        }

        final Collection<String> playerCommands = this.playerCommands.get(player);

        if (playerCommands.contains(command.replaceFirst("/", ""))) {
            return true;
        }

        return false;
    }

}
