package me.hapyl.fight.game.talents;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.entity.Player;

public class ChargedTalentData {

    private final GamePlayer player;
    private final ChargedTalent talent;

    private GameTask currentTask;
    private int queueTask;
    private int lastKnownSlot;
    private int chargesAvailable;

    public ChargedTalentData(GamePlayer player, ChargedTalent talent) {
        this.player = player;
        this.talent = talent;
        this.chargesAvailable = talent.getMaxCharges();
        this.lastKnownSlot = -1;
    }

    public Player getPlayer() {
        return player.getPlayer();
    }

    public int getQueueTask() {
        return queueTask;
    }

    public void setQueueTask(int queueTask) {
        this.queueTask = queueTask;
    }

    public int getLastKnownSlot() {
        return lastKnownSlot;
    }

    public void setLastKnownSlot(int lastKnownSlot) {
        this.lastKnownSlot = lastKnownSlot;
    }

    public int getChargesAvailable() {
        return chargesAvailable;
    }

    public void reset() {
        if (currentTask != null) {
            currentTask.cancel();
            currentTask = null;
        }

        queueTask = 0;
        chargesAvailable = talent.getMaxCharges();
        lastKnownSlot = -1;
    }

    public void removeCharge() {
        chargesAvailable--;
    }

    public void addCharge() {
        chargesAvailable++;
    }

    public void workTask() {
        if (currentTask != null) {
            queueTask++;
            return;
        }

        createTask();
    }

    public void maxCharge() {
        chargesAvailable = talent.getMaxCharges();
    }

    private void createTask() {
        currentTask = new GameTask() {
            @Override
            public void run() {
                // start another task
                if (queueTask >= 1) {
                    --queueTask;
                    createTask();
                }
                // nullate tasks and queue
                else {
                    currentTask = null;
                    queueTask = 0;
                }

                talent.grantCharge(player);
            }
        }.runTaskLater(player.scaleCooldown(talent.getRechargeTime()));
    }

}
