package me.hapyl.fight.util.hitbox;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Location;
import org.bukkit.entity.Slime;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

public interface Hitbox {
    
    void onDespawn();
    
    default void onDamageTaken(@Nonnull DamageInstance instance) {
    }
    
    default void onTeammateDamage(@Nonnull LivingGameEntity lastDamager) {
    }
    
    default void onInteract(@Nonnull GamePlayer player) {
    }
    
    @Nonnull
    default String getName() {
        return "Hitbox";
    }
    
    @Nonnull
    static <T extends HitboxEntity> T create(@Nonnull Location location, double health, double scale, @Nonnull BiFunction<Slime, Double, T> fn) {
        return CF.createEntity(
                location, Entities.SLIME, self -> {
                    self.setSize(1);
                    self.setAI(false);
                    self.setSilent(true);
                    self.setInvisible(true);
                    
                    // Apply health
                    final T hitbox = fn.apply(self, scale);
                    hitbox.overrideHealth(health);
                    
                    hitbox.setImmune(DamageCause.SUFFOCATION, DamageCause.FALL);
                    hitbox.setInformImmune(false);
                    
                    return hitbox;
                }
        );
    }
    
    @Nonnull
    static HitboxEntity create(@Nonnull Location location, double health, double size, @Nonnull Hitbox hitbox) {
        return create(
                location, health, size, (slime, scale) -> new HitboxEntity(slime, scale) {
                    @Override
                    public void onDespawn() {
                        hitbox.onDespawn();
                    }
                    
                    @Override
                    public void onDamageTaken(@Nonnull DamageInstance instance) {
                        hitbox.onDamageTaken(instance);
                    }
                    
                    @Override
                    public void onTeammateDamage(@Nonnull LivingGameEntity lastDamager) {
                        hitbox.onTeammateDamage(lastDamager);
                    }
                    
                    @Override
                    public void onInteract(@Nonnull GamePlayer player) {
                        hitbox.onInteract(player);
                    }
                    
                    @Nonnull
                    @Override
                    public String getName() {
                        return hitbox.getName();
                    }
                }
        );
    }
    
}
