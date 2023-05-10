package me.hapyl.fight.dialog;

import me.hapyl.fight.Main;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Queue;

public class DialogTask {

    private final Dialog dialog;
    private final Queue<DialogEntry> entries;
    private final Player player;

    private final BukkitTask task;

    public DialogTask(Dialog dialog, Player player) {
        this.dialog = dialog;
        this.entries = dialog.entriesCopy();
        this.player = player;

        this.task = new BukkitRunnable() {
            @Override
            public void run() {

            }
        }.runTaskTimer(Main.getPlugin(), 1, 1);
    }

    public void cancel() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }
}
