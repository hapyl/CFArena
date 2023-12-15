package me.hapyl.fight.dialog;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.profile.PlayerProfile;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.Queue;

public class ActiveDialog extends BukkitRunnable {

    private final PlayerProfile profile;
    private final Dialog dialog;
    private final Queue<DialogEntry> entries;

    private int tick;
    private boolean awaitInput;

    public ActiveDialog(PlayerProfile profile, Dialog dialog) {
        this.profile = profile;
        this.dialog = dialog;
        this.entries = dialog.entriesCopy();

        runTaskTimer(Main.getPlugin(), 1, 1);
    }

    @Nonnull
    public Player getPlayer() {
        return profile.getPlayer();
    }

    @Override
    public void run() {
        // Player left
        if (!profile.getPlayer().isOnline()) {
            cancel();
            return;
        }

        // For for player dialog
        if (awaitInput) {
            return;
        }

        if (tick-- <= 0) {
            nextEntry();
        }
    }

    public void nextEntry() {
        final DialogEntry entry = entries.poll();

        // Dialog finished
        if (entry == null) {
            profile.getDatabase().metadataEntry.set(dialog.getKey(), true);
            cancel();
            return;
        }

        entry.display(this);
        tick = entry.getDelay();
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        profile.dialog = null;
    }
}
