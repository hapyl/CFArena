package me.hapyl.fight.game.effect;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.game.effect.archive.*;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Described;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public enum Effects implements Described {

    // *=* Vanilla effects *=* //
    // Not all effects are present due to
    // some being disabled or just useless
    // for the game.
    SPEED(new VanillaEffect("Speed", PotionEffectType.SPEED, EffectType.POSITIVE)),
    SLOW(new VanillaEffect("Slowness", PotionEffectType.SLOW, EffectType.NEGATIVE)),
    HASTE(new VanillaEffect("Haste", PotionEffectType.FAST_DIGGING, EffectType.POSITIVE)),
    MINING_FATIGUE(new VanillaEffect("Mining Fatigue", PotionEffectType.SLOW_DIGGING, EffectType.NEGATIVE)),
    JUMP_BOOST(new VanillaEffect("Jump Boost", PotionEffectType.JUMP, EffectType.POSITIVE)),
    NAUSEA(new VanillaEffect("Nausea", PotionEffectType.CONFUSION, EffectType.NEGATIVE)),
    FIRE_RESISTANCE(new VanillaEffect("Fire Resistance", PotionEffectType.FIRE_RESISTANCE, EffectType.POSITIVE)),
    WATER_BREATHING(new VanillaEffect("Water Breathing", PotionEffectType.WATER_BREATHING, EffectType.POSITIVE)),
    BLINDNESS(new VanillaEffect("Blindness", PotionEffectType.BLINDNESS, EffectType.NEGATIVE) {
        @Override
        public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {
            super.onStop(entity, amplifier);
            // Fade out
            entity.addPotionEffect(PotionEffectType.BLINDNESS, 100, 10);
        }
    }),
    NIGHT_VISION(new VanillaEffect("Night Vision", PotionEffectType.NIGHT_VISION, EffectType.POSITIVE)),
    POISON(new VanillaEffect("Poison", PotionEffectType.POISON, EffectType.NEGATIVE)),
    WITHER(new VanillaEffect("Wither", PotionEffectType.WITHER, EffectType.NEGATIVE)),
    GLOWING(new VanillaEffect("Glowing", PotionEffectType.GLOWING, EffectType.NEUTRAL)),
    LEVITATION(new VanillaEffect("Levitation", PotionEffectType.LEVITATION, EffectType.NEUTRAL)),
    SLOW_FALLING(new VanillaEffect("Slow Falling", PotionEffectType.SLOW_FALLING, EffectType.NEUTRAL)),
    DARKNESS(new VanillaEffect("Darkness", PotionEffectType.DARKNESS, EffectType.NEGATIVE)),

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

    ;

    private static final Set<Effects> vanillaEffects;
    private static final Map<Effect, Effects> byHandle;

    static {
        vanillaEffects = Sets.newHashSet();
        byHandle = Maps.newHashMap();

        for (Effects effect : values()) {
            if (effect.effect instanceof VanillaEffect) {
                vanillaEffects.add(effect);
            }

            byHandle.put(effect.effect, effect);
        }
    }

    private final Effect effect;

    Effects(Effect effect) {
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
     * Gets all the {@link Effects} with the given {@link EffectType}.
     *
     * @param type - Type.
     * @return set of effects with the matching type.
     */
    @Nonnull
    public static Set<Effects> getEffects(@Nonnull EffectType type) {
        return CFUtils.setOfEnum(Effects.class, effect -> effect.getEffect().getType() == type);
    }

    /**
     * Gets an enum {@link Effects} by the {@link Effect} handle.
     *
     * @param effect - Handle.
     * @return an enum or null.
     */
    @Nullable
    public static Effects byHandle(@Nonnull Effect effect) {
        return byHandle.get(effect);
    }

    /**
     * Gets all the {@link Effects} with the given {@link EffectFlag}.
     *
     * @param flag - Flag.
     * @return set of effects with the matching flag.
     */
    @Nonnull
    public static Set<Effects> getEffects(@Nonnull EffectFlag flag) {
        return CFUtils.setOfEnum(Effects.class, effect -> {
            final boolean vanilla = effect.effect instanceof VanillaEffect;

            return switch (flag) {
                case VANILLA -> vanilla;
                case CUSTOM -> !vanilla;
                default -> true;
            };
        });
    }

    /**
     * Gets a random {@link Effects} with the given {@link EffectType}.
     *
     * @param type - Type.
     * @return a random effect.
     */
    @Nonnull
    public static Effects getRandomEffect(@Nonnull EffectType type) {
        return CollectionUtils.randomElement(getEffects(type), SPEED);
    }

    /**
     * Gets a random {@link Effects} with the given {@link EffectFlag}.
     *
     * @param flag - Flag.
     * @return a random effect.
     */
    @Nonnull
    public static Effects getRandomEffect(@Nonnull EffectFlag flag) {
        return CollectionUtils.randomElement(getEffects(flag), SPEED);
    }

}
