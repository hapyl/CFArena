package me.hapyl.fight.game.effect;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.cooldown.EntityCooldown;
import org.bukkit.Input;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class AmnesiaEffect extends Effect {
    
    private static final EntityCooldown COOLDOWN = EntityCooldown.of("amnesia");
    
    AmnesiaEffect(Key key) {
        super(key, "\uD83D\uDD00", "Amnesia", Color.STEEL_GRAY, Type.NEGATIVE);
        
        setDescription("""
                       Impairs movement and vision.
                       """);
    }
    
    @Override
    public void onStart(@Nonnull ActiveEffect effect) {
        effect.entity().addPotionEffectIndefinitely(PotionEffectType.NAUSEA, 1);
    }
    
    @Override
    public void onStop(@Nonnull ActiveEffect effect) {
        effect.entity().removePotionEffect(PotionEffectType.NAUSEA);
    }
    
    @Override
    public void onTick(@Nonnull ActiveEffect effect) {
        // Affect
        final LivingGameEntity entity = effect.entity();
        
        if (!(entity instanceof GamePlayer player) || isHorizontalInput(player.input())) {
            pushRandomly(entity);
        }
        
        // Fx
        if (entity.aliveTicks() % 25 == 0) {
            entity.playSound(Sound.ENTITY_WARDEN_AMBIENT, 2.0f);
        }
    }
    
    private void pushRandomly(LivingGameEntity entity) {
        if (entity.hasCooldown(COOLDOWN) || !entity.hasEffect(EffectType.AMNESIA)) {
            return;
        }
        
        final double x = entity.random.nextBoolean() ? 0.2 : -0.2;
        final double z = entity.random.nextBoolean() ? 0.2 : -0.2;
        
        entity.setVelocity(new Vector(x, -BukkitUtils.GRAVITY, z));
        entity.startCooldown(COOLDOWN, 100);
    }
    
    private boolean isHorizontalInput(Input input) {
        return input.isForward() || input.isBackward() || input.isRight() || input.isLeft();
    }
}
