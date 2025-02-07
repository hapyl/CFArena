package me.hapyl.fight.game.entity.named;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.attribute.Attributes;
import me.hapyl.fight.game.entity.EntityType;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.MultiPartLivingGameEntity;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Enderman;

import javax.annotation.Nonnull;

public class VoidAbomination extends NamedEntityType {
    public VoidAbomination(@Nonnull Key key) {
        super(key, "Void Abomination");

        final Attributes attributes = getAttributes();
        attributes.setMaxHealth(1_000_000_000);
        attributes.setSpeed(0.05);

        setType(EntityType.BOSS);
    }

    @Override
    public double getHologramOffset() {
        return 3.0d;
    }

    @Nonnull
    @Override
    public NamedGameEntity<?> create(@Nonnull Location location) {
        return CF.createEntity(location, Entities.ENDERMAN, Entity::new);
    }

    private class Entity extends MultiPartLivingGameEntity<Enderman> {
        public Entity(Enderman bukkitEntity) {
            super(VoidAbomination.this, bukkitEntity);

            final MultiPartLivingGameEntity<Enderman>.Part<Enderman> part = createPart(
                    Entities.ENDERMAN,
                    self -> {
                        self.flip();
                        Entity.this.addPassenger(self);
                    }
            );

            createPart(Entities.ENDERMAN, self -> {
                part.entity.addPassenger(self.getEntity());
                //part.flip();
            });
        }

        @Override
        public void onDamageTaken(@Nonnull DamageInstance instance) {
            final LivingGameEntity damager = instance.getDamager();

            if (damager != null) {
                damager.sendMessage("&5&l&k1 &cThis creature is immune to this kind of damage! &5&l&k1");
                damager.playSound(Sound.ENTITY_IRON_GOLEM_HURT, 1.25f);
            }

            instance.setCancelled(true);
        }
    }
}
