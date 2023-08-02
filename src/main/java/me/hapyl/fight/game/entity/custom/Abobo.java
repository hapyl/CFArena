package me.hapyl.fight.game.entity.custom;

import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.attribute.Attributes;
import me.hapyl.fight.game.entity.EntityType;
import me.hapyl.fight.game.entity.GameEntityType;
import me.hapyl.fight.game.entity.NamedGameEntity;
import org.bukkit.entity.Husk;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
            public DamageOutput onDamageTaken(@Nonnull DamageInput input) {
                Debug.info("ABOBO took damage from " + input.getDamager());
                return DamageOutput.CANCEL;
            }

            @Override
            public void onDeath() {
                Debug.info("ABOBO DIED NO!!!!!!!!!!!!!!!!!!!!");
            }

            @Nullable
            @Override
            public DamageOutput onDamageDealt(@Nonnull DamageInput input) {
                Debug.info("ABOBO hit " + input.getEntity());
                return null;
            }

            @Override
            public void onTick() {
                Debug.info("ABOBO IS TICKING!!!");
            }
        };
    }

}
