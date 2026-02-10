package me.hapyl.fight.garbage;

import me.hapyl.eterna.module.util.Runnables;
import me.hapyl.fight.game.Manager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.EntitiesLoadEvent;

import javax.annotation.Nonnull;

/**
 * This is a very not good solution to cleaning in-game garbage
 * entities that aren't removed because of restart/crash, etc.
 */
public final class SynchronizedGarbageEntityCollector {

    private static final String GARBAGE_TAG = "GarbageEntity";

    public static int clearInAllWorlds() {
        int removed = 0;

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (isGarbageEntity(entity)) {
                    removeSync(entity);
                }
            }
        }

        return removed;
    }

    public static void add(@Nonnull Entity entity) {
        entity.addScoreboardTag(GARBAGE_TAG);
    }

    public static void remove(@Nonnull Entity entity) {
        if (shouldNotRemove()) {
            return;
        }

        entity.remove();
    }

    public static void removeSync(@Nonnull Entity entity) {
        Runnables.runSync(() -> remove(entity));
    }

    public static boolean isGarbageEntity(@Nonnull Entity entity) {
        return entity.getScoreboardTags().contains(GARBAGE_TAG);
    }

    private static boolean shouldNotRemove() {
        return Manager.current().isGameInProgress();
    }

    public static class Handler implements Listener {
        @EventHandler()
        public void handleEntityLoad(EntitiesLoadEvent ev) {
            if (shouldNotRemove()) { // reduce checks
                return;
            }

            for (Entity entity : ev.getEntities()) {
                if (isGarbageEntity(entity)) {
                    removeSync(entity);
                }
            }
        }
    }

}
