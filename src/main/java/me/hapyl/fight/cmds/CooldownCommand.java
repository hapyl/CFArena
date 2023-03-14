package me.hapyl.fight.cmds;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CooldownCommand extends SimplePlayerAdminCommand {
    public CooldownCommand(String name) {
        super(name);
        setAliases("cd");
    }

    @Override
    protected void execute(Player player, String[] args) {
        final GamePlayer gamePlayer = GamePlayer.getAlivePlayer(player);
        if (gamePlayer == null) {
            Chat.sendMessage(player, "&cCannot use this command outside a game!");
            return;
        }

        for (ItemStack content : player.getInventory().getContents()) {
            if (content == null || content.getType().isAir()) {
                continue;
            }

            player.setCooldown(content.getType(), 0);
        }

        gamePlayer.getUltimate().stopCd(player);
        Chat.sendMessage(player, "&aReset cooldowns.");
    }
}
