package me.hapyl.fight.game.entity.named;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.attribute.BaseAttributes;
import me.hapyl.fight.game.entity.EntityType;
import org.bukkit.Location;
import org.bukkit.entity.Husk;

import javax.annotation.Nonnull;

public class Abobo extends NamedEntityType {
    public Abobo(@Nonnull Key key) {
        super(key, "Abobo");

        setType(EntityType.BOSS);

        final BaseAttributes attributes = getAttributes();
        attributes.setMaxHealth(250);
        attributes.setDefense(150);
    }

    @Nonnull
    @Override
    public NamedGameEntity<?> create(@Nonnull Location location) {
        return CF.createEntity(location, Entities.HUSK, self -> {
            return new NamedGameEntity<Husk>(Abobo.this, self) {
                @Override
                public void onDamageTaken(@Nonnull DamageInstance instance) {
                    Debug.info("ABOBO took damage from " + instance.getDamager());
                }

                @Override
                public void onDeath() {
                    super.onDeath();
                    Debug.info("ABOBO DIED NO!!!!!!!!!!!!!!!!!!!!");
                }

                @Override
                public void onDamageDealt(@Nonnull DamageInstance instance) {
                    Debug.info("ABOBO hit " + instance.getEntity());
                }

                @Override
                public void onTick(int tick) {

                }
            };
        });
    }

}
