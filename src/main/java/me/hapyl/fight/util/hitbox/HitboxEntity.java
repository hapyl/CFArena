package me.hapyl.fight.util.hitbox;

import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.Attributes;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;

public class HitboxEntity extends LivingGameEntity {

    private final Hitbox hitbox;
    private final String name;

    HitboxEntity(@Nonnull LivingEntity entity, @Nonnull String name, double health, @Nonnull Hitbox hitbox) {
        super(entity, defaultAttributes(health));

        this.hitbox = hitbox;
        this.name = name;
    }

    @Override
    public void onDeath() {
        super.onDeath();
        hitbox.onDeath();
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
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

    private static Attributes defaultAttributes(double health) {
        final Attributes attributes = new Attributes();

        attributes.setMaxHealth(health);
        attributes.set(AttributeType.EFFECT_RESISTANCE, 1.0d); // Kinda cheap way but it's ok

        return attributes;
    }
}
