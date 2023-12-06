package me.hapyl.fight.game;

import me.hapyl.spigotutils.module.annotate.Super;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/**
 * This allows to display custom damage cause messages.
 * <p>
 * ToDo -> add many more, for each ability at least
 * ToDo -> add custom gradient colors (:
 */
public enum EnumDamageCause {

    /**
     * System damage causes, <b>do not</b> modify the order.
     */
    // Have to consider entity_attack as custom damage for display purpose
    ENTITY_ATTACK(DamageCause.of("was killed", "by")),
    // Use this for normal attacks that should not crit if too lazy to create a custom damage cause
    ENTITY_ATTACK_NON_CRIT(ENTITY_ATTACK.damageCause.clone().setCanCrit(false)),
    PROJECTILE(DamageCause.minecraft("was shot", "by").setCanCrit(true).setProjectile(true)),
    FALL(DamageCause.minecraft("fell to their death", "while escaping from")),
    FIRE(DamageCause.minecraft("was toasted", "with help from")),
    FIRE_TICK(FIRE.damageCause),
    LAVA(FIRE.damageCause),
    DROWNING(DamageCause.minecraft("drowned", "with help from")),
    BLOCK_EXPLOSION(DamageCause.minecraft("exploded", "by")),
    ENTITY_EXPLOSION(DamageCause.minecraft("exploded", "by")),
    VOID(DamageCause.minecraft("fell into the void", "with help from")),
    POISON(DamageCause.minecraft("poisoned to death", "by")),
    MAGIC(DamageCause.minecraft("magically died", "with help from")),
    WITHER(DamageCause.minecraft("withered to death", "by")),
    FALLING_BLOCK(DamageCause.minecraft("should've been wearing a helmet", "and {damager} knew that")),
    DRAGON_BREATH(DamageCause.minecraft("didn't like the smell of dragon", "wait... it's not a dragon, it's")),
    CRAMMING(DamageCause.minecraft("is too fat", "but {damager} isn't")),
    CONTACT(DamageCause.minecraft("likes to hug, but {damager} doesn't")),
    ENTITY_SWEEP_ATTACK(ENTITY_ATTACK.damageCause),
    SUFFOCATION(DamageCause.minecraft("couldn't hold their breath", "and {damager} was watching, menacingly")),
    MELTING(DamageCause.minecraft("is now a puddle of water", "isn't that fun, {damager}?")),
    LIGHTNING(DamageCause.minecraft("was struck by lightning", "by")),
    SUICIDE(DamageCause.minecraft("died", "how did you do that, {damager}?")),
    STARVATION(DamageCause.minecraft("starved to death", "and {damager} didn't share their food")),
    THORNS(DamageCause.minecraft("was pricked", "by")),
    FLY_INTO_WALL(DamageCause.minecraft("hit the wall at 69,420 mph", "while running from")),
    HOT_FLOOR(DamageCause.minecraft("didn't know that floor was lava", "and {damager} didn't tell them")),
    DRYOUT(DamageCause.minecraft("though it was water, it wasn't")),
    FREEZE(DamageCause.minecraft("frooze to death")),
    SONIC_BOOM(DamageCause.minecraft("BOOM BOOM BAKUDAN'ed", "and {damager} is the one to blame")),

    FEROCIY(DamageCause.nonCrit("was killed", "by")),                   // this is used to indicate ferocity hits
    NONE(DamageCause.minecraft("mysteriously died", "with help from")), // this used as default return,
    CUSTOM(DamageCause.EMPTY), // should not be used
    OTHER(DamageCause.EMPTY),  // this used if there is no other damage

    /**
     * End of system damage causes, add custom damage causes below.
     */

    CREEPER_EXPLOSION(DamageCause.of("'sploded by lovely friend", "of")),
    NOVA_EXPLOSION(DamageCause.of("has been split into atoms", "by")),
    SHOCK_DART(DamageCause.of("was shocked", "by")),
    BOOM_BOW(DamageCause.nonCrit("went out with a BIG BANG", "of").setTrueDamage()),
    FIRE_MOLOTOV(DamageCause.of("couldn't find a way out of {damager}'s fire")),
    FIRE_SPRAY(DamageCause.of("got sprayed to death", "by")),
    FROZEN_WEAPON(DamageCause.of("has been frozen to death", "by")),
    LEASHED(DamageCause.of("leashed to death", "by")),
    SOUL_WHISPER(DamageCause.of("has entered {damager}'s souls collection")),
    TOXIN(DamageCause.nonCrit("drunk too many potions", "while trying to fight")),
    METEORITE(DamageCause.nonCrit("felt the wrath of the rock", "of")),
    MOON_PILLAR(DamageCause.of("couldn't handle the beat", "of")),
    WITHER_SKULLED(DamageCause.of("was scared to death", "by")),
    GRAVITY_GUN(DamageCause.of("clearly couldn't see {damager}'s block of size of their head flying in their direction...")),
    PLUNGE(DamageCause.of("was stepped on", "by")),
    BLACK_HOLE(DamageCause.of("was sucked into the black hole", "created by")),
    DARKNESS(DamageCause.of("was blinded to death", "by")),
    THROWING_STARS(DamageCause.of("felt the absolute pain of {damager}'s dagger")),
    STARFALL(DamageCause.of("doesn't know how danger looks like, yes {damager}?")),
    GOLDEN_PATH(DamageCause.of("couldn't fight against their willpower", "created by shine of")),
    FLOWER(DamageCause.nonCrit("was pruned to death", "by")),
    FEEL_THE_BREEZE(DamageCause.nonCrit("felt {damager}'s breeze...")),
    NEVERMISS(DamageCause.of("couldn't dodge {damager}'s attack, what a noob..")),
    FEET_ATTACK(DamageCause.of("probably lost their toe")),
    SUBMERGE(DamageCause.of("didn't know that Sharks bite")),
    SOTS(DamageCause.nonCrit("couldn't hide from the stars", "of")),
    STAR_SLASH(DamageCause.of("was slashed in half", "by")),
    RAINFIRE(DamageCause.of("thought it's raining, but in reality it was {damager}'s arrows..")),
    SWEEP(DamageCause.of("was swept to death", "by")),
    RIFLE(DamageCause.of("had their brain exploded in cool slow-mo", "by")),
    SATCHEL(DamageCause.of("had their last flights", "with")),
    TORNADO(DamageCause.of("couldn't find the wind", "of")),
    LIBRARY_VOID(DamageCause.of("was consumed by §0the void")), // fixme -> colors don't work
    RIPTIDE(DamageCause.of("was splashed", "by")),
    COLD(DamageCause.of("froze to death", "with the help of")),
    LASER(DamageCause.of("was lasered to death", "by")),
    WATER(DamageCause.of("really liked the water")),
    SWARM(DamageCause.of("was swarmed to death by {damager}'s bats")),
    TROLL_LAUGH(DamageCause.nonCrit("was trolled to death", "by")),
    BLOCK_SHIELD(DamageCause.of("was hit by {damager}'s circling block")),
    DECOY(DamageCause.of("was bamboozled", "by")),
    MINION(DamageCause.of("was killed by {damager}'s minion")),
    RIP_BONES(DamageCause.of("was ripped to shreds", "by")),
    AURA_OF_CIRCUS(DamageCause.of("was furiously tamed", "by")),
    BLEED(DamageCause.nonCrit("bled to death from {damager}'s touch")),
    SHOTGUN(DamageCause.of("was shot to death", "by")),
    DEATH_RAY(DamageCause.of("was swallowed by the darkness", "of")),
    BACKSTAB(DamageCause.nonCrit("was stabbed in the back", "by")),
    WITHERBORN(DamageCause.nonCrit("was withered to death by {damager}'s Witherborn")),
    EMBODIMENT_OF_DEATH(DamageCause.nonCrit("was bodied to death", "by")),
    SHREDS_AND_PIECES(DamageCause.nonCrit("was tear to shreds and pieces :o", "with help from")),
    DARKNESS_CURSE(DamageCause.of("was swallowed by {damager}'s darkness")),
    CORROSION(DamageCause.nonCrit("corroded to death", "with help from")),
    ORC_DASH(DamageCause.of("was hit too hard", "by")),
    ORC_WEAPON(DamageCause.of("was {damager}'s bullseye")),
    CYCLING_AXE(DamageCause.of("couldn't see that {damager}'s axe is flying there")),
    FROSTBITE(DamageCause.of("froze to death, and {damager} is the one to blame")),
    POISON_IVY(DamageCause.nonCrit("was poised to death by {damager}'s poison ivy")),
    DWARF_LAVA(DamageCause.nonCrit("didn't bounce high enough")),
    IMPEL(DamageCause.nonCrit("failed to obey {damager}'s command")),
    CHALICE(DamageCause.nonCrit("had their soul sucked away", "by")),
    TWINCLAW(DamageCause.nonCrit("was pierced to death by {damager}'s claw")),
    CANDLEBANE(DamageCause.nonCrit("was crushed by {damager}'s pillar")),
    RADIATION(DamageCause.nonCrit("was lasered to death", "by").setTrueDamage()),
    SOULS_REBOUND(DamageCause.nonCrit("had their soul rebounded", "by").setTrueDamage()),
    GRAVITY(DamageCause.nonCrit("felt the gravity of {damager}'s planet")),
    ENDER_TELEPORT(DamageCause.nonCrit("was too scared of {damager}'s threatening aura")),
    STEAM(DamageCause.nonCrit("was steamed to death", "with help from")),
    DARK_ENERGY(DamageCause.nonCrit("was annihilated to death", "by")),
    SHADOW_CLONE(DamageCause.nonCrit("was killed by {damager}'s shadow")),
    STONE_CASTLE(DamageCause.nonCrit("died while protecting their teammates")),
    SENTRY_SHOT(DamageCause.nonCrit("was shot to death", "by {damager}'s sentry")),
    ;

    private final DamageCause damageCause;

    @Super
    EnumDamageCause(DamageCause damageCause) {
        this.damageCause = damageCause;
    }

    public DamageCause getDamageCause() {
        return damageCause;
    }

    public boolean isCanCrit() {
        return damageCause.isCanCrit();
    }

    public DeathMessage getRandomIfMultiple() {
        final List<DeathMessage> messages = damageCause.getDeathMessages();

        if (messages.size() == 1) {
            return messages.get(0);
        }

        return CollectionUtils.randomElement(messages);
    }

    public boolean isCustomDamage() {
        return damageCause.custom;
    }

    public boolean isProjectile() {
        return damageCause.isProjectile;
    }

    public boolean isTrueDamage() {
        return damageCause.isTrueDamage;
    }

    public boolean isMelee() {
        return this == ENTITY_ATTACK || this == ENTITY_ATTACK_NON_CRIT;
    }

    public boolean isAllowedForFerocity() {
        return isMelee() || this == PROJECTILE;
    }

    public static EnumDamageCause getFromCause(EntityDamageEvent.DamageCause cause) {
        final EnumDamageCause enumValue = Validate.getEnumValue(EnumDamageCause.class, cause.name());
        return enumValue == null ? EnumDamageCause.OTHER : enumValue;
    }

}
