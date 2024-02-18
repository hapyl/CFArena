package me.hapyl.fight.game.entity.custom.voids;

import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.attribute.Attributes;
import me.hapyl.fight.game.entity.*;
import me.hapyl.fight.game.entity.event.EventType;
import me.hapyl.spigotutils.module.entity.Entities;
import org.bukkit.Sound;
import org.bukkit.entity.Enderman;

import javax.annotation.Nonnull;

public class VoidAbomination extends GameEntityType<Enderman> {
    public VoidAbomination() {
        super("Void Abomination", Enderman.class);

        final Attributes attributes = getAttributes();
        attributes.setHealth(1_000_000_000);
        attributes.setSpeed(0.05);

        setType(EntityType.BOSS);
    }

    @Override
    public double getHologramOffset() {
        return 3.0d;
    }

    @Nonnull
    @Override
    public NamedGameEntity<Enderman> create(@Nonnull Enderman bukkitEntity) {
        return new VoidAbominationEntity(bukkitEntity);
    }

    private class VoidAbominationEntity extends MultiPartLivingGameEntity<Enderman> {
        public VoidAbominationEntity(Enderman bukkitEntity) {
            super(VoidAbomination.this, bukkitEntity);

            final MultiPartLivingGameEntity<Enderman>.Part<Enderman> part = createPart(
                    Entities.ENDERMAN,
                    self -> {
                        self.flip();
                        VoidAbominationEntity.this.addPassenger(self);
                    }
            );

            createPart(Entities.ENDERMAN, self -> {
                part.entity.addPassenger(self.getEntity());
                //part.flip();
            });

            listenTo(EventType.TARGET, ev -> {
                ev.setCancelled(true);
                ev.setTarget(null);
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
