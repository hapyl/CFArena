package kz.hapyl.fight.game.talents;

import kz.hapyl.fight.Main;
import kz.hapyl.fight.game.talents.storage.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public enum Talents {

	// Archer
	TRIPLE_SHOT(new TripleShot()),
	SHOCK_DARK(new ShockDark()),
	HAWKEYE_ARROW(new PassiveTalent(
			"Hawkeye Arrow",
			"Fully charged shots while sneaking have 25% chance to fire hawkeye arrow which homes to nearby enemies.",
			Material.ENDER_EYE
	)),

	// Alchemist
	POTION(new RandomPotion()),
	CAULDRON(new CauldronAbility()),
	INTOXICATION(new PassiveTalent(
			"Intoxication",
			"Drinking potions will increase &eIntoxication &7level that will decrease constantly. Keeping an eye on &eIntoxication &7level is a good idea, who knows what can happen...",
			Material.DRAGON_BREATH
	)),

	// Moonwalker
	MOONSLITE_PILLAR(new MoonslitePillar()),
	MOONSLITE_BOMB(new MoonSliteBomb()),
	TARGET(new PassiveTalent(
			"Target",
			"Hold &e&lSNEAK &7to show your target block. Most of your abilities will spawn at the target block.",
			Material.SPECTRAL_ARROW
	)),

	// Hercules
	HERCULES_DASH(new HerculesShift()),
	HERCULES_UPDRAFT(new HerculesJump()),
	PLUNGE(new PassiveTalent(
			"Plunge",
			"While airborne, &e&lSNEAK &7to perform plunging attack, dealing damage to nearby enemies.",
			Material.COARSE_DIRT
	)),

	// Mage
	MAGE_TRANSMISSION(new MageTransmission()),
	MAGE_TODO(null),
	SOUL_HARVEST(new PassiveTalent(
			"Soul Harvest",
			"Deal &bmelee &7damage to gain soul fragment as fuel for your &e&lSoul &e&lEater&7's range attacks.",
			Material.SKELETON_SPAWN_EGG
	)),

	// Pytaria
	FLOWER_ESCAPE(new FlowerEscape()),
	FLOWER_BREEZE(new FlowerBreeze()),
	EXCELLENCY(new PassiveTalent(
			"Excellency",
			// When Pytaria's health is lower or equal to &c50%&7, her damage is increased by &b50%&7.
			"When Pytaria's &chealth &7is lower or equal to &c50%&7, her damage is increased by &b50%&7.",
			Material.ROSE_BUSH
	)),

	// Troll
	TROLL_SPIN(new TrollSpin()),
	REPULSOR(new Repulsor()),
	TROLL_PASSIVE(new PassiveTalent("idk yet", "idk yet", Material.STONE)),

	// Nightmare
	PARANOIA(new Paranoia()),
	SHADOW_SHIFT(new ShadowShift()),
	IN_THE_SHADOWS(new PassiveTalent("In the Shadows", "While in moody light, your &b&lSpeed &7and &c&lDamage &7is increased.", Material.DRIED_KELP)),

	// Dr. Ed
	CONFUSION_POTION(new ConfusionPotion()),
	MISSING_TALENT_0(null),
	MISSING_TALENT_1(new PassiveTalent("missing", "missing", Material.BEDROCK)),

	// Ender
	TELEPORT_PEARL(new TeleportPearl()),
	TRANSMISSION_BEACON(new TransmissionBeacon()),
	ENDERMAN_FLESH(new PassiveTalent("Fears of Enderman", "While in water, you will constantly take damage.", Material.WATER_BUCKET)),

	// Spark
	SPARK_MOLOTOV(new Molotov()),
	SPARK_FLASH(new SparkFlash()),
	FIRE_GUY(new PassiveTalent("Fire Guy", "You're completely immune to &clava &7and &cfire &7damage.", Material.LAVA_BUCKET)),

	// SA
	SHADOW_PRISM(new ShadowPrism()),
	SHROUDED_STEP(new ShroudedStep()),
	SECRET_SHADOW_WARRIOR_TECHNIQUE(new PassiveTalent(
			"Dark Cover",
			"As a assassin, you have mastered ability to stay in shadows. While &e&lSNEAKING&7, you become completely invisible, but cannot deal damage and your footsteps are visible.",
			Material.NETHERITE_CHESTPLATE
	)),

	// Witcher
	AARD(new Aard()),
	IGNY(new Igny()),
	KVEN(new Kven()),
	AKCIY(new Akciy()),
	IRDEN(new Irden()),
	COMBO_SYSTEM(new PassiveTalent(
			"Combo",
			"Dealing continuous damage to the same target will increase your combo, greater combo hits deals increased damage.",
			Material.SKELETON_SKULL
	)),

	// Vortex
	VORTEX_STAR(new VortexStar()),
	STAR_ALIGNER(new StarAligner()),
	EYES_OF_THE_GALAXY(new PassiveTalent(
			"Eyes of the Galaxy",
			"Astral Stars you place will glow different colors:____&eYellow &7indicates a placed star.____&bAqua &7indicates closest star that will be consumed upon teleport.____&aGreen &7indicates star you will blink to upon teleport.",
			Material.ENDER_EYE
	)),

	// Freazly
	ICE_CONE(new IceCone()),

	// Dark Mage
	BLINDING_CURSE(new BlindingCurse()),
	SLOWING_AURA(new SlowingAura()),
	HEALING_AURA(new HealingAura()),
	SHADOW_CLONE(new ShadowClone()),

	// Blast Knight
	SPEAR(new Spear()),
	SLOWNESS_POTION(new SlownessPotion()),
	SHIELDED(new PassiveTalent(
			"Shielded",
			"Blocking damage using your shield will charge it. Once charged, shield will explode and create Nova Explosion, dealing damage and knocking back opponents.",
			Material.SHIELD
	)),

	// Ninja
	NINJA_DASH(new NinjaDash()),
	NINJA_SMOKE(new NinjaSmoke()),
	FLEET_FOOT(new PassiveTalent(
			"Fleet Foot",
			"Ninja's are fast and fragile. You gain &bSpeed &7boost and don't take fall damage.",
			Material.ELYTRA
	)),

	// Taker
	RESERVED_TAKER(null),
	RESERVED_TAKER0(null),
	RESERVED_TAKER1(null),

	//
	ARROW_SHIELD(new ArrowShield()),
	CLIMB(new Climb()),
	ELUSIVE_BURST(new PassiveTalent(
			"Elusive Burst",
			"Fully charged shots while sneaking will infuse your arrow. Infused arrows exploded into small clusters dealing big damage upon hit.",
			Material.PEONY
	)),

	// Swooper
	BLAST_PACK(new BlastPack()),
	SNIPER_SCOPE(new PassiveTalent(
			"Sniper Scope",
			"&e&lSNEAK &7to activate sniper scope and increase your rifle's damage and distance.",
			Material.SPYGLASS
	)),

	// Shark
	SUBMERGE(new Submerge()),
	CLAW_CRITICAL(new PassiveTalent(
			"Sturdy Claws",
			"Your hits have &b10% &7chance to &ccrit&7! Critical hits summons an ancient creature from beneath that deals extra damage in small AoE!",
			Material.MILK_BUCKET
	)),

	// Librarian
	BLACK_HOLE(new BlackHole()),
	ENTITY_DARKNESS(new EntityDarkness()),
	LIBRARIAN_SHIELD(new LibrarianShield()),
	WEAPON_DARKNESS(new WeaponDarkness()),

	// Harbinger
	STANCE(new MeleeStance()),
	RIPTIDE(new PassiveTalent(
			"Riptide",
			"Fully charged shot in &e&lRange Stance&7 applies &bRiptide &7effect to opponents.__Hitting opponents affected by &bRiptide&7 in &e&lRange Stance &7executes &bRiptide Slash&7, that clears &bRiptide&7 and rapidly deals damage.",
			Material.HEART_OF_THE_SEA
	)),

	// TECHIE
	TRAP_CAGE(new TrapCage()),
	TRAP_WIRE(new TrapWire()),
	NEURAL_THEFT(new PassiveTalent(
			"Neural Theft/CYber Hack",
			// that stuns, reveals location and applies &6&lVulnerability&7.
			"Every &b10s&7, hacks into opponents revealing their location and health.____&b&lCYber Hack:__&7A small virus that reveals opponent's location, slows them and applies &6&lVulnerability&7 for short duration.",
			Material.CHAINMAIL_HELMET
	)),

	// test
	TestChargeTalent(new TestChargeTalent()),

	;

	private final Talent talent;

	Talents(Talent talent) {
		if (talent instanceof UltimateTalent) {
			throw new IllegalArgumentException("ultimate talent enum initiation");
		}
		this.talent = talent;
		if (talent instanceof Listener listener) {
			Main.getPlugin().addEvent(listener);
		}
	}

	public void startCd(Player player) {
		getTalent().startCd(player);
	}

	public String getName() {
		return getTalent().getName();
	}

	public Talent getTalent() {
		return talent;
	}
}
