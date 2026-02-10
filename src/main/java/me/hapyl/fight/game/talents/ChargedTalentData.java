package me.hapyl.fight.game.talents;

import me.hapyl.eterna.module.util.Removable;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class ChargedTalentData implements Removable {
    
    protected final GamePlayer player;
    protected final ChargedTalent talent;
    
    protected int queue;
    protected int charges;
    
    private Task task;
    
    ChargedTalentData(@Nonnull GamePlayer player, @Nonnull ChargedTalent talent) {
        this.player = player;
        this.talent = talent;
        this.charges = talent.maxCharges();
    }
    
    public int charges() {
        return charges;
    }
    
    @Override
    public void remove() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        
        queue = 0;
        charges = talent.maxCharges();
    }
    
    public void decrementCharge() {
        // Don't allow underflow
        if (charges <= 0) {
            return;
        }
        
        charges--;
        
        // If already on cooldown, increment queue
        if (task != null) {
            queue++;
        }
        // Else start the task
        else {
            createTask();
        }
        
        updateItem();
    }
    
    public void incrementCharge() {
        charges++;
        updateItem();
        
        // Fx
        player.playSound(Sound.ENTITY_CHICKEN_EGG, 1.0f);
    }
    
    public void recharge() {
        remove();
        updateItem();
        
        // Fx
        player.playSound(Sound.ENTITY_CHICKEN_EGG, 1.0f);
    }
    
    protected void updateItem() {
        final HotBarSlot slot = talentSlot();
        final ItemStack item = talent.getItem();
        
        item.setAmount(Math.max(1, charges));
        player.setItem(slot, item);
        
        // If charges > 0, start internal cooldown to prevent accidental usage
        if (charges > 0) {
            talent.startCooldown(player, talent.internalCooldown());
        }
        else {
            // If the task is null, it means that the talent does not recharge, so just start infinite cooldown
            if (task == null) {
                player.cooldownManager.setCooldownIgnoreCooldownModifier(item, Constants.INDEFINITE_COOLDOWN);
            }
            else {
                // Internal cooldown so ignore fatigue
                player.cooldownManager.setCooldownIgnoreCooldownModifier(item, getNextChargeIn());
            }
        }
    }
    
    public int getNextChargeIn() {
        return task != null ? (int) (task.cooldown * 50L - (System.currentTimeMillis() - task.startedAt)) / 50 : 0;
    }
    
    @Nonnull
    private HotBarSlot talentSlot() {
        return player.getHero().getTalentSlotByHandle(talent);
    }
    
    private void createTask() {
        // Don't bother if talent does not recharge
        if (talent.getCooldown() == Constants.INFINITE_DURATION) {
            return;
        }
        
        // Always cancel the task
        if (task != null) {
            task.cancel();
            task = null;
        }
        
        task = new Task();
    }
    
    private class Task extends GameTask {
        
        private final long startedAt;
        private final int cooldown;
        
        private Task() {
            this.startedAt = System.currentTimeMillis();
            this.cooldown = player.cooldownManager.scaleCooldown(talent.getCooldown(), false);
            
            runTaskLater(cooldown);
        }
        
        @Override
        public void run() {
            incrementCharge();
            
            // Reschedule the task if queued talent
            if (queue > 0) {
                queue--;
                createTask();
            }
            else {
                // Nullate task to indicate that nothing on cooldown
                task = null;
            }
        }
    }
    
}
