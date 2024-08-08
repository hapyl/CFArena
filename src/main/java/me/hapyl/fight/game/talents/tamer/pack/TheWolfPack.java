package me.hapyl.fight.game.talents.tamer.pack;

import me.hapyl.fight.game.attribute.Attribute;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.eterna.module.entity.Entities;
import org.bukkit.Location;
import org.bukkit.entity.Wolf;

import javax.annotation.Nonnull;

public class TheWolfPack extends TamerPack {

    @DisplayField(percentage = true) private final double wolfBaseAttackBoostPerWold = 0.1d;
    @DisplayField private final int wolfAttackBoostDuration = 20;

    public TheWolfPack() {
        super("The Wolf Pack", """
                &nEach&a alive&7 wolf grants you an %s boost.
                """.formatted(AttributeType.ATTACK), TalentType.ENHANCE);

        attributes.setHealth(25);
        setDurationSec(25);
    }

    @Override
    public void onSpawn(@Nonnull ActiveTamerPack pack, @Nonnull Location location) {
        pack.createEntity(location, Entities.WOLF, entity -> new TheWoldPackEntity(pack, entity));
    }

    @Override
    public int spawnAmount() {
        return 4;
    }

    @Nonnull
    @Override
    public String toString(ActiveTamerPack pack) {
        final int size = pack.getEntities().size();
        final Attribute attribute = AttributeType.ATTACK.attribute;

        return attribute.getColor() + attribute.getCharacter() + " " + size;
    }

    private class TheWoldPackEntity extends TamerEntity<Wolf> {

        public TheWoldPackEntity(@Nonnull ActiveTamerPack pack, @Nonnull Wolf entity) {
            super(pack, entity);
        }

        @Override
        public void tick(int index) {
            super.tick(index);
            final EntityAttributes playerAttributes = player.getAttributes();

            playerAttributes.increaseTemporary(
                    Temper.TAMER_WOLF,
                    AttributeType.ATTACK,
                    (1 + index) * scaleUltimateEffectiveness(player, wolfBaseAttackBoostPerWold),
                    wolfAttackBoostDuration
            );
        }

    }
}
