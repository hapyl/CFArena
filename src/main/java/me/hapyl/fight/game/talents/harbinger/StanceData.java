package me.hapyl.fight.game.talents.harbinger;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.inventory.ItemStack;

public class StanceData {

    private final GamePlayer player;
    private final long usedAt;
    private final ItemStack stack;
    private final GameTask task;

    public StanceData(MeleeStance talent, GamePlayer player, ItemStack weapon) {
        this.player = player;
        this.stack = weapon;
        this.usedAt = System.currentTimeMillis();
        this.task = player.schedule(() -> talent.switchToRange(player), talent.getMaxDuration());
    }

    public long getDuration() {
        return System.currentTimeMillis() - usedAt;
    }

    public int getDurationTick() {
        return (int) (getDuration() / 50);
    }

    public ItemStack getOriginalWeapon() {
        return stack;
    }

    public GamePlayer getPlayer() {
        return player;
    }

    public void cancelTask() {
        this.task.cancel();
    }
}
