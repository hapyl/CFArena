package me.hapyl.fight.notifier;

import com.google.common.collect.Lists;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.setting.Setting;
import me.hapyl.fight.util.Debuggable;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Queue;

public class Notifier implements Runnable, Debuggable {

    private final Queue<Notification> notifications = Lists.newLinkedList();
    private Iterator<Notification> iterator;

    private boolean debug = false;

    public Notifier(Main main) {
        this.notifications.addAll(Arrays.asList(Notification.values()));
        this.iterator = notifications.iterator();

        Bukkit.getScheduler().runTaskTimer(main, this, isDebug() ? 100L : 36000L, isDebug() ? 100L : 36000L);
    }

    public Notification next() {
        if (iterator.hasNext()) {
            return iterator.next();
        }
        else {
            iterator = notifications.iterator();
            return next();
        }
    }

    @Override
    public void run() {
        final Notification notification = next();

        if (isDebug()) {
            Chat.broadcast("NOTIFIER RUNNING IN DEBUG MODE");
        }


        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Setting.SEE_NOTIFICATIONS.isDisabled(player)) {
                continue;
            }

            emptyLine();
            Chat.sendCenterMessage(player, "&6&lNOTIFICATION");
            emptyLine();

            notification.forEachNotifier(player);

            emptyLine();
        }
    }

    private void emptyLine() {
        Bukkit.getOnlinePlayers().forEach(player -> Chat.sendMessage(player, ""));
    }

    @Override
    public boolean isDebug() {
        return debug;
    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
