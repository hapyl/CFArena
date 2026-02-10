package me.hapyl.fight.game.commission;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.MapMaker;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.attribute.*;
import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

public class Commission {

    public static final int MAX_PLAYER_LEVEL;
    public static final int MAX_LEVEL = 200;
    
    private static final double FLAT_HEALTH_BONUS = 1000;
    
    private static final Map<AttributeType, DoubleScale> ATTRIBUTE_SCALE_PER_LEVEL;
    private static final LinkedHashMap<Integer, Long> EXP_MAP;
    private static final ModifierSource MODIFIER_SOURCE;

    static {
        MAX_PLAYER_LEVEL = 50;

        ATTRIBUTE_SCALE_PER_LEVEL
                = MapMaker.<AttributeType, DoubleScale>ofLinkedHashMap()
                          .put(AttributeType.MAX_HEALTH, level -> 1 + Math.log(level) * Math.exp(level * 0.05d) * 3)
                          .put(AttributeType.ATTACK, level -> 1 + Math.log(level) * Math.pow(level - 1, 1.5d) * 0.5d)
                          .put(AttributeType.DEFENSE, level -> 1 + 0.1d * level - 0.1d)
                          .makeImmutableMap();

        EXP_MAP = Maps.newLinkedHashMap();
        MODIFIER_SOURCE = new ModifierSource(Key.ofString("blood_blessing"), true);

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

        return MAX_PLAYER_LEVEL;
    }

    @Nonnull
    public static Map<AttributeType, DoubleScale> attributeScalePerLevel() {
        return ATTRIBUTE_SCALE_PER_LEVEL;
    }

    public static void scaleAttributes(@Nonnull EntityAttributes attributes, int level) {
        attributes.addModifier(MODIFIER_SOURCE, Constants.INFINITE_DURATION, modifier -> {
            ATTRIBUTE_SCALE_PER_LEVEL.forEach((type, scale) -> {
                modifier.of(type, ModifierType.ADDITIVE, scale.scale(level));
                
                // Add flat health bonus to players because I don't know how to scale shit
                if (attributes.getEntity() instanceof GamePlayer && type == AttributeType.MAX_HEALTH) {
                    modifier.of(AttributeType.MAX_HEALTH, ModifierType.FLAT, FLAT_HEALTH_BONUS);
                }
            });
        });
    }

}
