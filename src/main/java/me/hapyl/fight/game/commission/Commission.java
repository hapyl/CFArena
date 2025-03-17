package me.hapyl.fight.game.commission;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.MapMaker;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

public class Commission {

    public static final int MAX_PLAYER_LEVEL;
    public static final int MAX_LEVEL = 200;

    private static final Map<AttributeType, DoubleScale> ATTRIBUTE_SCALE_PER_LEVEL;
    private static final LinkedHashMap<Integer, Long> EXP_MAP;

    static {
        MAX_PLAYER_LEVEL = 50;

        ATTRIBUTE_SCALE_PER_LEVEL
                = MapMaker.<AttributeType, DoubleScale>ofLinkedHashMap()
                          .put(AttributeType.MAX_HEALTH, level -> 1 + Math.log(level) * Math.exp(level * 0.05d) * 3)
                          .put(AttributeType.ATTACK, level -> 1 + Math.log(level) * Math.pow(level - 1, 1.5d) * 0.5d)
                          .put(AttributeType.DEFENSE, level -> 1 + 0.1d * level - 0.1d)
                          .makeImmutableMap();

        EXP_MAP = Maps.newLinkedHashMap();

        final int baseXP = 100;

        for (int level = 1; level <= MAX_PLAYER_LEVEL; level++) {
            final double scalingFactor = 1.21d;
            final long exp = (long) Math.ceil(baseXP * Math.pow(scalingFactor, level - 1));

            EXP_MAP.put(level, exp);
        }
    }

    public static long expNeededForLevel(int level) {
        return EXP_MAP.getOrDefault(level, Long.MAX_VALUE);
    }

    public static int levelByExp(long exp) {
        for (Map.Entry<Integer, Long> entry : EXP_MAP.entrySet()) {
            final int level = entry.getKey();
            final long experience = entry.getValue();

            if (experience >= exp) {
                return level;
            }
        }

        return 0;
    }

    @Nonnull
    public static Map<AttributeType, DoubleScale> attributeScalePerLevel() {
        return ATTRIBUTE_SCALE_PER_LEVEL;
    }

    public static void scaleAttributes(@Nonnull EntityAttributes attributes, int level) {
        ATTRIBUTE_SCALE_PER_LEVEL.forEach((type, scale) -> {
            final double base = attributes.getBase(type);
            final double scaled = base * scale.scale(level);

            attributes.set(type, scaled - base);
        });
    }

}
