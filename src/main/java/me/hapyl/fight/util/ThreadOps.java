package me.hapyl.fight.util;

import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;

public final class ThreadOps {
    
    private static final Main plugin = CF.getPlugin();
    
    private ThreadOps() {
    }
    
    public static void async(@Nonnull ThreadOp op) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    op.run();
                }
                catch (RuntimeException ex) {
                    op.exception(ex);
                }
            }
        }.runTaskAsynchronously(plugin);
    }
    
    public static void sync(@Nonnull ThreadOp op) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    op.run();
                }
                catch (RuntimeException ex) {
                    op.exception(ex);
                }
            }
        }.runTask(plugin);
    }
    
    public interface ThreadOp {
        
        void run();
        
        default void exception(@Nonnull RuntimeException ex) {
        }
        
    }
    
    
}
