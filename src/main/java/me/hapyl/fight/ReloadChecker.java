package me.hapyl.fight;

import me.hapyl.fight.game.Debug;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.chat.CenterChat;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.DependencyInjector;
import me.hapyl.spigotutils.module.util.Runnables;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public final class ReloadChecker extends DependencyInjector<Main> {

    private int reloadCount = -1;

    public ReloadChecker(Main plugin) {
        super(plugin);

        try {
            final Server server = getPlugin().getServer();
            reloadCount = (int) server.getClass().getDeclaredField("reloadCount").get(server);
        } catch (Exception ignored) {
        }
    }

    public int getReloadCount() {
        return reloadCount;
    }

    public void check(int delay) {
        Runnables.runLater(() -> {
            if (reloadCount > 0) {
                sendCenterMessageToOperatorsAndConsole("""
                                                
                        &4&lWARING
                        &cServer Reload Detected!
                                                
                        &cNote that &l%s&c does &nnot&c support &e/reload&c since it causes all sorts of errors and memory leaks.
                        
                        &cIf you are a developer, unless you &nabsolutely&c know what you are doing, you should not &e/reload&c the server!
                                                
                        &4Please &e/restart&4 your server before reporting an error!
                                                
                        """.formatted(getPlugin().getDescription().getName()));

                // sfx
                Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(player -> {
                    PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 0.0f);
                    PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 0.0f);
                });
            }

            Debug.keepInfo("&ePlugin started at &6" + new SimpleDateFormat("HH'h' mm'm' ss's'").format(new Date(Main.getStartupTime())));
        }, delay);
    }

    private void sendCenterMessageToOperatorsAndConsole(String message) {
        final List<CommandSender> senders = Collect.onlineOperatorsAndConsole();

        final String[] messages = message.split("\n");
        for (String s : messages) {
            if (s.isEmpty() || s.isBlank()) {
                senders.forEach(sender -> {
                    Chat.sendMessage(sender, "");
                });
                continue;
            }

            final List<String> strings = ItemBuilder.splitString("&c", Chat.format(s), 50);

            for (String string : strings) {
                final String centerString = CenterChat.makeString(string);

                senders.forEach(sender -> {
                    Chat.sendMessage(sender, centerString);
                });
            }
        }


    }

}
