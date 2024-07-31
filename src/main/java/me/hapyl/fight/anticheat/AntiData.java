package me.hapyl.fight.anticheat;

import me.hapyl.fight.util.collection.CacheSet;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class AntiData {

    public final Player player;

    // Data //
    public final ClickData recentCps = new ClickData();

    // Checks //
    public final AntiCheatPlayerCheck checks;

    AntiData(@Nonnull Player player) {
        this.player = player;
        this.checks = new AntiCheatPlayerCheck(player);
    }

    public static class ClickData {
        private final CacheSet<Long> clicks;

        private ClickData() {
            this.clicks = new CacheSet<>(1_000L);
        }

        public int size() {
            return clicks.size();
        }

        public void click() {
            clicks.add(System.currentTimeMillis());
        }
    }

}
