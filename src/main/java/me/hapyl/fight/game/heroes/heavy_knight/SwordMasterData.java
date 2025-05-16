package me.hapyl.fight.game.heroes.heavy_knight;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.cooldown.EntityCooldown;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.heavy_knight.Slash;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class SwordMasterData extends PlayerData {
    
    private static final EntityCooldown cooldown = EntityCooldown.of("perfect_sequence", 5_000L);
    
    protected final SwordMaster hero;
    protected final BufferedOrder<Talent> buffer;
    
    private final Slash slash;
    
    protected EmpoweredSwordTask empowered;
    
    SwordMasterData(SwordMaster hero, GamePlayer player) {
        super(player);
        
        this.hero = hero;
        this.slash = hero.getThirdTalent();
        
        this.buffer = new BufferedOrder<>(slash.perfectSequenceWindow, hero.getFirstTalent(), hero.getSecondTalent(), slash) {
            @Override
            public void onCorrectOrder() {
                clear();
                player.startCooldown(cooldown);
            }
            
            @Override
            public boolean offer(@Nonnull Talent talent) {
                if (player.hasCooldown(cooldown)) {
                    return false;
                }
                
                return super.offer(talent);
            }
        };
    }
    
    public void empowerWeapon() {
        empowered = new EmpoweredSwordTask(this);
    }
    
    @Override
    public void remove() {
        buffer.clear();
        
        if (empowered != null) {
            empowered.cancel();
            empowered = null;
        }
    }
    
    public static class EmpoweredSwordTask extends GameTask {
        
        private final SwordMasterData data;
        
        EmpoweredSwordTask(SwordMasterData data) {
            this.data = data;
            
            // Fx
            final ItemStack weapon = data.player.getItem(HotBarSlot.WEAPON);
            
            if (weapon != null) {
                weapon.addEnchantment(Enchantment.UNBREAKING, 1);
            }
            
            runTaskLater(data.slash.empoweredSlashDuration);
        }
        
        @Override
        public void run() {
            HeroRegistry.SWORD_MASTER.getWeapon().give(data.player);
            data.empowered = null;
        }
    }
}
