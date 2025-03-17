package me.hapyl.fight.game.entity.commission.crypt;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.BaseAttributes;
import me.hapyl.fight.game.entity.EntityType;
import me.hapyl.fight.game.entity.commission.CommissionEntity;
import me.hapyl.fight.game.entity.commission.CommissionEntityType;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public class LeechBoss extends CommissionEntityType {

    public LeechBoss(@Nonnull Key key) {
        super(
                key, "Leech", new BaseAttributes()
                        .put(AttributeType.MAX_HEALTH, 1_000)
                        .put(AttributeType.ATTACK, 200)
                        .put(AttributeType.DEFENSE, 200)
        );

        setDescription("""
                A leech boss, very scary and angry!
                """);

        setType(EntityType.BOSS);
    }

    @Nonnull
    @Override
    public CommissionEntity create(@Nonnull Location location) {
        throw new IllegalStateException("No.");
    }
}
