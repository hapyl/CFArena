package me.hapyl.fight.command;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.command.SimplePlayerCommand;
import me.hapyl.eterna.module.util.Enums;
import me.hapyl.fight.Message;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.maps.EnumLevel;
import me.hapyl.fight.gui.MapSelectGUI;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Sound;
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
            final EnumLevel value = Enums.byName(EnumLevel.class, args[0]);
            if (value == null) {
                Message.ERROR.send(player, "Invalid map!");
                Message.sound(player, Sound.ENTITY_VILLAGER_NO, 1.0f);
                return;
            }

            value.select(player);
            return;
        }

        new MapSelectGUI(player);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return completerSort(CFUtils.collectionToStringList(EnumLevel.getPlayableMaps(), EnumLevel::name), args);
    }

}