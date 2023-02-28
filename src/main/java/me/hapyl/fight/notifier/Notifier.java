package me.hapyl.fight.notifier;

import me.hapyl.fight.Main;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class Notifier implements Runnable {

    private final Queue<Notification> notifications = new LinkedList();
    private Iterator<Notification> iterator;

    public Notifier(Main main) {
        this.notifications.addAll(Arrays.asList(Notification.values()));
        this.iterator = notifications.iterator();

        Bukkit.getScheduler().runTaskTimer(main, this, 6000L, 6000L);
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

    }
}
