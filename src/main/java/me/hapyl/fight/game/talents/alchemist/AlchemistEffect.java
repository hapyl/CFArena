package me.hapyl.fight.game.talents.alchemist;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public interface AlchemistEffect {
    
    @Nonnull
    String name();
    
    @Nonnull
    Color color();
    
    void onHit(@Nonnull LivingGameEntity entity, @Nonnull GamePlayer alchemist);
    
    default void applyMadness(@Nonnull LivingGameEntity entity, @Nonnull GamePlayer alchemist) {
        onHit(entity, alchemist);
        
        // Fx
        entity.sendMessage("&4&l\uD83D\uDCA2 &eAlchemical Madness %s&e!".formatted(name()));
        
        entity.playSound(Sound.ENTITY_WITCH_HURT, 0.0f);
        entity.playSound(Sound.ENTITY_WITCH_DRINK, 0.75f);
        
        // Potion fx
        splashPotionFx(entity.getEyeLocation(), color());
    }
    
    static void splashPotionFx(@Nonnull Location location, @Nonnull Color color) {
        location.getWorld().spawn(
                location, ThrownPotion.class, self -> {
                    self.setVelocity(new Vector(0, -1, 0));
                    
                    final PotionMeta meta = self.getPotionMeta();
                    meta.setColor(color);
                    
                    self.setPotionMeta(meta);
                }
        );
    }
    
    
}
