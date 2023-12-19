package me.hapyl.fight.game.entity.custom;

import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.attribute.Attributes;
import me.hapyl.fight.game.entity.EntityType;
import me.hapyl.fight.game.entity.GameEntityType;
import me.hapyl.fight.game.entity.NamedGameEntity;
import org.bukkit.entity.Husk;

import javax.annotation.Nonnull;

public class Abobo extends GameEntityType<Husk> {
    public Abobo() {
        super("Abobo", Husk.class);

        setType(EntityType.BOSS);

        final Attributes attributes = getAttributes();
        attributes.setHealth(250);
        attributes.setDefense(150);
    }

    @Nonnull
    @Override
    public NamedGameEntity<Husk> create(@Nonnull Husk entity) {
        return new NamedGameEntity<>(this, entity) {
            @Override
            public void onDamageTaken(@Nonnull DamageInstance instance) {
                Debug.info("ABOBO took damage from " + instance.getDamager());
            }

            @Override
            public void onDeath() {
                Debug.info("ABOBO DIED NO!!!!!!!!!!!!!!!!!!!!");
            }

            @Override
            public void onDamageDealt(@Nonnull DamageInstance instance) {
                Debug.info("ABOBO hit " + instance.getEntity());
            }

            @Override
            public void onTick() {
                Debug.info("ABOBO IS TICKING!!!");
            }
        };
    }

}
