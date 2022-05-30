package me.hapyl.fight.cmds;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class GamemodeShortcut extends SimplePlayerAdminCommand {

    private final Map<GameMode, Set<String>> modeMap = Maps.newHashMap();

    public GamemodeShortcut(String name) {
        super(name);
        this.setAliases("gm");

        addAlias(GameMode.CREATIVE, "creative", "creat", "cr", "c", "1");
        addAlias(GameMode.SURVIVAL, "survival", "surv", "sv", "s", "0");
        addAlias(GameMode.ADVENTURE, "adventure", "adv", "a", "2");
        addAlias(GameMode.SPECTATOR, "spectator", "spec", "sp", "3");

    }

    private void addAlias(GameMode mode, String... strings) {
        modeMap.put(mode, Sets.newHashSet(strings));
    }

    @Override
    protected void execute(Player player, String[] args) {
        if (args.length != 1) {
            Chat.sendMessage(player, "&cNot enough arguments.");
            return;
        }

        final String gameModeString = args[0];
        final GameMode gameMode = byString(gameModeString);

        if (gameMode == null) {
            Chat.sendMessage(player, "&cAlias %s not found! Available aliases:", gameModeString);
            modeMap.forEach((mode, set) -> {
                final String gameModeName = Chat.capitalize(mode);
                Chat.sendMessage(player, " &b&l%s&b: &7%s&7", gameModeName, Arrays.toString(set.toArray()));
            });
            return;
        }

        if (player.getGameMode() == gameMode) {
            Chat.sendMessage(player, "&cAlready in %s gamemode!", Chat.capitalize(gameMode));
            return;
        }

        player.setGameMode(gameMode);
        Chat.sendMessage(player, "&aChanged your gamemode to %s.", Chat.capitalize(gameMode));

    }

    private GameMode byString(String s) {
        for (GameMode gameMode : modeMap.keySet()) {
            if (modeMap.get(gameMode).contains(s)) {
                return gameMode;
            }
        }
        return null;
    }

}
