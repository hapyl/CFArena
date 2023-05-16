package me.hapyl.fight.game;

import me.hapyl.spigotutils.module.annotate.Super;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * This allows to display custom damage cause messages.
 *
 * ToDo -> add many more, for each ability at least
 * ToDo -> add custom gradient colors (:
 */
public enum EnumDamageCause {

    /**
     * System damage causes, <b>do not</b> modify the order.
     */
    ENTITY_ATTACK(true, "was killed", "by"), // Have to consider entity_attack as custom damage for display purpose
    PROJECTILE(false, "was shot", "by"),
    FALL(false, "fell to their death", "while escaping from"),
    FIRE(false, "was toasted", "with help from"),
    FIRE_TICK(false, null, FIRE.deathMessage),
    LAVA(false, null, FIRE.deathMessage),
    DROWNING(false, "drowned"),
    BLOCK_EXPLOSION(false, "exploded", "by"),
    ENTITY_EXPLOSION(false, "exploded", "by"),
    VOID(false, "fell into the void"),
    POISON(false, "poisoned to death", "by"),
    MAGIC(false, "magically died", "with help of"),
    WITHER(false, "withered to death", "by"),
    FALLING_BLOCK(false, "should've been wearing a helmet"),
    DRAGON_BREATH(false, "didn't like the smell of dragon"),
    CRAMMING(false, "is too fat"),
    CONTACT(false, "likes to hug, but {damager} doesn't"),
    ENTITY_SWEEP_ATTACK(false, null, ENTITY_ATTACK.deathMessage),
    SUFFOCATION(false, "couldn't hold their breath"),
    MELTING(false, "is now a puddle of water"),
    LIGHTNING(false, "was struck by lightning", "by"),
    SUICIDE(false, "died"),
    STARVATION(false, "starved to death"),
    THORNS(false, "was pricked", "by"),
    FLY_INTO_WALL(false, "hit the wall at 69,420 mph", "while running from"),
    HOT_FLOOR(false, "didn't know that floor was lava"),
    DRYOUT(false, "though it was water, it wasn't"),
    FREEZE(false, "frooze to death"),
    SONIC_BOOM(false, "BOOM BOOM BAKUDAN", "and {damager} is the one to blame"),

    NONE(false, "mysteriously died"),  // this used as default return,
    CUSTOM, // should not be used
    OTHER, // this used if there is no other damage
    /**
     * End of system damage causes, add custom damage causes below.
     */

    CREEPER_EXPLOSION("'sploded by lovely friend", "of"),
    NOVA_EXPLOSION("has been split into atoms", "by"),
    SHOCK_DART("was shocked", "by"),
    BOOM_BOW_ULTIMATE("went out with a BIG BANG", "of"),
    FIRE_MOLOTOV("couldn't find a way out of {damager}'s fire"),
    FIRE_SPRAY("got sprayed to death", "by"),
    FROZEN_WEAPON("has been frozen to death", "by"),
    LEASHED("leashed to death", "by"),
    SOUL_WHISPER("has entered {damager}'s souls collection"),
    TOXIN("drunk too many potions"),
    METEORITE("felt the wrath of the rock", "of"),
    MOON_PILLAR("couldn't handle the beat", "of"),
    WITHER_SKULLED("was scared to death", "by"),
    GRAVITY_GUN("clearly couldn't see {damager}'s block of size of their head flying in their direction.."),
    PLUNGE("was stepped on", "by"),
    BLACK_HOLE("was sucked into the black hole created", "by"),
    DARKNESS("was blinded to death", "by"),
    THROWING_STARS("felt the absolute pain {damager}'s dagger"),
    STARFALL("doesn't know how danger looks like, yes {damager}?"),
    GOLDEN_PATH("couldn't fight against their willpower", "created by shine of"),
    FLOWER("was pruned to death", "by"),
    FELL_THE_BREEZE("felt {damager}'s breeze..."),
    NEVERMISS("couldn't dodge {damager}'s attack, what a noob.."),
    FEET_ATTACK("probably lost their toe"),
    SUBMERGE("didn't know that Sharks bite"),
    SOTS("couldn't hide from the stars", "of"),
    STAR_SLASH("was slashed in half", "by"),
    RAINFIRE("though it's raining, but in reality it were {damager}'s arrows.."),
    SWEEP("was swept to death", "by"),
    RIFLE("had their brain exploded in cool slow-mo", "by"),
    SATCHEL("had their last flights", "with"),
    TORNADO("couldn't find the wind", "of"),
    LIBRARY_VOID("was consumed by &0the void"),
    RIPTIDE("was splashed", "by"),
    COLD("froze to death", "with help of"),
    LASER("was lasered to death", "by"),
    WATER("really liked the water"),
    SWARM("was swarmed to death by {damager}'s bats"),
    TROLL_LAUGH("was trolled to death", "by"),
    BLOCK_SHIELD("was hit by {damager}'s circling block"),
    DECOY("was bamboozled", "by"),
    MINION("was killed by {damager}'s minion"),
    RIP_BONES("was ripped to shreds", "by"),
    AURA_OF_CIRCUS("was furiously tamed", "by"),
    BLEED("bled to death from {damager}'s touch"),
    SHOTGUN("was shot to death", "by"),
    DEATH_RAY("was swallowed by the darkness", "of"),
    BACKSTAB("was stabbed in the back", "by"),
    WITHERBORN("was withered to death by {damager}'s Witherborn"),
    EMBODIMENT_OF_DEATH("was bodied to death", "by"),

    ;

    private final boolean customDamage;
    private final DeathMessage[] deathMessage;
    private final DamageFormat format;

    @Super
    EnumDamageCause(boolean custom, DamageFormat format, DeathMessage... messages) {
        this.deathMessage = messages;
        this.customDamage = custom;
        this.format = format;
    }

    EnumDamageCause(String message, String suffix) {
        this(true, message, suffix);
    }

    EnumDamageCause(String message) {
        this(message, "");
    }

    EnumDamageCause(boolean custom, String message, String suffix) {
        this(custom, null, new DeathMessage(message, suffix));
    }

    EnumDamageCause(boolean custom, String message) {
        this(custom, message, "");
    }

    EnumDamageCause(boolean customDamage) {
        this(customDamage, "", "");
    }

    EnumDamageCause() {
        this(false);
    }

    public DamageFormat getFormat() {
        return format;
    }

    public DeathMessage getRandomIfMultiple() {
        if (deathMessage.length == 1) {
            return deathMessage[0];
        }
        return CollectionUtils.randomElement(deathMessage);
    }

    public boolean isCustomDamage() {
        return customDamage;
    }

    public static EnumDamageCause getFromCause(EntityDamageEvent.DamageCause cause) {
        final EnumDamageCause enumValue = Validate.getEnumValue(EnumDamageCause.class, cause.name());
        return enumValue == null ? EnumDamageCause.OTHER : enumValue;
    }

    public record DeathMessage(String message, String damagerSuffix) {

        // Include this in either message or damagerSuffix, and it will be replaced with the damager name
        private static final String DAMAGER_PLACEHOLDER = "{damager}";

        public DeathMessage(String message, String damagerSuffix) {
            this.message = message;

            // If message has placeholder, then damagerSuffix is not needed
            if (message.contains(DAMAGER_PLACEHOLDER)) {
                this.damagerSuffix = "";
            }
            else {
                // If suffix has placeholder, then don't append it
                if (damagerSuffix.contains(DAMAGER_PLACEHOLDER)) {
                    this.damagerSuffix = damagerSuffix;
                }
                else {
                    this.damagerSuffix = damagerSuffix + " " + DAMAGER_PLACEHOLDER;
                }
            }
        }

        public String formatMessage(String damager) {
            return message.replace(DAMAGER_PLACEHOLDER, damager);
        }

        public String formatSuffix(String damager) {
            if (damagerSuffix.isBlank()) {
                return "";
            }

            return damagerSuffix.replace(DAMAGER_PLACEHOLDER, damager);
        }

        public static DeathMessage of(String message, String suffix) {
            return new DeathMessage(message, suffix);
        }

    }

}
