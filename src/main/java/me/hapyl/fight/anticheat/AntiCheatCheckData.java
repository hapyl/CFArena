package me.hapyl.fight.anticheat;

import me.hapyl.fight.CF;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AntiCheatCheckData {

    private static final long CHECK_RESET = 3_500;

    private final Player player;
    private final AntiCheatCheck check;

    private int failedChecks;
    private long lastFailedCheck;

    AntiCheatCheckData(Player player, AntiCheatCheck check) {
        this.player = player;
        this.check = check;
    }

    public void fail() {
        // Check for reset
        if (lastFailedCheck != 0 && System.currentTimeMillis() - lastFailedCheck >= CHECK_RESET) {
            failedChecks = 0;
            lastFailedCheck = 0;
        }

        failedChecks++;
        lastFailedCheck = System.currentTimeMillis();

        // Notify
        CF.getAntiCheat()
                .message("%s failed %s check! &7(%s&7)".formatted(
                        player.getName(),
                        check.name(),
                        makeStringFractional(failedChecks, check.getMaxFails())
                ));

        // Fail check
        if (failedChecks >= check.getMaxFails()) {
            check.punish(player);
        }
    }

    private static String makeStringFractional(int current, int max) {
        final float percent = (float) current / max;
        final ChatColor color;

        if (percent >= 1.0f) {
            color = ChatColor.DARK_RED;
        }
        else if (percent >= 0.75f) {
            color = ChatColor.RED;
        }
        else if (percent >= 0.5f) {
            color = ChatColor.GOLD;
        }
        else {
            color = ChatColor.YELLOW;
        }

        return "%s%s&7/&4%s".formatted(color, current, max);
    }

}
