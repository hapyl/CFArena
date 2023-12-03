package me.hapyl.fight.dialog;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

// TODO (hapyl): 013, Aug 13: Complex
/**
 * Represents a Dialog system.
 */
public class Dialog {

    private final Queue<DialogEntry> entries;
    private final Map<Player, DialogTask> tasks;

    public Dialog() {
        this.entries = new LinkedList<>();
        this.tasks = Maps.newHashMap();
    }

    public void start(Player player) {
        final DialogTask task = tasks.get(player);

        if (task != null) {
            task.cancel();
        }

        tasks.put(player, new DialogTask(this, player));
    }

    public Queue<DialogEntry> entriesCopy() {
        return new LinkedList<>(entries);
    }
}
