package me.hapyl.fight.game.talents;

import com.google.common.collect.Maps;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.talents.archive.TestChargeTalent;
import me.hapyl.fight.game.talents.archive.alchemist.CauldronAbility;
import me.hapyl.fight.game.talents.archive.alchemist.RandomPotion;
import me.hapyl.fight.game.talents.archive.archer.ShockDark;
import me.hapyl.fight.game.talents.archive.archer.TripleShot;
import me.hapyl.fight.game.talents.archive.bounty_hunter.GrappleHookTalent;
import me.hapyl.fight.game.talents.archive.bounty_hunter.ShortyShotgun;
import me.hapyl.fight.game.talents.archive.dark_mage.BlindingCurse;
import me.hapyl.fight.game.talents.archive.dark_mage.HealingAura;
import me.hapyl.fight.game.talents.archive.dark_mage.ShadowClone;
import me.hapyl.fight.game.talents.archive.dark_mage.SlowingAura;
import me.hapyl.fight.game.talents.archive.doctor.ConfusionPotion;
import me.hapyl.fight.game.talents.archive.doctor.HarvestBlocks;
import me.hapyl.fight.game.talents.archive.ender.TeleportPearl;
import me.hapyl.fight.game.talents.archive.ender.TransmissionBeacon;
import me.hapyl.fight.game.talents.archive.engineer.EngineerRecall;
import me.hapyl.fight.game.talents.archive.engineer.EngineerSentry;
import me.hapyl.fight.game.talents.archive.freazly.IceBarrier;
import me.hapyl.fight.game.talents.archive.freazly.IceCone;
import me.hapyl.fight.game.talents.archive.harbinger.MeleeStance;
import me.hapyl.fight.game.talents.archive.harbinger.TidalWaveTalent;
import me.hapyl.fight.game.talents.archive.healer.HealingOrb;
import me.hapyl.fight.game.talents.archive.healer.ReviveTotem;
import me.hapyl.fight.game.talents.archive.heavy_knight.Slash;
import me.hapyl.fight.game.talents.archive.heavy_knight.Updraft;
import me.hapyl.fight.game.talents.archive.heavy_knight.Uppercut;
import me.hapyl.fight.game.talents.archive.hercules.HerculesJump;
import me.hapyl.fight.game.talents.archive.hercules.HerculesShift;
import me.hapyl.fight.game.talents.archive.juju.ArrowShield;
import me.hapyl.fight.game.talents.archive.juju.Climb;
import me.hapyl.fight.game.talents.archive.km.LaserEye;
import me.hapyl.fight.game.talents.archive.km.ShellGrande;
import me.hapyl.fight.game.talents.archive.knight.SlownessPotion;
import me.hapyl.fight.game.talents.archive.knight.Spear;
import me.hapyl.fight.game.talents.archive.librarian.BlackHole;
import me.hapyl.fight.game.talents.archive.librarian.EntityDarkness;
import me.hapyl.fight.game.talents.archive.librarian.LibrarianShield;
import me.hapyl.fight.game.talents.archive.librarian.WeaponDarkness;
import me.hapyl.fight.game.talents.archive.mage.ArcaneMute;
import me.hapyl.fight.game.talents.archive.mage.MageTransmission;
import me.hapyl.fight.game.talents.archive.moonwalker.GravityZone;
import me.hapyl.fight.game.talents.archive.moonwalker.MoonPillarTalent;
import me.hapyl.fight.game.talents.archive.moonwalker.MoonSliteBomb;
import me.hapyl.fight.game.talents.archive.nightmare.Paranoia;
import me.hapyl.fight.game.talents.archive.nightmare.ShadowShift;
import me.hapyl.fight.game.talents.archive.ninja.NinjaDash;
import me.hapyl.fight.game.talents.archive.ninja.NinjaSmoke;
import me.hapyl.fight.game.talents.archive.orc.OrcAxe;
import me.hapyl.fight.game.talents.archive.orc.OrcGrowl;
import me.hapyl.fight.game.talents.archive.pytaria.FlowerBreeze;
import me.hapyl.fight.game.talents.archive.pytaria.FlowerEscape;
import me.hapyl.fight.game.talents.archive.shadow_assassin.ShadowPrism;
import me.hapyl.fight.game.talents.archive.shadow_assassin.ShroudedStep;
import me.hapyl.fight.game.talents.archive.shaman.ResonanceType;
import me.hapyl.fight.game.talents.archive.shaman.Totem;
import me.hapyl.fight.game.talents.archive.shaman.TotemTalent;
import me.hapyl.fight.game.talents.archive.shark.Submerge;
import me.hapyl.fight.game.talents.archive.shark.Whirlpool;
import me.hapyl.fight.game.talents.archive.spark.Molotov;
import me.hapyl.fight.game.talents.archive.spark.SparkFlash;
import me.hapyl.fight.game.talents.archive.sun.SyntheticSun;
import me.hapyl.fight.game.talents.archive.swooper.BlastPack;
import me.hapyl.fight.game.talents.archive.swooper.Blink;
import me.hapyl.fight.game.talents.archive.taker.DeathSwap;
import me.hapyl.fight.game.talents.archive.taker.FatalReap;
import me.hapyl.fight.game.talents.archive.taker.SpiritualBonesPassive;
import me.hapyl.fight.game.talents.archive.tamer.MineOBall;
import me.hapyl.fight.game.talents.archive.techie.TrapCage;
import me.hapyl.fight.game.talents.archive.techie.TrapWire;
import me.hapyl.fight.game.talents.archive.troll.Repulsor;
import me.hapyl.fight.game.talents.archive.troll.TrollSpin;
import me.hapyl.fight.game.talents.archive.vampire.BatSwarm;
import me.hapyl.fight.game.talents.archive.vampire.VampirePet;
import me.hapyl.fight.game.talents.archive.vortex.StarAligner;
import me.hapyl.fight.game.talents.archive.vortex.VortexStar;
import me.hapyl.fight.game.talents.archive.witcher.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * This is a registry for all talents that are
 * executable, no matter if is used or not.
 *
 * <p>
 * Stores ENUM(Talent) where Talent is a class instance of a talent.
 * </p>
 *
 * <p>
 * Talents <b>MUST</b> be stored in here, otherwise they will not be registered.
 * </p>
 * <p>
 * To get actual talent class, use {@link #getTalent(Class)}.
 *
 * @author hapyl
 */
public enum Talents {

    // Archer
    TRIPLE_SHOT(new TripleShot()),
    SHOCK_DARK(new ShockDark()),
    HAWKEYE_ARROW(new PassiveTalent(
            "Hawkeye Arrow",
            "Fully charged shots while sneaking have &b25%&7 chance to fire hawkeye arrow that homes to nearby enemies.",
            Material.ENDER_EYE
    )),

    // Alchemist
    POTION(new RandomPotion()),
    CAULDRON(new CauldronAbility()),
    INTOXICATION(new PassiveTalent(
            "Intoxication",
            "Drinking potions will increase &eIntoxication &7level that will decrease constantly.____Keeping an eye on &eIntoxication &7level is a good idea, who knows what might happen...",
            Material.DRAGON_BREATH
    )),

    // Moonwalker
    MOONSLITE_PILLAR(new MoonPillarTalent()),
    @Deprecated MOONSLITE_BOMB(new MoonSliteBomb()),
    MOON_GRAVITY(new GravityZone()),
    TARGET(new PassiveTalent("Space Suit", "Your suit grants you slow falling ability.", Material.FEATHER)),

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
    ARCANE_MUTE(new ArcaneMute()),
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
            "The less &chealth&7 Pytaria has, the more her %s and %s increases. But her %s significantly decreases.".formatted(
                    AttributeType.ATTACK,
                    AttributeType.CRIT_CHANCE,
                    AttributeType.DEFENSE
            ), Material.ROSE_BUSH
    )),

    // Troll
    TROLL_SPIN(new TrollSpin()),
    REPULSOR(new Repulsor()),
    TROLL_PASSIVE(new PassiveTalent("Last Laugh", "Your hits have &b0.1% &7chance to instantly kill enemy.", Material.BLAZE_POWDER)),

    // Tamer
    MINE_O_BALL(new MineOBall()),

    // Nightmare
    PARANOIA(new Paranoia()),
    SHADOW_SHIFT(new ShadowShift()),
    IN_THE_SHADOWS(new PassiveTalent(
            "In the Shadows",
            "While in moody light, your &b&lSpeed &7and &c&lDamage &7is increased.",
            Material.DRIED_KELP
    )),

    // Dr. Ed
    CONFUSION_POTION(new ConfusionPotion()),
    HARVEST(new HarvestBlocks()),
    BLOCK_SHIELD(new PassiveTalent(
            "Block Maelstrom",
            "Creates a block that orbits around you, dealing damage based on the element upon contact with opponents.____&7Refreshes every &b10s&7.",
            Material.BRICK
    )),

    // Ender
    TELEPORT_PEARL(new TeleportPearl()),
    TRANSMISSION_BEACON(new TransmissionBeacon()),
    ENDERMAN_FLESH(new PassiveTalent("Fears of Enderman", "While in water, you will constantly take damage.", Material.WATER_BUCKET)),

    // Spark
    SPARK_MOLOTOV(new Molotov()),
    SPARK_FLASH(new SparkFlash()),
    FIRE_GUY(new PassiveTalent("Fire Guy", "You're completely immune to &clava &7and &cfire &7damage.", Material.LAVA_BUCKET)),

    // Shadow Assassin
    SHADOW_PRISM(new ShadowPrism()),
    SHROUDED_STEP(new ShroudedStep()),
    SECRET_SHADOW_WARRIOR_TECHNIQUE(new PassiveTalent(
            "Dark Cover",
            "As an assassin, you have mastered the ability to stay in the shadows.____While &e&lSNEAKING&7, you become completely invisible, but cannot deal damage and your footsteps are visible.",
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
            "Dealing &bcontinuous damage&7 to the &bsame target&7 will increase your combo.____Greater combo hits deal &cincreased damage&7.",
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
    ICE_BARRIER(new IceBarrier()),

    // Dark Mage
    BLINDING_CURSE(new BlindingCurse()),
    SLOWING_AURA(new SlowingAura()),
    HEALING_AURA(new HealingAura()),
    SHADOW_CLONE(new ShadowClone()),
    DARK_MAGE_PASSIVE(new PassiveTalent("Wither Blood", """
            Whenever you take damage, there is small chance to poison attackers blood, blinding and withering them.
            """, Material.WITHER_ROSE)),

    // Blast Knight
    SPEAR(new Spear()),
    SLOWNESS_POTION(new SlownessPotion()),
    SHIELDED(new PassiveTalent(
            "Shielded",
            "Blocking damage using your shield will add a charge to it, up to &b10&7 charges.____Once charged, it will explode and create &bNova Explosion&7, dealing moderate damage and knocking back nearby opponents.",
            Material.SHIELD
    )),

    // Ninja
    NINJA_DASH(new NinjaDash()),
    NINJA_SMOKE(new NinjaSmoke()),
    FLEET_FOOT(new PassiveTalent(
            "Fleet Foot",
            "Ninjas are fast and fragile.____You gain &bSpeed &7boost and don't take fall damage.",
            Material.ELYTRA
    )),

    // Taker
    FATAL_REAP(new FatalReap()),
    DEATH_SWAP(new DeathSwap()),
    SPIRITUAL_BONES(new SpiritualBonesPassive()),

    // JuJu
    ARROW_SHIELD(new ArrowShield()),
    CLIMB(new Climb()),
    ELUSIVE_BURST(new PassiveTalent(
            "Elusive Burst", """
            Fully &ncharged&7 shots while &nsneaking&7 will infuse your arrow.

            Infused arrows exploded into small clusters dealing big damage upon hit.
            """, Material.PEONY
    )),

    // Swooper
    BLAST_PACK(new BlastPack()),
    BLINK(new Blink()),
    SNIPER_SCOPE(new PassiveTalent(
            "Path Writer",
            "Your last &b5&7 seconds of life are stored in the path writer.",
            Material.STRING
    )),

    // Shark
    SUBMERGE(new Submerge()),
    WHIRLPOOL(new Whirlpool()),
    CLAW_CRITICAL(new PassiveTalent(
            "Oceanborn/Sturdy Claws",
            "&b&lOceanborn:__While in water, your speed and damage is drastically increased.____&b&lSturdy Claws:__&7Your hits have &b10% &7chance to &ccrit&7!__Critical hits summons an ancient creature from beneath that deals extra damage and heals you!",
            Material.MILK_BUCKET
    )),

    // Librarian
    BLACK_HOLE(new BlackHole()),
    ENTITY_DARKNESS(new EntityDarkness()),
    LIBRARIAN_SHIELD(new LibrarianShield()),
    WEAPON_DARKNESS(new WeaponDarkness()),

    // Harbinger
    STANCE(new MeleeStance()),
    TIDAL_WAVE(new TidalWaveTalent()),
    RIPTIDE(new PassiveTalent(
            "Riptide",
            "Fully charged shots in &e&lRange Stance&7 applies &bRiptide &7effect to opponents.____Hitting opponents affected by &bRiptide&7 with &nfully charged shots&7 or in &e&lMelee &e&lStance &7executes &bRiptide Slash&7 that rapidly deals damage.____&bRiptide Slash&7 can be executed once every &b2.5s&7 per opponent.",
            Material.HEART_OF_THE_SEA
    )),

    // Techie
    TRAP_CAGE(new TrapCage()),
    TRAP_WIRE(new TrapWire()),
    NEURAL_THEFT(new PassiveTalent(
            "Neural Theft/CYber Hack",
            "&b&lNeural Theft__Every &b10s&7, hacks into opponents revealing their location and health.____&b&lCYber Hack:__&7A small virus that reveals opponent's location, slows them and applies &6&lVulnerability&7 for short duration.",
            Material.CHAINMAIL_HELMET
    )),

    // Killing Machine
    LASER_EYE(new LaserEye()),
    GRENADE(new ShellGrande()),

    // Shaman
    TOTEM(new Totem()),
    TOTEM_SLOWING_AURA(new TotemTalent(ResonanceType.SLOWING_AURA, 10)),
    TOTEM_HEALING_AURA(new TotemTalent(ResonanceType.HEALING_AURA, 12)),
    TOTEM_CYCLONE_AURA(new TotemTalent(ResonanceType.CYCLONE_AURA, 16)),
    TOTEM_ACCELERATION_AURA(new TotemTalent(ResonanceType.ACCELERATING_AURA, 20)),

    // Healer
    HEALING_ORB(new HealingOrb()),
    REVIVE_TOTEM(new ReviveTotem()),
    REVIVE(new PassiveTalent(
            "Revive",
            "When taking lethal damage, instead of dying, become a ghost and seek placed &bRevive Catalyst&7 to revive yourself. Once you use &bRevive Catalyst&7, it will be destroyed. All your catalysts will be highlighted for enemy players.",
            Material.GHAST_TEAR
    )),

    // Vampire
    VAMPIRE_PET(new VampirePet()),
    BAT_SWARM(new BatSwarm()),
    BLOOD_THIRST(new PassiveTalent(
            "Blood Thirst", """
            &c;;Your health is constantly drained.
                        
            Whenever you or your bats hit an opponent, you will gain a stack of &bblood&7, up to &b10&7 stacks.
                        
            Drink the blood to &cincrease your damage&7 and &cheal yourself&7.
                        
            &6;;Healing, damage boost, duration and cooldown is based on the amount of stacks consumed.
            """,
            Material.REDSTONE
    )),

    // Bounty Hunter
    SHORTY(new ShortyShotgun()),
    GRAPPLE(new GrappleHookTalent()),
    SMOKE_BOMB(new PassiveTalent(
            "Smoke Bomb",
            "Whenever your health falls below &c50%&7, you gain a &eSmoke Bomb&7.____Throw it to create a smoke field that &bblinds&7 everyone inside it and grant you a &bspeed boost&7.",
            Material.ENDERMAN_SPAWN_EGG
    )),

    // Heavy Knight
    UPPERCUT(new Uppercut()),
    UPDRAFT(new Updraft()),
    SLASH(new Slash()),

    // Orc
    ORC_GROWN(new OrcGrowl()),
    ORC_AXE(new OrcAxe()),
    ORC_PASSIVE(new PassiveTalent("", "", Material.STONE)),

    // Engineer
    ENGINEER_SENTRY(new EngineerSentry()),
    ENGINEER_TURRET(null),
    ENGINEER_RECALL(new EngineerRecall()),
    ENGINEER_PASSIVE(null),

    // ???
    SYNTHETIC_SUN(new SyntheticSun()),

    // test (keep last)
    TestChargeTalent(new TestChargeTalent());

    private final static Map<Talent, Talents> HANDLE_TO_ENUM;

    static {
        HANDLE_TO_ENUM = Maps.newHashMap();

        for (Talents value : values()) {
            HANDLE_TO_ENUM.put(value.getTalent(), value);
        }
    }

    private final Talent talent;

    Talents(Talent talent) {
        if (talent instanceof UltimateTalent) {
            throw new IllegalArgumentException("ultimate talent enum initiation");
        }
        this.talent = talent;
        if (talent instanceof Listener listener) {
            Bukkit.getPluginManager().registerEvents(listener, Main.getPlugin());
        }
    }

    public void startCd(Player player) {
        getTalent().startCd(player);
    }

    public String getName() {
        return getTalent().getName();
    }

    /**
     * Returns a handle of a talent.
     * <p>
     * Note that this method only returns a base handle,
     * for specific hero handles, use {@link #getTalent(Class)}.
     *
     * @return handle of a talent.
     */
    @Nonnull
    public Talent getTalent() {
        return talent;
    }

    /**
     * Returns a handle of a talent.
     * <p>
     * This method tries to cast the handle to the specified class.
     *
     * @param cast - Cast to.
     * @return handle of a talent.
     * @throws IllegalArgumentException if the cast is invalid.
     */
    @Nonnull
    public <E extends Talent> E getTalent(Class<E> cast) throws IllegalArgumentException {
        try {
            return cast.cast(talent);
        } catch (Exception e) {
            throw new IllegalArgumentException("talent is not of type " + cast.getSimpleName());
        }
    }

    /**
     * Gets the enum from a talent handle.
     *
     * @param talent - Talent handle.
     * @return the enum if present, or null.
     */
    @Nullable
    public static Talents fromTalent(@Nullable Talent talent) {
        if (talent == null) {
            return null;
        }

        return HANDLE_TO_ENUM.get(talent);
    }

}
