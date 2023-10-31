package me.hapyl.fight.game.talents.archive.harbinger;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.inventory.ItemStack;

public class StanceData {

    private final GamePlayer player;
    private final long usedAt;
    private final ItemStack stack;
    private final GameTask task;

    public StanceData(GamePlayer player, ItemStack weapon) {
        this.player = player;
        this.stack = weapon;
        this.usedAt = System.currentTimeMillis();
        this.task = new GameTask() {
            @Override
            public void run() {
                Talents.STANCE.getTalent(MeleeStance.class).switchToRange(player);
            }
        }.runTaskLater(Talents.STANCE.getTalent(MeleeStance.class).getMaxDuration());
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
