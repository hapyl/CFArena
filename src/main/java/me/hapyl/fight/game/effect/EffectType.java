package me.hapyl.fight.game.effect;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.game.color.Color;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApiStatus.NonExtendable
public final class EffectType implements Iterable<Effect> {
    
    // *=* Vanilla Effects *=*
    // Not all effects are present because some are either useless or replaced with an attribute.
    
    public static final Effect NAUSEA;
    public static final Effect FIRE_RESISTANCE;
    public static final Effect WATER_BREATHING;
    public static final Effect BLINDNESS;
    public static final Effect NIGHT_VISION;
    public static final Effect POISON;
    public static final Effect WITHER;
    public static final Effect LEVITATION;
    public static final Effect SLOW_FALLING;
    public static final Effect DARKNESS;
    
    // *=* Custom Effects *=*
    public static final ParanoiaEffect PARANOIA;
    public static final AmnesiaEffect AMNESIA;
    public static final FallDamageResistanceEffect FALL_DAMAGE_RESISTANCE;
    public static final VulnerableEffect VULNERABLE;
    public static final InvisibilityEffect INVISIBLE;
    public static final RespawnResistanceEffect RESPAWN_RESISTANCE;
    public static final ArcaneMuteEffect ARCANE_MUTE;
    public static final BleedEffect BLEED;
    public static final ParachuteEffect PARACHUTE;
    public static final MovementContainmentEffect MOVEMENT_CONTAINMENT;
    public static final LingerEffect LINGER;
    public static final DazeEffect DAZE;
    
    private static final Map<Key, Effect> effects;
    
    static {
        effects = Maps.newHashMap();
        
        NAUSEA = of("nausea", key -> new VanillaEffect(key, "\uD83E\uDD22", "Nausea", Color.MOSS_GREEN, PotionEffectType.NAUSEA, Type.NEGATIVE));
        FIRE_RESISTANCE = of("fire_resistance", key -> new VanillaEffect(key, "\uD83D\uDD25", "Fire Resistance", Color.BURNT_ORANGE, PotionEffectType.FIRE_RESISTANCE, Type.POSITIVE));
        WATER_BREATHING = of("water_breathing", key -> new VanillaEffect(key, "&l\uD83E\uDEE7", "Water Breathing", Color.MINT_CYAN, PotionEffectType.WATER_BREATHING, Type.POSITIVE));
        BLINDNESS = of(
                "blindness", key -> new VanillaEffect(key, "\uD83D\uDC41", "Blindness", Color.CHARCOAL, PotionEffectType.BLINDNESS, Type.NEGATIVE) {
                    @Override
                    public void onStart(@Nonnull ActiveEffect effect) {
                        effect.entity().addPotionEffect(PotionEffectType.BLINDNESS, effect.amplifier(), effect.duration());
                    }
                    
                    @Override
                    public void onStop(@Nonnull ActiveEffect effect) {
                    }
                }
        );
        NIGHT_VISION = of("night_vision", key -> new VanillaEffect(key, "\uD83D\uDC41", "Night Vision", Color.EMERALD_GREEN, PotionEffectType.NIGHT_VISION, Type.POSITIVE));
        POISON = of("poison", key -> new VanillaEffect(key, "☣", "Poison", Color.NEON_GREEN, PotionEffectType.POISON, Type.NEGATIVE));
        WITHER = of("wither", key -> new VanillaEffect(key, "☠", "Wither", Color.SLATE_GRAY, PotionEffectType.WITHER, Type.NEGATIVE));
        LEVITATION = of("levitation", key -> new VanillaEffect(key, "☁", "Levitation", Color.PEACH, PotionEffectType.LEVITATION, Type.NEUTRAL));
        SLOW_FALLING = of("slow_falling", key -> new VanillaEffect(key, "\uD83C\uDF41", "Slow Falling", Color.DUSTY_BLUE, PotionEffectType.SLOW_FALLING, Type.NEUTRAL));
        DARKNESS = of("darkness", key -> new VanillaEffect(key, "\uD83D\uDC26", "Darkness", Color.MIDNIGHT_BLUE, PotionEffectType.DARKNESS, Type.NEGATIVE));
        
        PARANOIA = of("paranoia", ParanoiaEffect::new);
        AMNESIA = of("amnesia", AmnesiaEffect::new);
        FALL_DAMAGE_RESISTANCE = of("fall_damage_resistance", FallDamageResistanceEffect::new);
        VULNERABLE = of("vulnerable", VulnerableEffect::new);
        INVISIBLE = of("invisibility", InvisibilityEffect::new);
        RESPAWN_RESISTANCE = of("respawn_resistance", RespawnResistanceEffect::new);
        ARCANE_MUTE = of("arcane_mute", ArcaneMuteEffect::new);
        BLEED = of("bleed", BleedEffect::new);
        PARACHUTE = of("parachute", ParachuteEffect::new);
        MOVEMENT_CONTAINMENT = of("movement_containment", MovementContainmentEffect::new);
        LINGER = of("linger", LingerEffect::new);
        DAZE = of("daze", DazeEffect::new);
    }
    
    @Nonnull
    @Override
    public Iterator<Effect> iterator() {
        return effects.values().stream().iterator();
    }
    
    @Nonnull
    public static Set<Effect> ofType(@Nonnull Type type) {
        return effects.values()
                      .stream()
                      .filter(effect -> effect.getType() == type)
                      .collect(Collectors.toSet());
    }
    
    @Nonnull
    public static Set<Effect> ofFlag(@Nonnull EffectFlag flag) {
        return effects.values()
                      .stream()
                      .filter(effect -> {
                          final boolean vanilla = effect instanceof VanillaEffect;
                          
                          return switch (flag) {
                              case VANILLA -> vanilla;
                              case CUSTOM -> !vanilla;
                              default -> true;
                          };
                      })
                      .collect(Collectors.toSet());
    }
    
    @Nullable
    public static Effect byKey(@Nonnull Key key) {
        return effects.get(key);
    }
    
    @Nonnull
    public static Set<String> keys() {
        return effects.keySet().stream()
                .map(Key::getKey)
                .collect(Collectors.toSet());
    }
    
    @Nonnull
    public static Effect randomOfType(@Nonnull Type type) {
        return CollectionUtils.randomElementOrFirst(ofType(type));
    }
    
    @Nonnull
    public static Effect randomOfFlag(@Nonnull EffectFlag flag) {
        return CollectionUtils.randomElementOrFirst(ofFlag(flag));
    }
    
    private static <E extends Effect> E of(String key, Function<Key, E> fn) {
        final Key realkey = Key.ofString(key);
        
        if (effects.containsKey(realkey)) {
            throw new IllegalArgumentException("Key %s is already registered!".formatted(key));
        }
        
        final E effect = fn.apply(realkey);
        
        effects.put(realkey, effect);
        return effect;
    }
    
}
