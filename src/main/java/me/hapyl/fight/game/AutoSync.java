package me.hapyl.fight.game;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.util.NanoBenchmark;
import me.hapyl.fight.Main;
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
            final NanoBenchmark benchmark = NanoBenchmark.ofNow();
            Chat.broadcast("&7&oSyncing database, might lag a little.");

            Manager.current().forEachProfile(profile -> {
                profile.getDatabase().save();
            });

            benchmark.step("save");
            long millis = benchmark.getFirstResult().asMillis();

            Chat.broadcast("&a&oDatabase synced! &8(%sms)".formatted(millis));
        } catch (Exception e) {
            Chat.broadcast("&4&lCould not save database, report this!");
            e.printStackTrace();
        }
    }
}
