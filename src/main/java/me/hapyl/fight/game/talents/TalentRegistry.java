package me.hapyl.fight.game.talents;

import me.hapyl.fight.game.heroes.alchemist.Alchemist;
import me.hapyl.fight.game.heroes.archer.Archer;
import me.hapyl.fight.game.heroes.aurora.Aurora;
import me.hapyl.fight.game.heroes.bloodfield.Bloodfiend;
import me.hapyl.fight.game.heroes.bounty_hunter.BountyHunter;
import me.hapyl.fight.game.heroes.dark_mage.DarkMage;
import me.hapyl.fight.game.heroes.doctor.DrEd;
import me.hapyl.fight.game.heroes.echo.Echo;
import me.hapyl.fight.game.heroes.ender.Ender;
import me.hapyl.fight.game.heroes.engineer.Engineer;
import me.hapyl.fight.game.heroes.frostbite.Freazly;
import me.hapyl.fight.game.heroes.harbinger.Harbinger;
import me.hapyl.fight.game.heroes.healer.Healer;
import me.hapyl.fight.game.heroes.heavy_knight.SwordMaster;
import me.hapyl.fight.game.heroes.hercules.Hercules;
import me.hapyl.fight.game.heroes.juju.JuJu;
import me.hapyl.fight.game.heroes.km.KillingMachine;
import me.hapyl.fight.game.heroes.knight.BlastKnight;
import me.hapyl.fight.game.heroes.librarian.Librarian;
import me.hapyl.fight.game.heroes.mage.Mage;
import me.hapyl.fight.game.heroes.moonwalker.Moonwalker;
import me.hapyl.fight.game.heroes.nightmare.Nightmare;
import me.hapyl.fight.game.heroes.ninja.Ninja;
import me.hapyl.fight.game.heroes.nyx.Nyx;
import me.hapyl.fight.game.heroes.orc.Orc;
import me.hapyl.fight.game.heroes.pytaria.Pytaria;
import me.hapyl.fight.game.heroes.rogue.Rogue;
import me.hapyl.fight.game.heroes.shadow_assassin.ShadowAssassin;
import me.hapyl.fight.game.heroes.shaman.Shaman;
import me.hapyl.fight.game.heroes.shark.Shark;
import me.hapyl.fight.game.heroes.spark.Spark;
import me.hapyl.fight.game.heroes.swooper.Swooper;
import me.hapyl.fight.game.heroes.taker.Taker;
import me.hapyl.fight.game.heroes.tamer.Tamer;
import me.hapyl.fight.game.heroes.techie.Techie;
import me.hapyl.fight.game.heroes.troll.Troll;
import me.hapyl.fight.game.heroes.vampire.Vampire;
import me.hapyl.fight.game.heroes.vortex.Vortex;
import me.hapyl.fight.game.heroes.witcher.WitcherClass;
import me.hapyl.fight.game.heroes.zealot.Zealot;
import me.hapyl.fight.game.talents.alchemist.CauldronAbility;
import me.hapyl.fight.game.talents.alchemist.IntoxicationPassive;
import me.hapyl.fight.game.talents.alchemist.RandomPotion;
import me.hapyl.fight.game.talents.archer.HawkeyePassive;
import me.hapyl.fight.game.talents.archer.ShockDark;
import me.hapyl.fight.game.talents.archer.TripleShot;
import me.hapyl.fight.game.talents.aurora.CelesteArrow;
import me.hapyl.fight.game.talents.aurora.DivineIntervention;
import me.hapyl.fight.game.talents.aurora.EtherealArrow;
import me.hapyl.fight.game.talents.bloodfiend.BloodCup;
import me.hapyl.fight.game.talents.bloodfiend.BloodfiendPassive;
import me.hapyl.fight.game.talents.bloodfiend.TwinClaws;
import me.hapyl.fight.game.talents.bloodfiend.candlebane.CandlebaneTalent;
import me.hapyl.fight.game.talents.bloodfiend.chalice.BloodChaliceTalent;
import me.hapyl.fight.game.talents.bounty_hunter.GrappleHookTalent;
import me.hapyl.fight.game.talents.bounty_hunter.ShortyShotgun;
import me.hapyl.fight.game.talents.bounty_hunter.SmokeBombPassive;
import me.hapyl.fight.game.talents.dark_mage.*;
import me.hapyl.fight.game.talents.doctor.BlockMaelstromPassive;
import me.hapyl.fight.game.talents.doctor.ConfusionPotion;
import me.hapyl.fight.game.talents.doctor.HarvestBlocks;
import me.hapyl.fight.game.talents.echo.EchoTalent;
import me.hapyl.fight.game.talents.ender.EnderPassive;
import me.hapyl.fight.game.talents.ender.TeleportPearl;
import me.hapyl.fight.game.talents.ender.TransmissionBeacon;
import me.hapyl.fight.game.talents.engineer.EngineerRecall;
import me.hapyl.fight.game.talents.engineer.EngineerSentry;
import me.hapyl.fight.game.talents.engineer.EngineerTurret;
import me.hapyl.fight.game.talents.engineer.MagneticAttractionPassive;
import me.hapyl.fight.game.talents.frostbite.ChillAuraPassive;
import me.hapyl.fight.game.talents.frostbite.IceCageTalent;
import me.hapyl.fight.game.talents.frostbite.Icicles;
import me.hapyl.fight.game.talents.frostbite.IcyShardsPassive;
import me.hapyl.fight.game.talents.harbinger.MeleeStance;
import me.hapyl.fight.game.talents.harbinger.RiptidePassive;
import me.hapyl.fight.game.talents.harbinger.TidalWaveTalent;
import me.hapyl.fight.game.talents.healer.HealingOrb;
import me.hapyl.fight.game.talents.healer.RevivePassive;
import me.hapyl.fight.game.talents.healer.ReviveTotem;
import me.hapyl.fight.game.talents.heavy_knight.PerfectSequencePassive;
import me.hapyl.fight.game.talents.heavy_knight.Slash;
import me.hapyl.fight.game.talents.heavy_knight.Updraft;
import me.hapyl.fight.game.talents.heavy_knight.Uppercut;
import me.hapyl.fight.game.talents.hercules.HerculesJump;
import me.hapyl.fight.game.talents.hercules.HerculesShift;
import me.hapyl.fight.game.talents.hercules.PlungePassive;
import me.hapyl.fight.game.talents.juju.*;
import me.hapyl.fight.game.talents.km.LaserEye;
import me.hapyl.fight.game.talents.knight.*;
import me.hapyl.fight.game.talents.librarian.BlackHole;
import me.hapyl.fight.game.talents.librarian.EntityDarkness;
import me.hapyl.fight.game.talents.librarian.LibrarianShield;
import me.hapyl.fight.game.talents.librarian.WeaponDarkness;
import me.hapyl.fight.game.talents.mage.ArcaneMute;
import me.hapyl.fight.game.talents.mage.MageTransmission;
import me.hapyl.fight.game.talents.mage.SoulHarvestPassive;
import me.hapyl.fight.game.talents.moonwalker.GravityZone;
import me.hapyl.fight.game.talents.moonwalker.MoonPassive;
import me.hapyl.fight.game.talents.moonwalker.MoonPillarTalent;
import me.hapyl.fight.game.talents.moonwalker.MoonSliteBomb;
import me.hapyl.fight.game.talents.nightmare.InTheShadowsPassive;
import me.hapyl.fight.game.talents.nightmare.Paranoia;
import me.hapyl.fight.game.talents.nightmare.ShadowShift;
import me.hapyl.fight.game.talents.ninja.NinjaDash;
import me.hapyl.fight.game.talents.ninja.NinjaSmoke;
import me.hapyl.fight.game.talents.ninja.NinjaStylePassive;
import me.hapyl.fight.game.talents.nyx.ChaosGround;
import me.hapyl.fight.game.talents.nyx.NyxPassive;
import me.hapyl.fight.game.talents.nyx.WitherRosePath;
import me.hapyl.fight.game.talents.orc.DontAngerMePassive;
import me.hapyl.fight.game.talents.orc.OrcAxe;
import me.hapyl.fight.game.talents.orc.OrcGrowl;
import me.hapyl.fight.game.talents.pytaria.ExcellencyPassive;
import me.hapyl.fight.game.talents.pytaria.FlowerBreeze;
import me.hapyl.fight.game.talents.pytaria.FlowerEscape;
import me.hapyl.fight.game.talents.rogue.ExtraCut;
import me.hapyl.fight.game.talents.rogue.SecondWind;
import me.hapyl.fight.game.talents.rogue.Swayblade;
import me.hapyl.fight.game.talents.shadow_assassin.*;
import me.hapyl.fight.game.talents.shaman.*;
import me.hapyl.fight.game.talents.shark.SharkPassive;
import me.hapyl.fight.game.talents.shark.SubmergeTalent;
import me.hapyl.fight.game.talents.shark.Whirlpool;
import me.hapyl.fight.game.talents.spark.FireGuyPassive;
import me.hapyl.fight.game.talents.spark.Molotov;
import me.hapyl.fight.game.talents.spark.SparkFlash;
import me.hapyl.fight.game.talents.swooper.BlastPack;
import me.hapyl.fight.game.talents.swooper.Blink;
import me.hapyl.fight.game.talents.swooper.SmokeBomb;
import me.hapyl.fight.game.talents.swooper.SwooperPassive;
import me.hapyl.fight.game.talents.taker.DeathSwap;
import me.hapyl.fight.game.talents.taker.FatalReap;
import me.hapyl.fight.game.talents.taker.SpiritualBonesPassive;
import me.hapyl.fight.game.talents.tamer.MineOBall;
import me.hapyl.fight.game.talents.tamer.TamingTheEarth;
import me.hapyl.fight.game.talents.tamer.TamingTheTime;
import me.hapyl.fight.game.talents.tamer.TamingTheWind;
import me.hapyl.fight.game.talents.techie.*;
import me.hapyl.fight.game.talents.troll.LastLaughPassive;
import me.hapyl.fight.game.talents.troll.Repulsor;
import me.hapyl.fight.game.talents.troll.TrollSpin;
import me.hapyl.fight.game.talents.vampire.BatSwarm;
import me.hapyl.fight.game.talents.vampire.VampirePassive;
import me.hapyl.fight.game.talents.vampire.Bloodshift;
import me.hapyl.fight.game.talents.vampire.VampirePet;
import me.hapyl.fight.game.talents.vortex.*;
import me.hapyl.fight.game.talents.witcher.*;
import me.hapyl.fight.game.talents.zealot.BrokenHeartRadiation;
import me.hapyl.fight.game.talents.zealot.FerociousStrikes;
import me.hapyl.fight.game.talents.zealot.MaledictionVeil;
import me.hapyl.fight.game.talents.zealot.MalevolentHitshield;
import me.hapyl.fight.registry.AbstractStaticRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static me.hapyl.fight.database.key.DatabaseKey.ofEnum;

public final class TalentRegistry extends AbstractStaticRegistry<Talent> {

    /**
     * {@link Archer}
     */
    public static final TripleShot TRIPLE_SHOT;
    public static final ShockDark SHOCK_DARK;
    public static final HawkeyePassive HAWKEYE_ARROW;

    /**
     * {@link Alchemist}
     */
    public static final RandomPotion POTION;
    public static final CauldronAbility CAULDRON;
    public static final IntoxicationPassive INTOXICATION;

    /**
     * {@link Moonwalker}
     */
    public static final MoonPillarTalent MOONSLITE_PILLAR;
    public static final MoonSliteBomb MOONSLITE_BOMB;
    public static final GravityZone MOON_GRAVITY;
    public static final MoonPassive MOON_PASSIVE;

    /**
     * {@link Hercules}
     */
    public static final HerculesShift HERCULES_DASH;
    public static final HerculesJump HERCULES_UPDRAFT;
    public static final PlungePassive PLUNGE;

    /**
     * {@link Mage}
     */
    public static final MageTransmission MAGE_TRANSMISSION;
    public static final ArcaneMute ARCANE_MUTE;
    public static final SoulHarvestPassive SOUL_HARVEST;

    /**
     * {@link Pytaria}
     */
    public static final FlowerEscape FLOWER_ESCAPE;
    public static final FlowerBreeze FLOWER_BREEZE;
    public static final ExcellencyPassive EXCELLENCY;

    /**
     * {@link Troll}
     */
    public static final TrollSpin TROLL_SPIN;
    public static final Repulsor REPULSOR;
    public static final LastLaughPassive TROLL_PASSIVE;

    /**
     * {@link Tamer}
     */
    public static final MineOBall MINE_O_BALL;
    public static final TamingTheWind TAMING_THE_WIND;
    public static final TamingTheEarth TAMING_THE_EARTH;
    public static final TamingTheTime TAMING_THE_TIME;

    /**
     * {@link Nightmare}
     */
    public static final Paranoia PARANOIA;
    public static final ShadowShift SHADOW_SHIFT;
    public static final InTheShadowsPassive IN_THE_SHADOWS;

    /**
     * {@link DrEd}
     */
    public static final ConfusionPotion CONFUSION_POTION;
    public static final HarvestBlocks HARVEST;
    public static final BlockMaelstromPassive BLOCK_SHIELD;

    /**
     * {@link Ender}
     */
    public static final TeleportPearl TELEPORT_PEARL;
    public static final TransmissionBeacon TRANSMISSION_BEACON;
    public static final EnderPassive ENDER_PASSIVE;

    /**
     * {@link Spark}
     */
    public static final Molotov SPARK_MOLOTOV;
    public static final SparkFlash SPARK_FLASH;
    public static final FireGuyPassive FIRE_GUY;

    /**
     * {@link ShadowAssassin}
     */
    @Deprecated public static final ShadowPrism SHADOW_PRISM;
    @Deprecated public static final ShroudedStep SHROUDED_STEP;
    @Deprecated public static final DarkCoverPassive SECRET_SHADOW_WARRIOR_TECHNIQUE;

    public static final ShadowSwitch SHADOW_SWITCH;
    public static final DarkCover DARK_COVER;
    public static final ShadowAssassinClone SHADOW_ASSASSIN_CLONE;
    public static final ShadowEnergyPassive SHADOW_ENERGY;

    /**
     * {@link WitcherClass}
     */
    public static final Aard AARD;
    public static final Igny IGNY;
    public static final Kven KVEN;
    public static final Akciy AKCIY;
    public static final Irden IRDEN;
    public static final ComboPassive COMBO_SYSTEM;

    /**
     * {@link Vortex}
     */
    public static final VortexSlash VORTEX_SLASH;
    public static final VortexStarTalent VORTEX_STAR;
    public static final StarAligner STAR_ALIGNER;
    public static final LikeADreamPassive LIKE_A_DREAM;
    @Deprecated public static final EyesOfTheGalaxyPassive EYES_OF_THE_GALAXY;

    /**
     * {@link Freazly}
     */
    public static final Icicles ICICLES;
    public static final IceCageTalent ICE_CAGE;
    public static final IcyShardsPassive ICY_SHARDS;
    public static final ChillAuraPassive CHILL_AURA;

    /**
     * {@link DarkMage}
     */
    public static final BlindingCurse BLINDING_CURSE;
    public static final SlowingAura SLOWING_AURA;
    public static final HealingAura HEALING_AURA;
    public static final ShadowClone SHADOW_CLONE;
    public static final WitherRosePassive DARK_MAGE_PASSIVE;

    /**
     * {@link BlastKnight}
     */
    public static final StoneCastle STONE_CASTLE;
    public static final Discharge DISCHARGE;
    @Deprecated public static final Spear SPEAR;
    @Deprecated public static final SlownessPotion SLOWNESS_POTION;
    public static final QuantumEnergyPassive SHIELDED;

    /**
     * {@link Ninja}
     */
    public static final NinjaDash NINJA_DASH;
    public static final NinjaSmoke NINJA_SMOKE;
    public static final NinjaStylePassive FLEET_FOOT;

    /**
     * {@link Taker}
     */
    public static final FatalReap FATAL_REAP;
    public static final DeathSwap DEATH_SWAP;
    public static final SpiritualBonesPassive SPIRITUAL_BONES;

    /**
     * {@link JuJu}
     */
    public static final ArrowShield ARROW_SHIELD;
    @Deprecated public static final Climb CLIMB;
    public static final TricksOfTheJungle TRICKS_OF_THE_JUNGLE;
    public static final PoisonZone POISON_ZONE;
    public static final ClimbPassive JUJU_PASSIVE;

    /**
     * {@link Swooper}
     */
    public static final BlastPack BLAST_PACK;
    public static final SmokeBomb SWOOPER_SMOKE_BOMB;
    @Deprecated public static final Blink BLINK;
    public static final SwooperPassive SWOOPER_PASSIVE;

    /**
     * {@link Shark}
     */
    public static final SubmergeTalent SUBMERGE;
    public static final Whirlpool WHIRLPOOL;
    public static final SharkPassive SHARK_PASSIVE;

    /**
     * {@link Librarian}
     */
    public static final BlackHole BLACK_HOLE;
    public static final EntityDarkness ENTITY_DARKNESS;
    public static final LibrarianShield LIBRARIAN_SHIELD;
    public static final WeaponDarkness WEAPON_DARKNESS;

    /**
     * {@link Harbinger}
     */
    public static final MeleeStance STANCE;
    public static final TidalWaveTalent TIDAL_WAVE;
    public static final RiptidePassive RIPTIDE;

    /**
     * {@link Techie}
     */
    public static final Saboteur SABOTEUR;
    public static final CipherLock CIPHER_LOCK;

    @Deprecated public static final TrapCage TRAP_CAGE;
    @Deprecated public static final TrapWire TRAP_WIRE;
    public static final NeuralTheftPassive NEURAL_THEFT;

    /**
     * {@link KillingMachine}
     */
    public static final LaserEye LASER_EYE;
    //public static final ShellGrande GRENADE;

    /**
     * {@link Shaman}
     */
    public static final TotemTalent TOTEM;
    public static final TotemImprisonment TOTEM_IMPRISONMENT;
    public static final ShamanMarkTalent SHAMAN_MARK;
    public static final OverhealPassive OVERHEAL;
    public static final SlimeGunkTalent SLIMY_GUNK;
    @Deprecated public static final ArcaneLinkPassive TOTEM_LINK;

    /**
     * {@link Healer}
     */
    public static final HealingOrb HEALING_ORB;
    public static final ReviveTotem REVIVE_TOTEM;
    public static final RevivePassive REVIVE;

    /**
     * {@link Vampire}
     */
    @Deprecated public static final VampirePet VAMPIRE_PET;
    public static final Bloodshift BLOODSHIFT;
    public static final BatSwarm BAT_SWARM;
    public static final VampirePassive VANPIRE_PASSIVE;

    /**
     * {@link BountyHunter}
     */
    public static final ShortyShotgun SHORTY;
    public static final GrappleHookTalent GRAPPLE;
    public static final SmokeBombPassive SMOKE_BOMB;

    /**
     * {@link SwordMaster}
     */
    public static final Uppercut UPPERCUT;
    public static final Updraft UPDRAFT;
    public static final Slash SLASH;
    public static final PerfectSequencePassive SWORD_MASTER_PASSIVE;

    /**
     * {@link Orc}
     */
    public static final OrcGrowl ORC_GROWN;
    public static final OrcAxe ORC_AXE;
    public static final DontAngerMePassive ORC_PASSIVE;

    /**
     * {@link Engineer}
     */
    public static final EngineerSentry ENGINEER_SENTRY;
    public static final EngineerTurret ENGINEER_TURRET;
    public static final EngineerRecall ENGINEER_RECALL;
    public static final MagneticAttractionPassive ENGINEER_PASSIVE;

    /**
     * {@link Bloodfiend}
     */
    public static final TwinClaws TWIN_CLAWS;
    public static final CandlebaneTalent CANDLEBANE;
    public static final BloodChaliceTalent BLOOD_CHALICE;
    public static final BloodCup BLOOD_CUP;
    public static final BloodfiendPassive SUCCULENCE;

    /**
     * {@link Zealot}
     */
    public static final BrokenHeartRadiation BROKEN_HEART_RADIATION;
    public static final MalevolentHitshield MALEVOLENT_HITSHIELD;
    public static final FerociousStrikes FEROCIOUS_STRIKES;
    @Deprecated public static final MaledictionVeil MALEDICTION_VEIL;

    /**
     * {@link Rogue}
     */
    public static final ExtraCut EXTRA_CUT;
    public static final Swayblade SWAYBLADE;
    public static final SecondWind SECOND_WIND;

    /**
     * {@link Aurora}
     */
    public static final CelesteArrow CELESTE_ARROW;
    public static final EtherealArrow ETHEREAL_ARROW;
    public static final DivineIntervention DIVINE_INTERVENTION;

    /**
     * {@link Nyx}
     */
    public static final WitherRosePath WITHER_ROSE_PATH;
    public static final ChaosGround CHAOS_GROUND;
    public static final NyxPassive NYX_PASSIVE;

    /**
     * {@link Echo}
     */
    public static final EchoTalent ECHO;


    /*/ don't put anything below here /*/
    private static final Set<Talent> values;

    static {
        AbstractStaticRegistry.ensure(TalentRegistry.class, Talent.class);

        values = new LinkedHashSet<>();

        /*/ ⬇️ Register below ⬇️ /*/

        TRIPLE_SHOT = register(new TripleShot(ofEnum("TRIPLE_SHOT")));
        SHOCK_DARK = register(new ShockDark(ofEnum("SHOCK_DARK")));
        HAWKEYE_ARROW = register(new HawkeyePassive(ofEnum("HAWKEYE_ARROW")));

        POTION = register(new RandomPotion(ofEnum("POTION")));
        CAULDRON = register(new CauldronAbility(ofEnum("CAULDRON")));
        INTOXICATION = register(new IntoxicationPassive(ofEnum("INTOXICATION")));

        MOONSLITE_PILLAR = register(new MoonPillarTalent(ofEnum("MOONSLITE_PILLAR")));
        MOONSLITE_BOMB = register(new MoonSliteBomb(ofEnum("MOONSLITE_BOMB")));
        MOON_GRAVITY = register(new GravityZone(ofEnum("MOON_GRAVITY")));
        MOON_PASSIVE = register(new MoonPassive(ofEnum("MOON_PASSIVE")));

        HERCULES_DASH = register(new HerculesShift(ofEnum("HERCULES_DASH")));
        HERCULES_UPDRAFT = register(new HerculesJump(ofEnum("HERCULES_UPDRAFT")));
        PLUNGE = register(new PlungePassive(ofEnum("PLUNGE")));

        MAGE_TRANSMISSION = register(new MageTransmission(ofEnum("MAGE_TRANSMISSION")));
        ARCANE_MUTE = register(new ArcaneMute(ofEnum("ARCANE_MUTE")));
        SOUL_HARVEST = register(new SoulHarvestPassive(ofEnum("SOUL_HARVEST")));

        FLOWER_ESCAPE = register(new FlowerEscape(ofEnum("FLOWER_ESCAPE")));
        FLOWER_BREEZE = register(new FlowerBreeze(ofEnum("FLOWER_BREEZE")));
        EXCELLENCY = register(new ExcellencyPassive(ofEnum("EXCELLENCY")));

        TROLL_SPIN = register(new TrollSpin(ofEnum("TROLL_SPIN")));
        REPULSOR = register(new Repulsor(ofEnum("REPULSOR")));
        TROLL_PASSIVE = register(new LastLaughPassive(ofEnum("TROLL_PASSIVE")));

        MINE_O_BALL = register(new MineOBall(ofEnum("MINE_O_BALL")));
        TAMING_THE_WIND = register(new TamingTheWind(ofEnum("TAMING_THE_WIND")));
        TAMING_THE_EARTH = register(new TamingTheEarth(ofEnum("TAMING_THE_EARTH")));
        TAMING_THE_TIME = register(new TamingTheTime(ofEnum("TAMING_THE_TIME")));

        PARANOIA = register(new Paranoia(ofEnum("PARANOIA")));
        SHADOW_SHIFT = register(new ShadowShift(ofEnum("SHADOW_SHIFT")));
        IN_THE_SHADOWS = register(new InTheShadowsPassive(ofEnum("IN_THE_SHADOWS")));

        CONFUSION_POTION = register(new ConfusionPotion(ofEnum("CONFUSION_POTION")));
        HARVEST = register(new HarvestBlocks(ofEnum("HARVEST")));
        BLOCK_SHIELD = register(new BlockMaelstromPassive(ofEnum("BLOCK_SHIELD")));

        TELEPORT_PEARL = register(new TeleportPearl(ofEnum("TELEPORT_PEARL")));
        TRANSMISSION_BEACON = register(new TransmissionBeacon(ofEnum("TRANSMISSION_BEACON")));
        ENDER_PASSIVE = register(new EnderPassive(ofEnum("ENDER_PASSIVE")));

        SPARK_MOLOTOV = register(new Molotov(ofEnum("SPARK_MOLOTOV")));
        SPARK_FLASH = register(new SparkFlash(ofEnum("SPARK_FLASH")));
        FIRE_GUY = register(new FireGuyPassive(ofEnum("FIRE_GUY")));

        SHADOW_PRISM = register(new ShadowPrism(ofEnum("SHADOW_PRISM")));
        SHROUDED_STEP = register(new ShroudedStep(ofEnum("SHROUDED_STEP")));
        SECRET_SHADOW_WARRIOR_TECHNIQUE = register(new DarkCoverPassive(ofEnum("SECRET_SHADOW_WARRIOR_TECHNIQUE")));
        SHADOW_SWITCH = register(new ShadowSwitch(ofEnum("SHADOW_SWITCH")));
        DARK_COVER = register(new DarkCover(ofEnum("DARK_COVER")));
        SHADOW_ASSASSIN_CLONE = register(new ShadowAssassinClone(ofEnum("SHADOW_ASSASSIN_CLONE")));
        SHADOW_ENERGY = register(new ShadowEnergyPassive(ofEnum("SHADOW_ENERGY")));

        AARD = register(new Aard(ofEnum("AARD")));
        IGNY = register(new Igny(ofEnum("IGNY")));
        KVEN = register(new Kven(ofEnum("KVEN")));
        AKCIY = register(new Akciy(ofEnum("AKCIY")));
        IRDEN = register(new Irden(ofEnum("IRDEN")));
        COMBO_SYSTEM = register(new ComboPassive(ofEnum("COMBO_SYSTEM")));

        VORTEX_SLASH = register(new VortexSlash(ofEnum("VORTEX_SLASH")));
        VORTEX_STAR = register(new VortexStarTalent(ofEnum("VORTEX_STAR")));
        STAR_ALIGNER = register(new StarAligner(ofEnum("STAR_ALIGNER")));
        LIKE_A_DREAM = register(new LikeADreamPassive(ofEnum("LIKE_A_DREAM")));
        EYES_OF_THE_GALAXY = register(new EyesOfTheGalaxyPassive(ofEnum("EYES_OF_THE_GALAXY")));

        ICICLES = register(new Icicles(ofEnum("ICICLES")));
        ICE_CAGE = register(new IceCageTalent(ofEnum("ICE_CAGE")));
        ICY_SHARDS = register(new IcyShardsPassive(ofEnum("ICY_SHARDS")));
        CHILL_AURA = register(new ChillAuraPassive(ofEnum("CHILL_AURA")));

        BLINDING_CURSE = register(new BlindingCurse(ofEnum("BLINDING_CURSE")));
        SLOWING_AURA = register(new SlowingAura(ofEnum("SLOWING_AURA")));
        HEALING_AURA = register(new HealingAura(ofEnum("HEALING_AURA")));
        SHADOW_CLONE = register(new ShadowClone(ofEnum("SHADOW_CLONE")));
        DARK_MAGE_PASSIVE = register(new WitherRosePassive(ofEnum("DARK_MAGE_PASSIVE")));

        STONE_CASTLE = register(new StoneCastle(ofEnum("STONE_CASTLE")));
        DISCHARGE = register(new Discharge(ofEnum("DISCHARGE")));
        SPEAR = register(new Spear(ofEnum("SPEAR")));
        SLOWNESS_POTION = register(new SlownessPotion(ofEnum("SLOWNESS_POTION")));
        SHIELDED = register(new QuantumEnergyPassive(ofEnum("SHIELDED")));

        NINJA_DASH = register(new NinjaDash(ofEnum("NINJA_DASH")));
        NINJA_SMOKE = register(new NinjaSmoke(ofEnum("NINJA_SMOKE")));
        FLEET_FOOT = register(new NinjaStylePassive(ofEnum("FLEET_FOOT")));

        FATAL_REAP = register(new FatalReap(ofEnum("FATAL_REAP")));
        DEATH_SWAP = register(new DeathSwap(ofEnum("DEATH_SWAP")));
        SPIRITUAL_BONES = register(new SpiritualBonesPassive(ofEnum("SPIRITUAL_BONES")));

        ARROW_SHIELD = register(new ArrowShield(ofEnum("ARROW_SHIELD")));
        CLIMB = register(new Climb(ofEnum("CLIMB")));
        TRICKS_OF_THE_JUNGLE = register(new TricksOfTheJungle(ofEnum("TRICKS_OF_THE_JUNGLE")));
        POISON_ZONE = register(new PoisonZone(ofEnum("POISON_ZONE")));
        JUJU_PASSIVE = register(new ClimbPassive(ofEnum("JUJU_PASSIVE")));

        BLAST_PACK = register(new BlastPack(ofEnum("BLAST_PACK")));
        SWOOPER_SMOKE_BOMB = register(new SmokeBomb(ofEnum("SWOOPER_SMOKE_BOMB")));
        BLINK = register(new Blink(ofEnum("BLINK")));
        SWOOPER_PASSIVE = register(new SwooperPassive(ofEnum("SWOOPER_PASSIVE")));

        SUBMERGE = register(new SubmergeTalent(ofEnum("SUBMERGE")));
        WHIRLPOOL = register(new Whirlpool(ofEnum("WHIRLPOOL")));
        SHARK_PASSIVE = register(new SharkPassive(ofEnum("SHARK_PASSIVE")));

        BLACK_HOLE = register(new BlackHole(ofEnum("BLACK_HOLE")));
        ENTITY_DARKNESS = register(new EntityDarkness(ofEnum("ENTITY_DARKNESS")));
        LIBRARIAN_SHIELD = register(new LibrarianShield(ofEnum("LIBRARIAN_SHIELD")));
        WEAPON_DARKNESS = register(new WeaponDarkness(ofEnum("WEAPON_DARKNESS")));

        STANCE = register(new MeleeStance(ofEnum("STANCE")));
        TIDAL_WAVE = register(new TidalWaveTalent(ofEnum("TIDAL_WAVE")));
        RIPTIDE = register(new RiptidePassive(ofEnum("RIPTIDE")));

        SABOTEUR = register(new Saboteur(ofEnum("SABOTEUR")));
        CIPHER_LOCK = register(new CipherLock(ofEnum("CIPHER_LOCK")));
        TRAP_CAGE = register(new TrapCage(ofEnum("TRAP_CAGE")));
        TRAP_WIRE = register(new TrapWire(ofEnum("TRAP_WIRE")));
        NEURAL_THEFT = register(new NeuralTheftPassive(ofEnum("NEURAL_THEFT")));

        LASER_EYE = register(new LaserEye(ofEnum("LASER_EYE")));

        TOTEM = register(new TotemTalent(ofEnum("TOTEM")));
        TOTEM_IMPRISONMENT = register(new TotemImprisonment(ofEnum("TOTEM_IMPRISONMENT")));
        SHAMAN_MARK = register(new ShamanMarkTalent(ofEnum("SHAMAN_MARK")));
        OVERHEAL = register(new OverhealPassive(ofEnum("OVERHEAL")));
        SLIMY_GUNK = register(new SlimeGunkTalent(ofEnum("SLIMY_GUNK")));
        TOTEM_LINK = register(new ArcaneLinkPassive(ofEnum("TOTEM_LINK")));

        HEALING_ORB = register(new HealingOrb(ofEnum("HEALING_ORB")));
        REVIVE_TOTEM = register(new ReviveTotem(ofEnum("REVIVE_TOTEM")));
        REVIVE = register(new RevivePassive(ofEnum("TOTEM")));

        VAMPIRE_PET = register(new VampirePet(ofEnum("VAMPIRE_PET")));
        BLOODSHIFT = register(new Bloodshift(ofEnum("BLOODSHIFT")));
        BAT_SWARM = register(new BatSwarm(ofEnum("BAT_SWARM")));
        VANPIRE_PASSIVE = register(new VampirePassive(ofEnum("VAMPIRE_PASSIVE")));

        SHORTY = register(new ShortyShotgun(ofEnum("SHORTY")));
        GRAPPLE = register(new GrappleHookTalent(ofEnum("GRAPPLE")));
        SMOKE_BOMB = register(new SmokeBombPassive(ofEnum("SMOKE_BOMB")));

        UPPERCUT = register(new Uppercut(ofEnum("UPPERCUT")));
        UPDRAFT = register(new Updraft(ofEnum("UPDRAFT")));
        SLASH = register(new Slash(ofEnum("SLASH")));
        SWORD_MASTER_PASSIVE = register(new PerfectSequencePassive(ofEnum("SWORD_MASTER_PASSIVE")));

        ORC_GROWN = register(new OrcGrowl(ofEnum("ORC_GROWN")));
        ORC_AXE = register(new OrcAxe(ofEnum("ORC_AXE")));
        ORC_PASSIVE = register(new DontAngerMePassive(ofEnum("ORC_PASSIVE")));

        ENGINEER_SENTRY = register(new EngineerSentry(ofEnum("ENGINEER_SENTRY")));
        ENGINEER_TURRET = register(new EngineerTurret(ofEnum("ENGINEER_TURRET")));
        ENGINEER_RECALL = register(new EngineerRecall(ofEnum("ENGINEER_RECALL")));
        ENGINEER_PASSIVE = register(new MagneticAttractionPassive(ofEnum("ENGINEER_PASSIVE")));

        TWIN_CLAWS = register(new TwinClaws(ofEnum("TWIN_CLAWS")));
        CANDLEBANE = register(new CandlebaneTalent(ofEnum("CANDLEBANE")));
        BLOOD_CHALICE = register(new BloodChaliceTalent(ofEnum("BLOOD_CHALICE")));
        BLOOD_CUP = register(new BloodCup(ofEnum("BLOOD_CUP")));
        SUCCULENCE = register(new BloodfiendPassive(ofEnum("SUCCULENCE")));

        BROKEN_HEART_RADIATION = register(new BrokenHeartRadiation(ofEnum("BROKEN_HEART_RADIATION")));
        MALEVOLENT_HITSHIELD = register(new MalevolentHitshield(ofEnum("MALEVOLENT_HITSHIELD")));
        FEROCIOUS_STRIKES = register(new FerociousStrikes(ofEnum("FEROCIOUS_STRIKES")));
        MALEDICTION_VEIL = register(new MaledictionVeil(ofEnum("MALEDICTION_VEIL")));

        EXTRA_CUT = register(new ExtraCut(ofEnum("EXTRA_CUT")));
        SWAYBLADE = register(new Swayblade(ofEnum("SWAYBLADE")));
        SECOND_WIND = register(new SecondWind(ofEnum("SECOND_WIND")));

        CELESTE_ARROW = register(new CelesteArrow(ofEnum("CELESTE_ARROW")));
        ETHEREAL_ARROW = register(new EtherealArrow(ofEnum("ETHEREAL_ARROW")));
        DIVINE_INTERVENTION = register(new DivineIntervention(ofEnum("DIVINE_INTERVENTION")));

        WITHER_ROSE_PATH = register(new WitherRosePath(ofEnum("WITHER_ROSE_PATH")));
        CHAOS_GROUND = register(new ChaosGround(ofEnum("CHAOS_GROUND")));
        NYX_PASSIVE = register(new NyxPassive(ofEnum("NYX_PASSIVE")));

        ECHO = register(new EchoTalent(ofEnum("ECHO")));
    }

    @Nonnull
    public static List<Talent> values() {
        return AbstractStaticRegistry.values(values);
    }

    @Nonnull
    public static List<String> keys() {
        return AbstractStaticRegistry.keys(values);
    }

    @Nonnull
    public static Talent ofString(@Nonnull String string) {
        return AbstractStaticRegistry.ofString(values, string, null);
    }

    @Nullable
    public static Talent ofStringOrNull(@Nonnull String string) {
        return AbstractStaticRegistry.ofStringOrNull(values, string);
    }

    private static <E extends Talent> E register(@Nonnull E e) {
        values.add(e);
        return e;
    }

}
