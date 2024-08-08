package me.hapyl.fight.command;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.command.SimplePlayerAdminCommand;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class DebugPlayerCommand extends SimplePlayerAdminCommand {
    public DebugPlayerCommand(String name) {
        super(name);
    }

    @Override
    protected void execute(Player player, String[] args) {
        final GamePlayer gamePlayer = CF.getPlayer(player);

        if (gamePlayer == null) {
            Chat.sendMessage(player, "&cNo game instance found.");
            return;
        }

        Chat.sendMessage(player, "&c&lDEBUG:");

        boolean color = true;
        try {
            for (Field field : gamePlayer.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                String name = field.getName();
                String type = field.getType().getSimpleName();

                final Object valueRaw = field.get(gamePlayer);
                String value = valueRaw == null ? "null" : valueRaw.toString();

                if (Modifier.isStatic(field.getModifiers())) {
                    type = type + " &lSTATIC";
                }

                if (value.contains("@")) {
                    value = "@" + valueRaw.getClass().getSimpleName();
                }

                // name: Type = value
                if (color) {
                    Chat.sendMessage(player, "&f%s: &o%s &f= &f&l%s".formatted(name, type, value));
                }
                else {
                    Chat.sendMessage(player, "&e%s: &o%s &e= &e&l%s".formatted(name, type, value));
                }

                color = !color;
            }
        } catch (Exception e) {
            Chat.sendMessage(player, "&cError debugging player, see console.");
            e.printStackTrace();
        }
    }
}
