package me.hapyl.fight.cmds;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import org.bukkit.entity.Player;

public class UltimateCommand extends SimplePlayerAdminCommand {

    public UltimateCommand(String str) {
        super(str);
    }

    @Override
    protected void execute(Player player, String[] strings) {
        final GamePlayer gamePlayer = GamePlayer.getAlivePlayer(player);
        if (gamePlayer == null) {
            Chat.sendMessage(player, "&cCannot use this command outside a game!");
            return;
        }

        gamePlayer.setUltPoints(gamePlayer.getUltPointsNeeded());
        gamePlayer.getUltimate().stopCd(player);

        Chat.sendMessage(player, "&aCharged your ultimate!");
    }

}