package me.hapyl.fight.game.heroes.bounty_hunter;

import me.hapyl.eterna.module.reflect.glowing.GlowingColor;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.Outline;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.util.RemovableCause;
import me.hapyl.fight.util.StringRandom;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class BloodBounty implements RemovableCause<BloodBounty.RemoveCause> {
    
    private static final String[] bountyFailedMessages = { "I... lose....", "That's... it...", "It least I can finally rest..." };
    private static final String[] bountyCompleteMessages = { "Bounty complete!", "Well fought.", "I win." };
    
    private final GamePlayer player;
    private final LivingGameEntity entity;
    
    private int hits;
    
    BloodBounty(GamePlayer player, LivingGameEntity entity) {
        this.player = player;
        this.entity = entity;
        
        entity.setGlowingFor(player, GlowingColor.RED);
        
        // Glowing for player target and notify
        if (entity instanceof GamePlayer playerEntity) {
            player.setGlowingFor(playerEntity, GlowingColor.RED);
            
            playerEntity.setOutline(Outline.RED);
            
            playerEntity.sendTitle("&4⚔", "&6%s is hunting you!".formatted(player.getName()), 5, 15, 5);
            playerEntity.playSound(Sound.ENCHANT_THORNS_HIT, 0.75f);
            playerEntity.playSound(Sound.ENTITY_WITHER_SPAWN, 2.0f);
        }
    }
    
    @Nonnull
    public LivingGameEntity entity() {
        return entity;
    }
    
    public int incrementHit() {
        hits++;
        return hits;
    }
    
    @Override
    public void remove(@Nonnull RemoveCause cause) {
        entity.setGlowingFor(player);
        
        if (entity instanceof GamePlayer playerEntity) {
            player.setGlowingFor(playerEntity);
            playerEntity.setOutline(Outline.CLEAR);
        }
        
        final BountyHunter bountyHunter = HeroRegistry.BOUNTY_HUNTER;
        
        switch (cause) {
            case PLAYER_DIED -> bountyHunter.talk(player, StringRandom.of(bountyFailedMessages));
            case ENTITY_DIED -> bountyHunter.talk(player, StringRandom.of(bountyCompleteMessages));
        }
        
        // Start the cooldown unless player died
        if (cause != RemoveCause.PLAYER_DIED) {
            bountyHunter.getWeapon().ability.startCooldown(player);
        }
    }
    
    public enum RemoveCause {
        ENTITY_DIED,
        PLAYER_DIED,
        BOUNTY_COMPLETE
    }
}
