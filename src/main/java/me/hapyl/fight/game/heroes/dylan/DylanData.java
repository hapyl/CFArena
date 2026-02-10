package me.hapyl.fight.game.heroes.dylan;

import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.dylan.DylanPassive;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class DylanData extends PlayerData implements Ticking {
    
    public DylanFamiliar familiar;
    protected Rebuke rebuke;
    
    public DylanData(@Nonnull GamePlayer player) {
        super(player);
    }
    
    @Override
    public void remove() {
        if (familiar != null) {
            familiar.remove();
            familiar = null;
        }
        
        if (rebuke != null) {
            rebuke.cancel();
            rebuke = null;
        }
    }
    
    public void allowRebuke(@Nonnull LivingGameEntity entity, @Nonnull DamageInstance instance) {
        final DylanPassive passive = TalentRegistry.DYLAN_PASSIVE;
        final int threshold = passive.threshold + Math.min(player.getPing(), 150) / 10;
        
        this.rebuke = new Rebuke(player, entity, instance.getDamage(), threshold) {
            @Override
            public void cancel(@Nonnull Type type) {
                super.cancel(type);
                
                // Applying cooldown here for consistency
                if (type == Type.EXPIRED) {
                    passive.startCooldown(player, 60);
                }
                else {
                    passive.startCooldown(player);
                }
                
                rebuke = null;
            }
        };
        
        // Fx
        player.playSound(Sound.ENTITY_BLAZE_DEATH, 2.0f);
        player.playSound(Sound.ENTITY_BLAZE_HURT, 1.75f);
    }
    
    public void tick() {
        if (familiar == null) {
            return;
        }
        
        // Check for death
        if (familiar.entity().isDead()) {
            familiar = null;
            return;
        }
        
        familiar.tick();
    }
    
}
