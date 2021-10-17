package kz.hapyl.fight.game;

import kz.hapyl.spigotutils.module.util.CollectionUtils;
import kz.hapyl.spigotutils.module.util.Validate;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * This allows to display custom damage cause messages.
 *
 * ToDo -> add many more, for each ability at least
 * ToDo -> add custom gradient colors (:
 */
public enum EnumDamageCause {

	CREEPER_EXPLOSION("'sploded by lovely friend", "of"),
	NOVA_EXPLOSION("has been split into atoms", "by"),
	SHOCK_DART("got shocked", "by"),
	BOOM_BOW_ULTIMATE("went out with a BIG BANG", "of"),
	FIRE_MOLOTOV("couldn't find a way out of {player}'s fire"),
	FIRE_SPRAY("got sprayed to death", "by"),
	FROZEN_WEAPON("has been frozen to death", "by"),
	LEASHED("leashed to death", "by"),
	SOUL_WHISPER("has entered {player}'s souls collection"),
	TOXIN("drunk too many potions"),
	METEORITE("felt the wrath of the rock", "of"),
	MOON_PILLAR("couldn't handle the beat", "of"),
	WITHER_SKULLED("was scared to death", "by"),
	GRAVITY_GUN("clearly couldn't see {player}'s block of size of their head flying in their direction.."),
	PLUNGE("was stepped on", "by"),
	BLACK_HOLE("was sucked into the black hole created", "by"),
	DARKNESS("was blinded to death", "by"),
	THROWING_STARS("felt the absolute pain {player}'s dagger"),
	STARFALL("doesn't know how danger looks like, yes {player}?"),
	GOLDEN_PATH("couldn't fight against their willpower", "created by shine of"),
	FLOWER("was pruned to death", "by"),
	FELL_THE_BREEZE("felt {player}'s breeze..."),
	NEVERMISS("couldn't dodge {player}'s attack, what a noob.."),
	FEET_ATTACK("probably lost their pinky"),
	SUBMERGE("didn't know that Sharks bite"),
	SOTS("couldn't hide from the stars", "of"),
	STAR_SLASH("was slashed in half", "by"),
	RAINFIRE("though it's raining, but in reality it were {player}'s arrows.."),
	SWEEP("was swept to death", "by"),
	RIFLE("had their brain exploded in cool slow-mo", "by"),
	SATCHEL("had their last flights", "with"),
	TORNADO("couldn't find the wind", "of"),
	LIBRARY_VOID("was consumed by &0the void"),
	RIPTIDE("was splashed", "by"),
	FREEZE("froze to death", "with help of"),

	// *==* there are vanilla ones, have to use them *==*

	// Have to consider entity_attack as custom damage for display porpoises
	ENTITY_ATTACK(true, "was killed", "by"),
	PROJECTILE(false, "was shot", "by"),
	FALL(false, "fell to their death", "while escaping from"),
	FIRE(false, "was toasted", "with help from"),
	FIRE_TICK(false, FIRE.deathMessage),
	LAVA(false, FIRE.deathMessage),
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

	NONE, // this used as default return
	OTHER; // this used if there no other damage

	private final boolean customDamage;
	private final DeathMessage[] deathMessage;

	EnumDamageCause(String message, String suffix) {
		this(true, message, suffix);
	}

	EnumDamageCause(String message) {
		this(message, "");
	}

	EnumDamageCause(boolean custom, String message, String suffix) {
		this.deathMessage = new DeathMessage[]{new DeathMessage(message, suffix)};
		this.customDamage = custom;
	}

	EnumDamageCause(boolean custom, DeathMessage... messages) {
		this.deathMessage = messages;
		this.customDamage = custom;
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

		private static final String PLAYER_PLACEHOLDER = "{player}";

		public DeathMessage(String message, String damagerSuffix) {
			this.message = message;
			this.damagerSuffix = message.contains(PLAYER_PLACEHOLDER) ? "" : damagerSuffix;
		}

		public String getMessage() {
			return message;
		}

		public String formatMessage(String player) {
			return getMessage().replace(PLAYER_PLACEHOLDER, player);
		}

		public boolean hasSuffix() {
			return !this.damagerSuffix.isBlank();
		}

		public String getDamagerSuffix() {
			return damagerSuffix;
		}

		public static DeathMessage of(String message, String suffix) {
			return new DeathMessage(message, suffix);
		}

	}

}
