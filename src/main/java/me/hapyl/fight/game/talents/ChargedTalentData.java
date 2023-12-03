package me.hapyl.fight.game.talents;

import me.hapyl.fight.game.task.GameTask;
import org.bukkit.entity.Player;

public class ChargedTalentData {

    private final Player player;
    private final ChargedTalent talent;

    private GameTask currentTask;
    private int queueTask;
    private int lastKnownSlot;
    private int chargedAvailable;

    public ChargedTalentData(Player player, ChargedTalent talent) {
        this.player = player;
        this.talent = talent;
        this.chargedAvailable = talent.getMaxCharges();
        this.lastKnownSlot = -1;
    }

    public Player getPlayer() {
        return player;
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

    public int getChargedAvailable() {
        return chargedAvailable;
    }

    public void reset() {
        if (currentTask != null) {
            currentTask.cancel();
            currentTask = null;
        }

        queueTask = 0;
        chargedAvailable = talent.getMaxCharges();
        lastKnownSlot = -1;
    }

    public void removeCharge() {
        chargedAvailable--;
    }

    public void addCharge() {
        chargedAvailable++;
    }

    public void workTask() {
        if (currentTask != null) {
            queueTask++;
            return;
        }

        createTask();
    }

    public void maxCharge() {
        chargedAvailable = talent.getMaxCharges();
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
        }.runTaskLater(talent.getRechargeTime());
    }

}
