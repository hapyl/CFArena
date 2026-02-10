package me.hapyl.fight.game.heroes.bloodfield;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.Outline;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.bloodfiend.BloodfiendPassive;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class BiteData {
    
    private static final ModifierSource modifierSource = new ModifierSource(Key.ofString("bloodfiend_bite"));
    
    private final GamePlayer player;
    private final LivingGameEntity entity;
    private final double healthSacrifice;
    
    protected int tick;
    
    BiteData(GamePlayer player, LivingGameEntity entity) {
        this.player = player;
        this.entity = entity;
        this.healthSacrifice = entity.getMaxHealth() * -TalentRegistry.SUCCULENCE.healthDeduction; // Remember to invert since by the rules multiplier constants must be negative for de-buffs
    }
    
    public void bite(int duration) {
        final boolean firstBite = tick == 0;
        
        this.tick = duration;
        
        // Don't do anything if not first hit
        if (!firstBite) {
            return;
        }
        
        final double health = entity.getHealth();
        final BloodfiendPassive talent = TalentRegistry.SUCCULENCE;
        
        entity.getAttributes().addModifier(
                modifierSource, Constants.INFINITE_DURATION, player, modifier ->
                        modifier.of(AttributeType.MAX_HEALTH, ModifierType.ADDITIVE, talent.healthDeduction)
        );
        
        if (health > entity.getMaxHealth()) {
            entity.setHealth(entity.getMaxHealth());
        }
        
        // Fx (Only show for players)
        if (!(entity instanceof GamePlayer entityPlayer)) {
            return;
        }
        
        entityPlayer.setOutline(Outline.RED);
        entityPlayer.sendMessage("&6&l🦇 &e%s has bitten you! &c-%.0f ❤".formatted(
                this.player.getName(),
                healthSacrifice
        ));
        entityPlayer.playSound(Sound.ENTITY_BAT_DEATH, 0.75f);
        entityPlayer.playSound(Sound.ENTITY_ZOMBIE_HURT, 0.75f);
    }
    
    public void remove() {
        entity.getAttributes().removeModifier(modifierSource);
        
        if (!(entity instanceof GamePlayer entityPlayer)) {
            return;
        }
        
        // Fx
        entityPlayer.setOutline(Outline.CLEAR);
        entityPlayer.sendMessage("&6&l🦇 &e&oMuch better! &a+%.0f ❤".formatted(healthSacrifice));
        entityPlayer.playSound(Sound.ENTITY_HORSE_SADDLE, 0.75f);
        entityPlayer.playSound(Sound.ENTITY_WARDEN_HEARTBEAT, 0.0f);
    }
    
    @Nonnull
    public LivingGameEntity getEntity() {
        return entity;
    }
    
}
