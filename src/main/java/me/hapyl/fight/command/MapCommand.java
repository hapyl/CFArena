package me.hapyl.fight.command;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.gui.MapSelectGUI;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerCommand;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MapCommand extends SimplePlayerCommand {

    public MapCommand(String str) {
        super(str);
        setUsage("map [Map]");
    }

    @Override
    protected void execute(Player player, String[] args) {
        if (Manager.current().isGameInProgress()) {
            Chat.sendMessage(player, "&cCannot change map during the game.");
            return;
        }
        // Map [MapName]
        if (args.length >= 1) {
            final GameMaps value = Validate.getEnumValue(GameMaps.class, args[0]);
            if (value == null) {
                PlayerLib.villagerNo(player, "&cInvalid map!");
                return;
            }

            value.select(player);
            return;
        }

        new MapSelectGUI(player);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return completerSort(CFUtils.collectionToStringList(GameMaps.getPlayableMaps(), GameMaps::name), args);
    }

}