package me.hapyl.fight.game;

import me.hapyl.fight.Main;
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
            Chat.broadcast("&7&oSyncing database, might lag a little.");

            Manager.current().allProfiles(profile -> {
                profile.getDatabase().sync();
            });

            Chat.broadcast("&a&oDatabase synced!");
        } catch (Exception e) {
            Chat.broadcast("&4&lCould not save database, report this!");
            e.printStackTrace();
        }
    }
}
