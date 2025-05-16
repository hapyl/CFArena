package me.hapyl.fight.game.effect;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.event.custom.TalentPreconditionEvent;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class ArcaneMuteEffect extends Effect implements Listener {
    ArcaneMuteEffect(Key key) {
        super(key, "⛧", "Arcane Mute", Color.BLOOD_PURPLE, Type.NEGATIVE);
        
        setDescription("""
                       Prevents players from using their talents and deafens them.
                       """);
    }
    
    @EventHandler()
    public void handlePrecondition(TalentPreconditionEvent ev) {
        final GamePlayer player = ev.getPlayer();
        
        if (player.hasEffect(this)) {
            ev.setCancelled(true, "Arcane Mute!");
        }
    }
    
    @Override
    public void onStart(@Nonnull ActiveEffect effect) {
        final LivingGameEntity entity = effect.entity();
        
        entity.sendTitle(getPrefixColored(), "&dShhhhh...", 10, 20, 10);
        entity.playSound(Sound.ENTITY_SILVERFISH_HURT, 0.0f);
    }
    
    @Override
    public void onStop(@Nonnull ActiveEffect effect) {
    }
    
}
