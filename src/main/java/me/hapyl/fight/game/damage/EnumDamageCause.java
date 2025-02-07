package me.hapyl.fight.game.damage;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.util.Enums;
import me.hapyl.fight.game.DeathMessage;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nonnull;
import java.util.Set;

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
    ENTITY_ATTACK_NON_CRIT(ENTITY_ATTACK.damageCause.createCopy().setCanCrit(false)),
    PROJECTILE(DamageCause.minecraft("was shot", "by").setProjectile(true)),
    FALL(DamageCause.minecraft("fell to their death", "while escaping from").addFlags(DamageFlag.PIERCING_DAMAGE)),
    FIRE(DamageCause.minecraft("was toasted", "with help from").setDamageFormat(instance -> "&6%.0f 🔥".formatted(instance.getDamage()))),
    FIRE_TICK(FIRE.damageCause),
    LAVA(FIRE.damageCause),
    DROWNING(DamageCause.minecraft("drowned", "with help from")),
    BLOCK_EXPLOSION(DamageCause.minecraft("exploded", "by")),
    ENTITY_EXPLOSION(DamageCause.minecraft("exploded", "by")),
    VOID(DamageCause.minecraft("fell into the void", "with help from")),
    POISON(DamageCause.minecraft("poisoned to death", "by")
            .setDamageFormat(instance -> {
                return "&a%.0f ☣".formatted(instance.getDamage());
            })),
    MAGIC(DamageCause.minecraft("magically died", "with help from")),
    WITHER(DamageCause.minecraft("withered to death", "by")),
    FALLING_BLOCK(DamageCause.minecraft("should've been wearing a helmet", "and {damager} knew that")),
    DRAGON_BREATH(DamageCause.minecraft("didn't like the smell of dragon", "wait... it's not a dragon, it's")),
    CRAMMING(DamageCause.minecraft("is too fat", "but {damager} isn't")),
    CONTACT(DamageCause.minecraft("likes to hug, but {damager} doesn't")),
    ENTITY_SWEEP_ATTACK(ENTITY_ATTACK.damageCause),
    SUFFOCATION(DamageCause.minecraft("couldn't hold their breath", "and {damager} was watching, menacingly")),
    MELTING(DamageCause.minecraft("is now a puddle of water", "isn't that fun, {damager}?")),
    LIGHTNING(DamageCause.minecraft("was struck by lightning", "by").addFlags(DamageFlag.IGNORES_DAMAGE_TICKS)),
    SUICIDE(DamageCause.minecraft("died", "how did you do that, {damager}?")),
    STARVATION(DamageCause.minecraft("starved to death", "and {damager} didn't share their food")),
    THORNS(DamageCause.minecraft("was pricked", "by")),
    FLY_INTO_WALL(DamageCause.minecraft("hit the wall at 69,420 mph", "while running from")),
    HOT_FLOOR(DamageCause.minecraft("didn't know that floor was lava", "and {damager} didn't tell them").setDamageTicks(5)),
    DRYOUT(DamageCause.minecraft("thought it was water, it wasn't", "and {damager} was there to watch")),
    FREEZE(DamageCause.minecraft("frooze to death", "while running from")),
    SONIC_BOOM(DamageCause.minecraft("BOOM BOOM BAKUDAN'ed", "and {damager} is the one to blame")),

    FEROCITY(DamageCause.nonCrit("was ferociously killed", "by")
            .addFlags(DamageFlag.IGNORES_DAMAGE_TICKS)),      // this is used to indicate ferocity hits
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
    SOUL_WHISPER(DamageCause.nonCrit("has entered {damager}'s souls collection")),
    TOXIN(DamageCause.nonCrit("drunk too many potions", "while trying to fight")),
    METEORITE(DamageCause.nonCrit("felt the wrath of the rock", "of")),
    MOON_PILLAR(DamageCause.of("couldn't handle the beat", "of")),
    WITHER_SKULLED(DamageCause.of("was scared to death", "by")),
    GRAVITY_GUN(DamageCause.of("clearly couldn't see {damager}'s block of size of their head flying in their direction...")),
    PLUNGE(DamageCause.of("was stepped on", "by")),
    BLACK_HOLE(DamageCause.of("was sucked into the black hole", "created by")),
    DARKNESS(DamageCause.of("was blinded to death", "by")),
    THROWING_STARS(DamageCause.nonCrit("felt the absolute pain of {damager}'s dagger")),
    STARFALL(DamageCause.of("doesn't know how danger looks like, yes {damager}?")),
    GOLDEN_PATH(DamageCause.of("couldn't fight against their willpower", "created by shine of")),
    FLOWER(DamageCause.nonCrit("was pruned to death", "by")),
    FEEL_THE_BREEZE(DamageCause.nonCrit("felt {damager}'s breeze...")),
    NEVERMISS(DamageCause.of("couldn't dodge {damager}'s attack, what a noob...")),
    FEET_ATTACK(DamageCause.of("probably lost their toe", "isn't that right, {damager}?")),
    SUBMERGE(DamageCause.of("didn't know that Sharks bite", "but thanks to the {damager}, not they do")),
    SOTS(DamageCause.nonCrit("couldn't hide from the stars", "of").setDamageTicks(2)),
    STAR_SLASH(DamageCause.nonCrit("was slashed in half", "by")),
    RAINFIRE(DamageCause.of("thought it's raining, but in reality it was {damager}'s arrows...")),
    SWEEP(DamageCause.of("was swept to death", "by")),
    RIFLE(DamageCause.of("had their brain exploded in cool slow-mo", "by")),
    SATCHEL(DamageCause.of("had their last flights", "with")),
    TORNADO(DamageCause.of("couldn't find the wind", "of")),
    LIBRARY_VOID(DamageCause.of("was consumed by §kthe void")),
    RIPTIDE(DamageCause.nonCrit("was splashed", "by").addFlags(DamageFlag.IGNORES_DAMAGE_TICKS)),
    COLD(DamageCause.of("froze to death", "with the help of")),
    LASER(DamageCause.of("was lasered to death", "by").setDamageTicks(5)),
    WATER(DamageCause.of("really liked the water").setDamageTicks(5)),
    SWARM(DamageCause.of("was swarmed to death by {damager}'s bats").setDamageTicks(1).addFlags(DamageFlag.IGNORES_DAMAGE_TICKS)),
    TROLL_LAUGH(DamageCause.nonCrit("was trolled to death", "by")),
    BLOCK_SHIELD(DamageCause.of("was hit by {damager}'s circling block")),
    DECOY(DamageCause.of("was bamboozled", "by")),
    MINION(DamageCause.of("was killed by {damager}'s minion")),
    RIP_BONES(DamageCause.nonCrit("was ripped to shreds", "by")),
    AURA_OF_CIRCUS(DamageCause.of("was furiously tamed", "by")),
    BLEED(DamageCause.nonCrit("bled to death from {damager}'s touch")),
    SHOTGUN(DamageCause.of("was shot to death", "by")),
    DEATH_RAY(DamageCause.of("was swallowed by the darkness", "of")),
    BACKSTAB(DamageCause.nonCrit("was stabbed in the back", "by")),
    WITHERBORN(DamageCause.nonCrit("was withered to death by {damager}'s Witherborn")),
    EMBODIMENT_OF_DEATH(DamageCause.nonCrit("was bodied to death", "by").setDamageTicks(5)),
    SHREDS_AND_PIECES(DamageCause.nonCrit("was tear to shreds and pieces :o", "with help from")),
    DARKNESS_CURSE(DamageCause.of("was swallowed by {damager}'s darkness")),
    CORROSION(DamageCause.nonCrit("corroded to death", "with help from").setDamageTicks(5)),
    ORC_DASH(DamageCause.of("was hit too hard", "by")),
    ORC_WEAPON(DamageCause.nonCrit("was {damager}'s bullseye")),
    CYCLING_AXE(DamageCause.of("couldn't see that {damager}'s axe is flying there")),
    FROSTBITE(DamageCause.of("froze to death, and {damager} is the one to blame")),
    POISON_IVY(DamageCause.nonCrit("was poised to death by {damager}'s poison ivy").setDamageTicks(5)),
    DWARF_LAVA(DamageCause.nonCrit("didn't bounce high enough", "and {damager} was just stood there, menacingly")),
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
    SHADOW_CLONE(DamageCause.nonCrit("was killed by {damager}'s shadow").setDamageTicks(1)),
    STONE_CASTLE(DamageCause.nonCrit("died because of {damager} while protecting their teammates").setDamageTicks(1)),
    SENTRY_SHOT(DamageCause.nonCrit("was shot to death", "by {damager}'s sentry")),
    HACK(DamageCause.nonCrit("was hacked", "by")),
    BLADE_BARRAGE(DamageCause.nonCrit("fell before {damager}'s swords")),
    PIERCING(DamageCause.of("was pierced to death", "by")
            .addFlags(DamageFlag.PIERCING_DAMAGE)
            .setDamageFormat(instance -> "&b%.0f ⚡".formatted(instance.getDamage()))),
    TOTEM(DamageCause.of("was stomped on", "by").setDamageTicks(1)),
    RAY_OF_DEATH(DamageCause.of("was doomed to fail", "and {damager} knew that").setDamageTicks(2)),
    ROGUE_ATTACK(ENTITY_ATTACK.damageCause.createCopy().setDamageTicks(7)),
    THROWING_KNIFE(DamageCause.nonCrit("was hit by a throwing knife", "of")),
    PIPE_BOMB(DamageCause.nonCrit("was blown away by {damager}'s Pipe Bomb").setTrueDamage()),
    UPPERCUT(DamageCause.nonCrit("was upperCUT", "by")),
    RANGE_ATTACK(DamageCause.of("was shot", "by")),
    ICICLE(DamageCause.nonCrit("was pierced by an icicle", "by")),
    CELESTE_ARROW(DamageCause.nonCrit("was shot", "by")),
    CHAOS(DamageCause.nonCrit("was chaotically killed", "by").setDamageTicks(1)),
    SHARK_BITE(DamageCause.nonCrit("was bitten", "by").addFlags(DamageFlag.PIERCING_DAMAGE)),
    NYX_SPIKE(DamageCause.nonCrit("was pierced to death", "by").addFlags(DamageFlag.PIERCING_DAMAGE)),
    SPIKE_SHIELD(DamageCause.nonCrit("was hit by spikes", "by").addFlags(DamageFlag.TRUE_DAMAGE)),
    THE_JOKER(DamageCause.nonCrit("'s death was yoinked", "by")),
    ECHO(DamageCause.nonCrit("lost their body in the monochrome world", "of")),
    RONIN_HIT(DamageCause.nonCrit("lost in the duel", "to")),
    DEFLECT(DamageCause.nonCrit("was killed by {damager}'s deflected attack")),
    BAT_BITE(DamageCause.nonCrit("was bitten", "by")),
    BAT_BITE_NO_TICK(BAT_BITE.damageCause.createCopy().setDamageTicks(1)),
    DEAD_EYE(DamageCause.of("was dead eyed", "by")),

    ;

    private final DamageCause damageCause;
    private final String name;

    EnumDamageCause(@Nonnull DamageCause damageCause) {
        this.damageCause = damageCause;
        this.name = Chat.capitalize(this);
    }

    public DamageCause getDamageCause() {
        return damageCause;
    }

    public boolean isCanCrit() {
        return damageCause.isCanCrit();
    }

    @Nonnull
    public DeathMessage getDeathMessage() {
        return damageCause.getDeathMessage();
    }

    public boolean isCustomDamage() {
        return damageCause.isCustom();
    }

    public boolean isProjectile() {
        return damageCause.hasFlag(DamageFlag.PROJECTILE);
    }

    public boolean isTrueDamage() {
        return damageCause.isTrueDamage();
    }

    public boolean isMelee() {
        return this == ENTITY_ATTACK || this == ENTITY_ATTACK_NON_CRIT;
    }

    public boolean isAllowedForFerocity() {
        return isMelee() || this == PROJECTILE;
    }

    @Nonnull
    public Set<DamageFlag> getFlags() {
        return damageCause.getFlags();
    }

    public boolean hasFlag(@Nonnull DamageFlag flag) {
        return damageCause.hasFlag(flag);
    }

    public int getDamageTicks() {
        return damageCause.getDamageTicks();
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    public static EnumDamageCause getFromCause(EntityDamageEvent.DamageCause cause) {
        final EnumDamageCause enumValue = Enums.byName(EnumDamageCause.class, cause.name());
        return enumValue == null ? EnumDamageCause.OTHER : enumValue;
    }

}
