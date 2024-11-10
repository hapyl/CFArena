package me.hapyl.fight.anticheat;

import me.hapyl.eterna.module.util.collection.Cache;
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
        private final Cache<Long> clicks;

        private ClickData() {
            this.clicks = Cache.ofSet(1_000L);
        }

        public int size() {
            return clicks.size();
        }

        public void click() {
            clicks.add(System.currentTimeMillis());
        }
    }

}
