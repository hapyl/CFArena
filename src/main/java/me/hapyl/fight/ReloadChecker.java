package me.hapyl.fight;

import me.hapyl.fight.game.Debugger;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.CenterChat;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.DependencyInjector;
import me.hapyl.spigotutils.module.util.Runnables;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public final class ReloadChecker extends DependencyInjector<Main> {

    private ReloadChecker(Main plugin) {
        super(plugin);
    }

    public static void check(Main plugin, int delay) {
        new ReloadChecker(plugin).check(delay);
    }

    public void check(int delay) {
        Runnables.runLater(() -> {
            try {
                final Server server = getPlugin().getServer();
                final int reloadCount = (int) server.getClass().getDeclaredField("reloadCount").get(server);

                if (reloadCount > 0) {
                    sendCenterMessageToOperatorsAndConsole("");
                    sendCenterMessageToOperatorsAndConsole("&4&lWARNING");
                    sendCenterMessageToOperatorsAndConsole("&cSever Reload Detected!");
                    sendCenterMessageToOperatorsAndConsole("");

                    sendCenterMessageToOperatorsAndConsole(
                            "&cNote that %s does &nnot&c support &e/reload&c and it's &nshould only&c be used in development.",
                            getPlugin().getDescription().getName()
                    );

                    sendCenterMessageToOperatorsAndConsole("");

                    sendCenterMessageToOperatorsAndConsole("&cIf you are not a developer, please &lrestart&c the server instead.");
                    sendCenterMessageToOperatorsAndConsole("");

                    // sfx
                    Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(player -> {
                        PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 0.0f);
                        PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 0.0f);
                    });
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }

            Debugger.keepInfo("&ePlugin started at &6" + new SimpleDateFormat("HH'h' mm'm' ss's'").format(new Date(Main.getStartupTime())));
        }, delay);
    }

    private void sendCenterMessageToOperatorsAndConsole(String message, @Nullable Object... format) {
        if (message.isEmpty() || message.isBlank()) {
            Utils.getOnlineOperatorsAndConsole().forEach(sender -> {
                Chat.sendMessage(sender, "", format);
            });
            return;
        }

        final List<String> strings = ItemBuilder.splitString("&c", Chat.format(message, format), 50);

        for (String string : strings) {
            final String centerString = CenterChat.makeString(string);

            Utils.getOnlineOperatorsAndConsole().forEach(sender -> {
                Chat.sendMessage(sender, centerString, format);
            });
        }
    }

}
