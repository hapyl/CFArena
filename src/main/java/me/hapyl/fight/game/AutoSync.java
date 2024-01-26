package me.hapyl.fight.game;

import me.hapyl.fight.Main;
import me.hapyl.fight.util.Benchmark;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoSync {

    protected boolean scheduleSave;

    public AutoSync(int perTicks) {
        this.scheduleSave = false;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (scheduleSave) {
                    return;
                }

                if (!Manager.current().anyProfiles()) {
                    Chat.broadcast("&7&oNo one is online, skipping database sync.");
                    return;
                }

                if (Manager.current().isGameInProgress()) {
                    scheduleSave = true;
                    Chat.broadcast("&7&oScheduled to sync database after this game.");
                    return;
                }

                save();
            }
        }.runTaskTimer(Main.getPlugin(), perTicks, perTicks);
    }

    public void save() {
        scheduleSave = false;

        try {
            final Benchmark benchmark = new Benchmark();
            benchmark.start();
            Chat.broadcast("&7&oSyncing database, might lag a little.");

            Manager.current().allProfiles(profile -> {
                profile.getDatabase().save();
            });

            benchmark.end();
            long millis = benchmark.getResult().asMillis();

            Chat.broadcast("&a&oDatabase synced! &8(%sms)", millis);
        } catch (Exception e) {
            Chat.broadcast("&4&lCould not save database, report this!");
            e.printStackTrace();
        }
    }
}
