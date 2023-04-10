package me.hapyl.fight.game.talents.storage.extra;

import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.storage.harbinger.MeleeStance;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StanceData {

    private final Player player;
    private final long usedAt;
    private final ItemStack stack;
    private final GameTask task;

    public StanceData(Player player, ItemStack weapon) {
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

    public Player getPlayer() {
        return player;
    }

    public void cancelTask() {
        this.task.cancel();
    }
}
