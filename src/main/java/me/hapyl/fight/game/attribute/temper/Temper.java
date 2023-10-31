package me.hapyl.fight.game.attribute.temper;

import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.archive.pytaria.FlowerBreeze;
import me.hapyl.fight.util.Described;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum Temper implements Described {

    COMMAND("command", "command temper", true) {
        @Override
        protected void affect(@Nonnull EntityAttributes attributes, double value, int duration) {
            attributes.decreaseTemporary(this, AttributeType.FEROCITY, value, duration);
        }
    }, // for testing

    // Positive
    FLOWER_BREEZE("Flower Breeze", "Grants attack and defense boost.", true) {
        @Override
        protected void affect(@Nonnull EntityAttributes attributes, double value, int duration) {
            final FlowerBreeze talent = Talents.FLOWER_BREEZE.getTalent(FlowerBreeze.class);

            attributes.increaseTemporary(Temper.FLOWER_BREEZE, AttributeType.ATTACK, talent.attackIncrease, getDuration());
            attributes.increaseTemporary(Temper.FLOWER_BREEZE, AttributeType.DEFENSE, talent.defenseIncrease, getDuration());
        }
    },
    BERSERK_MODE("Berserk", "Berserk mode.", true) {
        @Override
        protected void affect(@Nonnull EntityAttributes attributes, double value, int duration) {
            attributes.increaseTemporary(Temper.BERSERK_MODE, AttributeType.ATTACK, 0.5d, duration);
            attributes.increaseTemporary(Temper.BERSERK_MODE, AttributeType.SPEED, 0.05d, duration);
            attributes.increaseTemporary(Temper.BERSERK_MODE, AttributeType.CRIT_CHANCE, 0.4d, duration);
            attributes.decreaseTemporary(Temper.BERSERK_MODE, AttributeType.DEFENSE, 0.7d, duration);

            final LivingGameEntity entity = attributes.getGameEntity();

            entity.spawnBuffDisplay(Named.BERSERK.toString(), 30);
            entity.sendMessage("%s &aYou're berserk!", Named.BERSERK.getCharacter());
        }
    },

    // Negative
    POISON_IVY("Poison Ivy", "Reduces defense.", false) {
        @Override
        protected void affect(@Nonnull EntityAttributes attributes, double value, int duration) {
            attributes.decreaseTemporary(this, AttributeType.DEFENSE, value, duration);
        }
    },

    INQUISITION("Inquisition", "Reduces all attributes by 20%.", false) {
        @Override
        public String getMessage() {
            return "Inquisition is at nigh!";
        }

        @Override
        public void affect(@Nonnull EntityAttributes attributes, double v2, int duration) {
            // Prevent infinite stat reduction
            if (attributes.hasTemper(this)) {
                return;
            }

            for (AttributeType type : AttributeType.values()) {
                final double value = attributes.get(type);
                if (value <= 0) {
                    continue;
                }

                attributes.decreaseTemporary(this, type, value * 0.8d, duration);
            }
        }
    },

    RADIATION("Radiation", "Reduces mending.", false),
    WYVERN_HEART("Wyvern Heart", "", false),
    ENDER_TELEPORT("Ender Teleport", "", false),

    ;

    public static final String PREFIX = "&d🌶 &7";

    private final String name;
    private final String description;
    private final boolean isBuff;

    Temper(@Nonnull String name, @Nonnull String description, boolean isBuff) {
        this.name = name;
        this.description = description;
        this.isBuff = isBuff;
    }

    public boolean isBuff() {
        return isBuff;
    }

    public boolean isDebuff() {
        return !isBuff();
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }

    public int getDuration() {
        return 100;
    }

    public double getValue() {
        return 0.5d;
    }

    @Nullable
    public String getMessage() {
        return null;
    }

    public final void temper(@Nonnull LivingGameEntity gameEntity, double value, int duration) {
        temper(gameEntity.getAttributes(), value, duration);
    }

    public final void temper(@Nonnull EntityAttributes attributes, double value, int duration) {
        final String message = getMessage();

        // Only send a message if not tempered
        if (!attributes.hasTemper(this) && message != null) {
            attributes.getGameEntity().sendMessage(PREFIX + message);
        }

        affect(attributes, value, duration);
    }

    public final void temper(@Nonnull EntityAttributes attributes, int duration) {
        temper(attributes, getValue(), duration);
    }

    public final void temper(@Nonnull EntityAttributes attributes) {
        temper(attributes, getValue(), getDuration());
    }

    protected void affect(@Nonnull EntityAttributes attributes, double value, int duration) {
        throw new IllegalArgumentException(getName() + " does not support affect!");
    }

}
