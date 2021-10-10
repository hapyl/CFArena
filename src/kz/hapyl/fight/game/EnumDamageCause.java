package kz.hapyl.fight.game;

import kz.hapyl.spigotutils.module.util.CollectionUtils;
import kz.hapyl.spigotutils.module.util.Validate;
import org.bukkit.event.entity.EntityDamageEvent;

public enum EnumDamageCause {

	// *==* those are custom damages *==*
	CREEPER_EXPLOSION(true, "'sploded by lovely friend", "of"),
	NOVA_EXPLOSION(true, "has been split into atoms", "by"),
	SHOCK_DART(true, "got shocked", "by"),
	BOOM_BOW_ULTIMATE(true, "went out with a BIG BANG", "of"),
	FIRE_MOLOTOV(true, "couldn't find a way out of {player}'s fire"),
	FIRE_SPRAY(true, "got sprayed to death", "by"),
	FROZEN_WEAPON(true, "has been frozen to death", "by"),
	LEASHED(true, "leashed to death", "by"),
	SOUL_WHISPER(true, "has entered {player}'s souls collection"),
	TOXIN(true, "drunk too many potions"),

	// NEW ONES POGGGG
	METEORITE(true, "felt the wrath of the rock", "of"),
	MOON_PILLAR(true, "couldn't handle the beat", "of"),
	WITHER_SKULLED(true, "was scared to death", "by"),
	GRAVITY_GUN(true, "clearly couldn't see {player}'s block of size of their head flying in their direction.."),
	PLUNGE(true, "was stepped on", "by"),
	BLACK_HOLE(true, "was sucked into the black hole created", "by"),
	DARKNESS(true, "was blinded to death", "by"),
	THROWING_STARS(true, "felt the absolute pain {player}'s dagger"),
	STARFALL(true, "doesn't know how danger looks like, yes {player}?"),
	GOLDEN_PATH(true, "couldn't fight against their willpower", "created by shine of"),
	FLOWER(true, "was pruned to death", "by"),
	FELL_THE_BREEZE(true, "felt {player}'s breeze..."),
	NEVERMISS(true, "couldn't dodge {player}'s attack, what a noob.."),
	FEET_ATTACK(true, "probably lost their pinky"),
	SUBMERGE(true, "didn't know that Sharks bite"),
	SOTS(true, "couldn't hide from the stars", "of"),
	STAR_SLASH(true, "was slashed in half", "by"),
	RAINFIRE(true, "though it's raining, but in reality it were {player}'s arrows.."),
	SWEEP(true, "was swept to death", "by"),
	RIFLE(true, "had their brain exploded in cool slow-mo", "by"),
	SATCHEL(true, "had their last flights", "with"),
	TORNADO(true, "couldn't find the wind", "of"),
	LIBRARY_VOID(true, "was consumed by &0the void"),

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
