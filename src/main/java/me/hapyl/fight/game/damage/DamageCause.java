package me.hapyl.fight.game.damage;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.fight.game.DamageFormat;
import me.hapyl.fight.util.CloneableKeyed;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class DamageCause implements Keyed, CloneableKeyed {

    public static final DamageCause ENTITY_ATTACK;
    public static final DamageCause PROJECTILE;
    public static final DamageCause FALL;
    public static final DamageCause FIRE;
    public static final DamageCause FIRE_TICK;
    public static final DamageCause LAVA;
    public static final DamageCause DROWNING;
    public static final DamageCause BLOCK_EXPLOSION;
    public static final DamageCause ENTITY_EXPLOSION;
    public static final DamageCause VOID;
    public static final DamageCause POISON;
    public static final DamageCause MAGIC;
    public static final DamageCause WITHER;
    public static final DamageCause FALLING_BLOCK;
    public static final DamageCause DRAGON_BREATH;
    public static final DamageCause CRAMMING;
    public static final DamageCause CONTACT;
    public static final DamageCause ENTITY_SWEEP_ATTACK;
    public static final DamageCause SUFFOCATION;
    public static final DamageCause MELTING;
    public static final DamageCause LIGHTNING;
    public static final DamageCause SUICIDE;
    public static final DamageCause STARVATION;
    public static final DamageCause THORNS;
    public static final DamageCause FLY_INTO_WALL;
    public static final DamageCause HOT_FLOOR;
    public static final DamageCause DRYOUT;
    public static final DamageCause FREEZE;
    public static final DamageCause SONIC_BOOM;

    public static final DamageCause FEROCITY;
    public static final DamageCause LIBRARY_VOID;
    public static final DamageCause COLD;
    public static final DamageCause DWARF_LAVA;
    public static final DamageCause STEAM;

    public static final DamageCause NOVA_EXPLOSION;
    public static final DamageCause SHOCK_DART;
    public static final DamageCause BOOM_BOW;
    public static final DamageCause FIRE_MOLOTOV;
    public static final DamageCause FIRE_SPRAY;
    public static final DamageCause FROZEN_WEAPON;
    public static final DamageCause LEASHED;
    public static final DamageCause SOUL_WHISPER;
    public static final DamageCause TOXIN;
    public static final DamageCause METEORITE;
    public static final DamageCause MOON_PILLAR;
    public static final DamageCause WITHER_SKULLED;
    public static final DamageCause GRAVITY_GUN;
    public static final DamageCause PLUNGE;
    public static final DamageCause BLACK_HOLE;
    public static final DamageCause DARKNESS;
    public static final DamageCause THROWING_STARS;
    public static final DamageCause STARFALL;
    public static final DamageCause GOLDEN_PATH;
    public static final DamageCause FLOWER;
    public static final DamageCause FEEL_THE_BREEZE;
    public static final DamageCause NEVERMISS;
    public static final DamageCause FEET_ATTACK;
    public static final DamageCause SUBMERGE;
    public static final DamageCause SOTS;
    public static final DamageCause STAR_SLASH;
    public static final DamageCause RAINFIRE;
    public static final DamageCause SWEEP;
    public static final DamageCause RIFLE;
    public static final DamageCause SATCHEL;
    public static final DamageCause TORNADO;
    public static final DamageCause RIPTIDE;
    public static final DamageCause LASER;
    public static final DamageCause WATER;
    public static final DamageCause SWARM;
    public static final DamageCause TROLL_LAUGH;
    public static final DamageCause BLOCK_SHIELD;
    public static final DamageCause DECOY;
    public static final DamageCause MINION;
    public static final DamageCause RIP_BONES;
    public static final DamageCause AURA_OF_CIRCUS;
    public static final DamageCause BLEED;
    public static final DamageCause SHOTGUN;
    public static final DamageCause BACKSTAB;
    public static final DamageCause WITHERBORN;
    public static final DamageCause EMBODIMENT_OF_DEATH;
    public static final DamageCause SHREDS_AND_PIECES;
    public static final DamageCause DARKNESS_CURSE;
    public static final DamageCause CORROSION;
    public static final DamageCause ORC_DASH;
    public static final DamageCause ORC_WEAPON;
    public static final DamageCause CYCLING_AXE;
    public static final DamageCause FROSTBITE;
    public static final DamageCause POISON_IVY;
    public static final DamageCause IMPEL;
    public static final DamageCause TWINCLAW;
    public static final DamageCause CANDLEBANE;
    public static final DamageCause RADIATION;
    public static final DamageCause SOULS_REBOUND;
    public static final DamageCause GRAVITY;
    public static final DamageCause ENDER_TELEPORT;
    public static final DamageCause DARK_ENERGY;
    public static final DamageCause SHADOW_CLONE;
    public static final DamageCause STONE_CASTLE;
    public static final DamageCause SENTRY_SHOT;
    public static final DamageCause HACK;
    public static final DamageCause BLADE_BARRAGE;
    public static final DamageCause TOTEM;
    public static final DamageCause RAY_OF_DEATH;
    public static final DamageCause ROGUE_ATTACK;
    public static final DamageCause THROWING_KNIFE;
    public static final DamageCause PIPE_BOMB;
    public static final DamageCause UPPERCUT;
    public static final DamageCause RANGE_ATTACK;
    public static final DamageCause ICICLE;
    public static final DamageCause CELESTE_ARROW;
    public static final DamageCause CHAOS;
    public static final DamageCause SHARK_BITE;
    public static final DamageCause NYX_SPIKE;
    public static final DamageCause SPIKE_SHIELD;
    public static final DamageCause THE_JOKER;
    public static final DamageCause ECHO;
    public static final DamageCause RONIN_HIT;
    public static final DamageCause DEFLECT;
    public static final DamageCause BAT_BITE;
    public static final DamageCause BAT_BITE_NO_TICK;
    public static final DamageCause DEAD_EYE;
    public static final DamageCause VAMPIRE_BITE;
    public static final DamageCause GAMBLE;
    public static final DamageCause POTION;
    public static final DamageCause MADNESS;
    public static final DamageCause ABYSS_CURSE;

    // Private fields
    private static final int DEFAULT_ATTACK_COOLDOWN;
    private static final int DEFAULT_NO_DAMAGE_TICKS;
    private static final Map<Key, DamageCause> BY_KEY;

    static {
        DEFAULT_ATTACK_COOLDOWN = 10;
        DEFAULT_NO_DAMAGE_TICKS = 10;
        BY_KEY = Maps.newHashMap();

        /*/ ⬇️ Register below ⬇️ /*/

        // Base causes
        ENTITY_ATTACK = of(Key.ofString("entity_attack"), "was killed", "by").flags(DamageFlag.MELEE);
        PROJECTILE = of(Key.ofString("projectile"), "was shot", "by").flags(DamageFlag.PROJECTILE);

        // Vanilla causes, needed for vanilla damage
        FALL = minecraft(Key.ofString("fall"), "fell to their death", "while escaping from").flags(DamageFlag.PIERCING_DAMAGE);
        FIRE = minecraft(Key.ofString("fire"), "was toasted", "with help from").damageFormat(instance -> "&6%.0f 🔥".formatted(instance.getDamage()));
        FIRE_TICK = FIRE.cloneAs(Key.ofString("fire_tick"));
        LAVA = FIRE.cloneAs(Key.ofString("lava"));
        DROWNING = minecraft(Key.ofString("drowning"), "drowned", "with help from");
        BLOCK_EXPLOSION = minecraft(Key.ofString("block_explosion"), "was exploded", "by");
        ENTITY_EXPLOSION = BLOCK_EXPLOSION.cloneAs(Key.ofString("entity_explosion"));
        VOID = minecraft(Key.ofString("void"), "fell into the void", "with help from");
        POISON = minecraft(Key.ofString("poison"), "was poisoned to death", "by").removeFlags(DamageFlag.CAN_KILL).damageFormat(instance -> "&a%.0f ☣".formatted(instance.getDamage()));
        MAGIC = minecraft(Key.ofString("magic"), "magically died", "with help from");
        WITHER = minecraft(Key.ofString("wither"), "withered to death", "by");
        FALLING_BLOCK = minecraft(Key.ofString("falling_block"), "should've been wearing a helmet", "and {damager} knew that");
        DRAGON_BREATH = minecraft(Key.ofString("dragon_breath"), "didn't like the smell of dragon", "wait... it's not a dragon, it's");
        CRAMMING = minecraft(Key.ofString("cramming"), "was crushed by {damager}'s weight");
        CONTACT = minecraft(Key.ofString("contact"), "wanted a hug, but {damager} didn't");
        ENTITY_SWEEP_ATTACK = ENTITY_ATTACK.cloneAs(Key.ofString("entity_sweep_attack"));
        SUFFOCATION = minecraft(Key.ofString("suffocation"), "couldn't hold their breath", "and {damager} was watching, menacingly");
        MELTING = minecraft(Key.ofString("melting"), "is now a puddle of water", "isn't that fun, {damager}?");
        LIGHTNING = minecraft(Key.ofString("lightning"), "was struck by lightning", "by").flags(DamageFlag.IGNORES_INVULNERABILITY_TICKS_AND_ATTACK_COOLDOWN);
        SUICIDE = minecraft(Key.ofString("suicide"), "died", "with help from");
        STARVATION = minecraft(Key.ofString("starvation"), "starved to death", "because {damager} didn't share their food");
        THORNS = minecraft(Key.ofString("thorns"), "was prickled to death", "by");
        FLY_INTO_WALL = minecraft(Key.ofString("fly_into_wall"), "hit the wall at 69,420 mph", "while running from");
        HOT_FLOOR = minecraft(Key.ofString("hot_floor"), "didn't know that floor was lava", "and {damager} didn't tell them");
        DRYOUT = minecraft(Key.ofString("dryout"), "thought it was water, it wasn't", "and {damager} was there to watch");
        FREEZE = minecraft(Key.ofString("freeze"), "froze to death", "while running from");
        SONIC_BOOM = minecraft(Key.ofString("sonic_boom"), "was BOOM BOOM BAKUDAN'ed", "by {damager}");

        // Development causes
        FEROCITY = ofNonCrit(Key.ofString("ferocity"), "was ferociously killed", "by").flags(DamageFlag.IGNORES_INVULNERABILITY_TICKS_AND_ATTACK_COOLDOWN);
        LIBRARY_VOID = ofNonCrit(Key.ofString("library_void"), "was consumed by §kthe void").flags(DamageFlag.ENVIRONMENT);
        COLD = ofNonCrit(Key.ofString("cold"), "froze to death", "with help from").flags(DamageFlag.ENVIRONMENT);
        DWARF_LAVA = ofNonCrit(Key.ofString("dwarf_lava"), "didn't bounce high enough", "and {damager} was just stood there, menacingly").flags(DamageFlag.ENVIRONMENT);
        STEAM = ofNonCrit(Key.ofString("steam"), "was steamed to death", "with help from").flags(DamageFlag.ENVIRONMENT);

        // Custom causes
        NOVA_EXPLOSION = of(Key.ofString("nova_explosion"), "has been split into atoms", "by");
        SHOCK_DART = of(Key.ofString("shock_dart"), "was shocked", "by");
        BOOM_BOW = ofNonCrit(Key.ofString("boom_bow"), "went out with a BIG BANG", "with help from").flags(DamageFlag.TRUE_DAMAGE);
        FIRE_MOLOTOV = of(Key.ofString("fire_molotov"), "couldn't find a way out of {damager}'s fire");
        FIRE_SPRAY = of(Key.ofString("fire_spray"), "got sprayed to death", "by");
        FROZEN_WEAPON = of(Key.ofString("frozen_weapon"), "has been frozen to death", "by");
        LEASHED = of(Key.ofString("leashed"), "leashed to death", "by");
        SOUL_WHISPER = ofNonCrit(Key.ofString("soul_whisper"), "has entered {damager}'s souls collection");
        // This is a special case, because the toxin itself can't kill,
        // but called dieBy(DamageCause) bypasses the CAN_KILL flag
        TOXIN = ofNonCrit(Key.ofString("toxin"), "felt the abyssal contamination", "while trying to fight").removeFlags(DamageFlag.CAN_KILL);
        METEORITE = ofNonCrit(Key.ofString("meteorite"), "felt the wrath of the rock", "of");
        MOON_PILLAR = of(Key.ofString("moon_pillar"), "couldn't handle the beat", "of");
        WITHER_SKULLED = of(Key.ofString("wither_skulled"), "was scared to death", "by");
        GRAVITY_GUN = of(Key.ofString("gravity_gun"), "clearly couldn't see {damager}'s block of the size of their head flying in their direction...");
        PLUNGE = of(Key.ofString("plunge"), "was stepped on", "by");
        BLACK_HOLE = of(Key.ofString("black_hole"), "was sucked into the black hole", "created by");
        DARKNESS = of(Key.ofString("darkness"), "was blinded to death", "by");
        THROWING_STARS = ofNonCrit(Key.ofString("throwing_stars"), "felt the absolute pain of {damager}'s dagger");
        STARFALL = of(Key.ofString("starfall"), "doesn't know what danger looks like, yes {damager}?");
        GOLDEN_PATH = of(Key.ofString("golden_path"), "couldn't fight against their willpower", "created by shine of");
        FLOWER = ofNonCrit(Key.ofString("flower"), "was pruned to death", "by");
        FEEL_THE_BREEZE = ofNonCrit(Key.ofString("feel_the_breeze"), "felt {damager}'s breeze...");
        NEVERMISS = of(Key.ofString("nevermiss"), "couldn't dodge {damager}'s attack, what a noob...");
        FEET_ATTACK = of(Key.ofString("feet_attack"), "probably lost their toe", "isn't that right, {damager}?");
        SUBMERGE = of(Key.ofString("submerge"), "didn't know that Sharks bite", "but thanks to the {damager}, now they know...");
        SOTS = ofNonCrit(Key.ofString("sots"), "couldn't hide from {damager}'s stars").attackCooldown(2); // FIXME (Sat, Feb 15 2025 @xanyjl): Requires impl change
        STAR_SLASH = ofNonCrit(Key.ofString("star_slash"), "was slashed in half", "by");
        RAINFIRE = of(Key.ofString("rainfire"), "thought it's raining, but in reality it was {damager}'s arrows...");
        SWEEP = of(Key.ofString("sweep"), "was swept to death", "by");
        RIFLE = of(Key.ofString("rifle"), "had their brain exploded in cool slow-mo", "by");
        SATCHEL = of(Key.ofString("satchel"), "had their last flights", "with");
        TORNADO = of(Key.ofString("tornado"), "couldn't find the wind", "of");
        RIPTIDE = ofNonCrit(Key.ofString("riptide"), "was splashed to death", "by").flags(DamageFlag.IGNORES_INVULNERABILITY_TICKS_AND_ATTACK_COOLDOWN);
        LASER = of(Key.ofString("laser"), "was lasered to death", "by").flags(DamageFlag.IGNORES_INVULNERABILITY_TICKS_AND_ATTACK_COOLDOWN);
        WATER = of(Key.ofString("water"), "really liked the water").flags(DamageFlag.ENVIRONMENT, DamageFlag.IGNORES_INVULNERABILITY_TICKS_AND_ATTACK_COOLDOWN);
        SWARM = of(Key.ofString("swarm"), "was swarmed to death by {damager}'s bats").flags(DamageFlag.IGNORES_INVULNERABILITY_TICKS_AND_ATTACK_COOLDOWN);
        TROLL_LAUGH = ofNonCrit(Key.ofString("troll_laugh"), "was trolled to death", "by");
        BLOCK_SHIELD = of(Key.ofString("block_shield"), "was hit by {damager}'s circling block");
        DECOY = of(Key.ofString("decoy"), "was bamboozled", "by");
        MINION = of(Key.ofString("minion"), "was killed by {damager}'s minion");
        RIP_BONES = of(Key.ofString("rip_bones"), "was ripped to shreds", "by");
        AURA_OF_CIRCUS = of(Key.ofString("aura_of_circus"), "was furiously tamed", "by");
        BLEED = ofNonCrit(Key.ofString("bleed"), "bled to death from {damager}'s touch").flags(DamageFlag.IGNORES_INVULNERABILITY_TICKS_AND_ATTACK_COOLDOWN);
        SHOTGUN = of(Key.ofString("shotgun"), "was shot to death", "by");
        BACKSTAB = ofNonCrit(Key.ofString("backstab"), "was stabbed in the back", "by");
        WITHERBORN = ofNonCrit(Key.ofString("witherborn"), "was withered to death by {damager}'s Witherborn");
        EMBODIMENT_OF_DEATH = ofNonCrit(Key.ofString("embodiment_of_death"), "was bodied to death", "by").flags(DamageFlag.IGNORES_INVULNERABILITY_TICKS_AND_ATTACK_COOLDOWN); // FIXME (Sat, Feb 15 2025 @xanyjl): Impl change
        SHREDS_AND_PIECES = ofNonCrit(Key.ofString("shreds_and_pieces"), "was tear to shreds and pieces :o");
        DARKNESS_CURSE = of(Key.ofString("darkness_curse"), "was swallowed by {damager}'s darkness");
        CORROSION = ofNonCrit(Key.ofString("corrosion"), "corroded to death", "with help from").flags(DamageFlag.IGNORES_INVULNERABILITY_TICKS_AND_ATTACK_COOLDOWN);
        ORC_DASH = of(Key.ofString("orc_dash"), "was hit too hard", "by");
        ORC_WEAPON = ofNonCrit(Key.ofString("orc_weapon"), "was {damager}'s bullseye");
        CYCLING_AXE = of(Key.ofString("cycling_axe"), "couldn't see that {damager}'s axe is flying there");
        FROSTBITE = of(Key.ofString("frostbite"), "froze to death, and {damager} is the one to blame");
        POISON_IVY = ofNonCrit(Key.ofString("poison_ivy"), "was poised to death by {damager}'s poison ivy").attackCooldown(5);
        IMPEL = ofNonCrit(Key.ofString("impel"), "failed to obey {damager}'s command");
        TWINCLAW = ofNonCrit(Key.ofString("twinclaw"), "was pierced to death by {damager}'s claw");
        CANDLEBANE = ofNonCrit(Key.ofString("candlebane"), "was crushed by {damager}'s pillar");
        RADIATION = ofNonCrit(Key.ofString("radiation"), "was lasered to death", "by").flags(DamageFlag.TRUE_DAMAGE);
        SOULS_REBOUND = ofNonCrit(Key.ofString("souls_rebound"), "had their soul rebound", "by").flags(DamageFlag.TRUE_DAMAGE);
        GRAVITY = ofNonCrit(Key.ofString("gravity"), "felt the gravity of {damager}'s planet");
        ENDER_TELEPORT = ofNonCrit(Key.ofString("ender_teleport"), "was too scared of {damager}'s threatening aura");
        DARK_ENERGY = ofNonCrit(Key.ofString("dark_energy"), "was annihilated to death", "by");
        SHADOW_CLONE = ofNonCrit(Key.ofString("shadow_clone"), "was killed by {damager}'s shadow").flags(DamageFlag.IGNORES_INVULNERABILITY_TICKS_AND_ATTACK_COOLDOWN);
        STONE_CASTLE = ofNonCrit(Key.ofString("stone_castle"), "died because of {damager} while protecting their teammates").flags(DamageFlag.IGNORES_INVULNERABILITY_TICKS_AND_ATTACK_COOLDOWN);
        SENTRY_SHOT = ofNonCrit(Key.ofString("sentry_shot"), "was shot to death", "by {damager}'s sentry");
        HACK = ofNonCrit(Key.ofString("hack"), "was hacked", "by");
        BLADE_BARRAGE = ofNonCrit(Key.ofString("blade_barrage"), "fell before {damager}'s swords");
        TOTEM = of(Key.ofString("totem"), "was stomped on", "by").flags(DamageFlag.IGNORES_INVULNERABILITY_TICKS_AND_ATTACK_COOLDOWN);
        RAY_OF_DEATH = of(Key.ofString("ray_of_death"), "was doomed to fail", "before {damager}'s Ray of Death").flags(DamageFlag.IGNORES_INVULNERABILITY_TICKS_AND_ATTACK_COOLDOWN);
        ROGUE_ATTACK = ENTITY_ATTACK.cloneAs(Key.ofString("rogue_attack")).knockBack(0.5d);
        THROWING_KNIFE = ofNonCrit(Key.ofString("throwing_knife"), "was hit by a throwing knife", "of");
        PIPE_BOMB = ofNonCrit(Key.ofString("pipe_bomb"), "was blown away by {damager}'s Pipe Bomb").flags(DamageFlag.TRUE_DAMAGE);
        UPPERCUT = ofNonCrit(Key.ofString("uppercut"), "was upperCUT", "by");
        RANGE_ATTACK = PROJECTILE.cloneAs(Key.ofString("range_attack"));
        ICICLE = ofNonCrit(Key.ofString("icicle"), "was pierced by {damager}'s icicle");
        CELESTE_ARROW = ofNonCrit(Key.ofString("celeste_arrow"), "was somehow shot", "by");
        CHAOS = ofNonCrit(Key.ofString("chaos"), "was chaotically killed", "by").flags(DamageFlag.IGNORES_INVULNERABILITY_TICKS_AND_ATTACK_COOLDOWN);
        SHARK_BITE = ofNonCrit(Key.ofString("shark_bite"), "was bitten to death", "by");
        NYX_SPIKE = ofNonCrit(Key.ofString("nyx_spike"), "was pierced to death", "by").flags(DamageFlag.PIERCING_DAMAGE).attackCooldown(5);
        SPIKE_SHIELD = ofNonCrit(Key.ofString("spike_shield"), "was hit by {damager}'s spikes").flags(DamageFlag.TRUE_DAMAGE);
        THE_JOKER = ofNonCrit(Key.ofString("the_joker"), "'s death was yoinked", "by");
        ECHO = ofNonCrit(Key.ofString("echo"), "lost their body in {damager}'s monochrome world...");
        RONIN_HIT = ofNonCrit(Key.ofString("ronin_hit"), "lost in the duel", "against");
        DEFLECT = ofNonCrit(Key.ofString("deflect"), "was killed by {damager}'s deflected attack");
        BAT_BITE = ofNonCrit(Key.ofString("bat_bite"), "was bitten to death", "by").removeFlags(DamageFlag.CAN_CRIT);
        BAT_BITE_NO_TICK = BAT_BITE.cloneAs(Key.ofString("bat_bite_no_tick")).flags(DamageFlag.IGNORES_INVULNERABILITY_TICKS_AND_ATTACK_COOLDOWN);
        DEAD_EYE = of(Key.ofString("dead_eye"), "was dead eyed", "by");
        VAMPIRE_BITE = of(Key.ofString("vampire_bite"), "was bitten to death").knockBack(0.0d).flags(DamageFlag.MELEE);
        GAMBLE = ofNonCrit(Key.ofString("gamble"), "gambled their way to the grave", "by");
        POTION = ofNonCrit(Key.ofString("potion"), "was splashed by {damager}'s potion").flags(DamageFlag.IGNORES_INVULNERABILITY_TICKS_AND_ATTACK_COOLDOWN);
        MADNESS = ofNonCrit(Key.ofString("madness"), "was killed by mad {damager}");
        ABYSS_CURSE = ofNonCrit(Key.ofString("abyss_curse"), "was killed by {damager}'s curse");
    }

    private final Key key;
    private final DeathMessage deathMessage;
    private final Set<DamageFlag> flags;

    private DamageFormat damageFormat;
    private double knockBack;
    private int attackCooldown;

    private DamageCause(@Nonnull Key key, @Nonnull DeathMessage message) {
        this.key = key;
        this.deathMessage = message;
        this.flags = Sets.newHashSet(DamageFlag.CAN_CRIT, DamageFlag.CAN_KILL);
        this.damageFormat = DamageFormat.DEFAULT;
        this.knockBack = 1.0d;
        this.attackCooldown = defaultAttackCooldown();

        // Register for the vanilla causes
        final DamageCause previousCause = BY_KEY.put(key, this);

        if (previousCause != null) {
            throw new IllegalStateException("Duplicate damage cause registration! %s is already registered!".formatted(previousCause.key));
        }
    }

    @Override
    public DamageCause cloneAs(@Nonnull Key key) {
        final DamageCause clone = new DamageCause(key, deathMessage);
        clone.flags.addAll(this.flags);

        return clone;
    }

    public DamageCause attackCooldown(int attackCooldown) {
        this.attackCooldown = attackCooldown;
        return this;
    }

    public int attackCooldown() {
        return attackCooldown;
    }

    @Nonnull
    @Override
    public Key getKey() {
        return this.key;
    }

    public double knockBack() {
        return knockBack;
    }

    public DamageCause knockBack(@Range(from = 0, to = 1) double knockBack) {
        this.knockBack = knockBack;
        return this;
    }

    @Nonnull
    public DeathMessage getDeathMessage() {
        return deathMessage;
    }

    @Nonnull
    public DamageFormat damageFormat() {
        return damageFormat;
    }

    public DamageCause damageFormat(@Nonnull DamageFormat damageFormat) {
        this.damageFormat = damageFormat;
        return this;
    }

    public boolean hasFlag(@Nonnull DamageFlag flag) {
        return flags.contains(flag);
    }

    public DamageCause flags(@Nonnull DamageFlag... flags) {
        this.flags.addAll(Arrays.asList(flags));
        return this;
    }

    public DamageCause removeFlags(@Nonnull DamageFlag... flags) {
        Arrays.asList(flags).forEach(this.flags::remove);
        return this;
    }

    @Nonnull
    public Set<DamageFlag> getFlags() {
        return flags;
    }

    public DamageCause setFlags(@Nonnull DamageFlag... flags) {
        this.flags.clear();
        this.flags.addAll(Arrays.asList(flags));

        return this;
    }

    @Override
    public final boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final DamageCause that = (DamageCause) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(key);
    }

    /**
     * Returns {@code true} if this considered a 'direct' damage, meaning an attack from entity's hand or an arrow they show.
     *
     * @return true if this is a 'direct' damage.
     */
    public boolean isDirectDamage() {
        return hasFlag(DamageFlag.MELEE) || hasFlag(DamageFlag.PROJECTILE);
    }

    @Nonnull
    public String getReadableName() {
        return Chat.capitalize(key.getKey());
    }

    public static int defaultAttackCooldown() {
        return DEFAULT_ATTACK_COOLDOWN;
    }

    public static int defaultNoDamageTicks() {
        return DEFAULT_NO_DAMAGE_TICKS;
    }

    @Nullable
    public static DamageCause byKey(@Nonnull Key key) {
        return BY_KEY.get(key);
    }

    @Nullable
    public static DamageCause byBukkitCause(@Nonnull EntityDamageEvent.DamageCause cause) {
        return byKey(Key.ofString(cause.name().toLowerCase()));
    }

    @Nonnull
    public static List<String> keys() {
        return BY_KEY.keySet().stream().map(Key::getKey).toList();
    }

    @Nonnull
    public static List<DamageCause> values() {
        return BY_KEY.values().stream().toList();
    }

    /**
     * Creates a DamageCause that cannot crit.
     *
     * @param message - Message.
     * @param suffix  - Suffix.
     */
    @Nonnull
    private static DamageCause ofNonCrit(@Nonnull Key key, @Nonnull String message, @Nonnull String suffix) {
        return of(key, message, suffix).removeFlags(DamageFlag.CAN_CRIT);
    }

    /**
     * Creates a DamageCause that cannot crit.
     *
     * @param message - Message.
     */
    @Nonnull
    private static DamageCause ofNonCrit(@Nonnull Key key, @Nonnull String message) {
        return ofNonCrit(key, message, "");
    }

    /**
     * Creates a DamageCause that can crit.
     *
     * @param message - Message.
     * @param suffix  - Suffix.
     */
    @Nonnull
    private static DamageCause of(@Nonnull Key key, @Nonnull String message, @Nonnull String suffix) {
        return new DamageCause(key, new DeathMessage(message, suffix));
    }

    /**
     * Creates a DamageCause that can crit.
     *
     * @param message - Message.
     */
    @Nonnull
    private static DamageCause of(@Nonnull Key key, @Nonnull String message) {
        return of(key, message, "by");
    }

    /**
     * Creates a DamageCause that are minecraft vanilla and cannot crit.
     *
     * @param message - Message.
     * @param suffix  - Suffix.
     */
    @Nonnull
    private static DamageCause minecraft(@Nonnull Key key, @Nonnull String message, @Nonnull String suffix) {
        return of(key, message, suffix).setFlags(DamageFlag.ENVIRONMENT);
    }

    /**
     * Creates a DamageCause that are minecraft vanilla and cannot crit.
     *
     * @param message - Message.
     */
    @Nonnull
    private static DamageCause minecraft(@Nonnull Key key, @Nonnull String message) {
        return minecraft(key, message, "");
    }
}
