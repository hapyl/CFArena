package me.hapyl.fight.game.effect;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.eterna.module.util.Described;
import me.hapyl.fight.game.effect.effects.*;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

// FIXME (Tue, Feb 18 2025 @xanyjl): Yep
public enum EffectType implements Described {

    // *=* Vanilla effects *=* //
    // Not all effects are present due to
    // some being disabled or just useless
    // for the game.
    SPEED(new VanillaEffect("Speed", PotionEffectType.SPEED, Type.POSITIVE)),
    SLOW(new VanillaEffect("Slowness", PotionEffectType.SLOWNESS, Type.NEGATIVE)),
    HASTE(new VanillaEffect("Haste", PotionEffectType.HASTE, Type.NEUTRAL)),
    MINING_FATIGUE(new VanillaEffect("Mining Fatigue", PotionEffectType.MINING_FATIGUE, Type.NEUTRAL)),
    JUMP_BOOST(new VanillaEffect("Jump Boost", PotionEffectType.JUMP_BOOST, Type.POSITIVE)),
    NAUSEA(new VanillaEffect("Nausea", PotionEffectType.NAUSEA, Type.NEGATIVE)),
    FIRE_RESISTANCE(new VanillaEffect("Fire Resistance", PotionEffectType.FIRE_RESISTANCE, Type.POSITIVE)),
    WATER_BREATHING(new VanillaEffect("Water Breathing", PotionEffectType.WATER_BREATHING, Type.POSITIVE)),
    BLINDNESS(new VanillaEffect("Blindness", PotionEffectType.BLINDNESS, Type.NEGATIVE) {
        @Override
        public void onStart(@Nonnull LivingGameEntity entity, int amplifier, int duration) {
            entity.addPotionEffect(PotionEffectType.BLINDNESS, amplifier, duration);
        }

        @Override
        public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {
        }
    }),
    NIGHT_VISION(new VanillaEffect("Night Vision", PotionEffectType.NIGHT_VISION, Type.POSITIVE)),
    POISON(new VanillaEffect("Poison", PotionEffectType.POISON, Type.NEGATIVE)),
    WITHER(new VanillaEffect("Wither", PotionEffectType.WITHER, Type.NEGATIVE)),
    GLOWING(new VanillaEffect("Glowing", PotionEffectType.GLOWING, Type.NEUTRAL)),
    LEVITATION(new VanillaEffect("Levitation", PotionEffectType.LEVITATION, Type.NEUTRAL)),
    SLOW_FALLING(new VanillaEffect("Slow Falling", PotionEffectType.SLOW_FALLING, Type.NEUTRAL)),
    DARKNESS(new VanillaEffect("Darkness", PotionEffectType.DARKNESS, Type.NEGATIVE)),

    // *=* Custom effects *=* //

    CORROSION(new Corrosion()),
    PARANOIA(new ParanoiaEffect()),
    AMNESIA(new Amnesia()),
    FALL_DAMAGE_RESISTANCE(new FallDamageResistance()),
    MOVEMENT_CONTAINMENT(new MovementContainment()),
    VULNERABLE(new Vulnerable()),
    IMMOVABLE(new Immovable()),
    INVISIBILITY(new Invisibility()),
    RESPAWN_RESISTANCE(new RespawnResistance()),
    RIPTIDE(new Riptide()),
    ARCANE_MUTE(new ArcaneMuteEffect()),
    SLOWING_AURA(new SlowingAuraEffect()),
    BLEED(new BleedEffect()),
    ORC_GROWL(new OrcGrowlEffect()),
    WITHER_BLOOD(new WitherBlood()),
    SADNESS(new SadnessEffect()),
    CHILL_AURA(new ChillAuraEffect()),
    PARACHUTE(new ParachuteEffect()),

    ;

    private static final Set<EffectType> vanillaEffects;
    private static final Map<Effect, EffectType> byHandle;

    static {
        vanillaEffects = Sets.newHashSet();
        byHandle = Maps.newHashMap();

        for (EffectType effect : values()) {
            if (effect.effect instanceof VanillaEffect) {
                vanillaEffects.add(effect);
            }

            byHandle.put(effect.effect, effect);
        }
    }

    private final Effect effect;

    EffectType(Effect effect) {
        this.effect = effect;
    }

    @Nonnull
    public Effect getEffect() {
        return effect;
    }

    @Nonnull
    @Override
    public String getName() {
        return effect.getName();
    }

    @Nonnull
    @Override
    public String getDescription() {
        return effect.getDescription();
    }

    /**
     * Gets all the {@link EffectType} with the given {@link Type}.
     *
     * @param type - Type.
     * @return set of effects with the matching type.
     */
    @Nonnull
    public static Set<EffectType> getEffects(@Nonnull Type type) {
        return CFUtils.setOfEnum(EffectType.class, effect -> effect.getEffect().getType() == type);
    }

    /**
     * Gets an enum {@link EffectType} by the {@link Effect} handle.
     *
     * @param effect - Handle.
     * @return an enum or null.
     */
    @Nullable
    public static EffectType byHandle(@Nonnull Effect effect) {
        return byHandle.get(effect);
    }

    /**
     * Gets all the {@link EffectType} with the given {@link EffectFlag}.
     *
     * @param flag - Flag.
     * @return set of effects with the matching flag.
     */
    @Nonnull
    public static Set<EffectType> getEffects(@Nonnull EffectFlag flag) {
        return CFUtils.setOfEnum(
                EffectType.class, effect -> {
            final boolean vanilla = effect.effect instanceof VanillaEffect;

            return switch (flag) {
                case VANILLA -> vanilla;
                case CUSTOM -> !vanilla;
                default -> true;
            };
        });
    }

    /**
     * Gets a random {@link EffectType} with the given {@link Type}.
     *
     * @param type - Type.
     * @return a random effect.
     */
    @Nonnull
    public static EffectType getRandomEffect(@Nonnull Type type) {
        return CollectionUtils.randomElement(getEffects(type), SPEED);
    }

    /**
     * Gets a random {@link EffectType} with the given {@link EffectFlag}.
     *
     * @param flag - Flag.
     * @return a random effect.
     */
    @Nonnull
    public static EffectType getRandomEffect(@Nonnull EffectFlag flag) {
        return CollectionUtils.randomElement(getEffects(flag), SPEED);
    }

}
