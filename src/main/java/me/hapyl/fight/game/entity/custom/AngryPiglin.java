package me.hapyl.fight.game.entity.custom;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.Attributes;
import me.hapyl.fight.game.entity.EntityType;
import me.hapyl.fight.game.entity.GameEntityType;
import me.hapyl.fight.game.entity.NamedGameEntity;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.entity.Piglin;

import javax.annotation.Nonnull;

public class AngryPiglin extends GameEntityType<Piglin> {
    public AngryPiglin() {
        super("Angry Piglin", Piglin.class);

        final Attributes attributes = getAttributes();
        attributes.setHealth(100);
        attributes.setAttack(50);

        setType(EntityType.HOSTILE);
    }

    @Nonnull
    @Override
    public NamedGameEntity<Piglin> create(@Nonnull Piglin entity) {
        return new Instance(this, entity);
    }

    private static class Instance extends NamedGameEntity<Piglin> {

        private static final int MAX_TICK = Tick.fromSecond(33);

        public Instance(GameEntityType<Piglin> type, Piglin entity) {
            super(type, entity);
        }

        @Override
        public void onSpawn() {
            entity.setImmuneToZombification(true);
        }

        @Override
        public void onTick() {
            attributes.addSilent(AttributeType.ATTACK, 0.025);

            if (getTick() >= MAX_TICK) {
                forceRemove();
                return;
            }
        }

        @Override
        public String[] getExtraHologramLines() {
            return new String[] {
                    "&4Attack Boost &l" + AttributeType.ATTACK.toString(attributes.get(AttributeType.ATTACK)) + "%",
                    "&aTime Left: &l" + ((MAX_TICK - getTick()) / 20) + "s"
            };
        }
    }
}
