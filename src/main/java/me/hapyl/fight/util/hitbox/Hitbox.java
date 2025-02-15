package me.hapyl.fight.util.hitbox;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Location;
import org.jetbrains.annotations.Range;

import javax.annotation.Nonnull;

public interface Hitbox {

    void onSpawn(@Nonnull HitboxEntity entity);

    void onDeath();

    default void onDamageTaken(@Nonnull DamageInstance instance) {
    }

    default void onTeammateDamage(@Nonnull LivingGameEntity lastDamager) {
    }

    default void onInteract(@Nonnull GamePlayer player) {
    }

    @Nonnull
    static HitboxEntity create(@Nonnull Location location, @Nonnull String name, double health, @Nonnull Hitbox hitbox, @Range(from = 1, to = 10) int size) {
        return CF.createEntity(
                location, Entities.SLIME, self -> {
                    self.setSize(size);
                    self.setAI(false);
                    self.setSilent(true);
                    self.setInvisible(true);

                    final HitboxEntity entity = new HitboxEntity(self, name, health, hitbox);

                    // Call onSpawn after the entity has spawned!
                    hitbox.onSpawn(entity);

                    return entity;
                }
        );
    }

}
