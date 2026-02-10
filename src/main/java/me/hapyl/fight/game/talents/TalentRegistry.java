package me.hapyl.fight.game.talents;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.KeyFunction;
import me.hapyl.fight.game.heroes.alchemist.Alchemist;
import me.hapyl.fight.game.heroes.archer.Archer;
import me.hapyl.fight.game.heroes.aurora.Aurora;
import me.hapyl.fight.game.heroes.bloodfield.Bloodfiend;
import me.hapyl.fight.game.heroes.bounty_hunter.BountyHunter;
import me.hapyl.fight.game.heroes.dark_mage.DarkMage;
import me.hapyl.fight.game.heroes.dylan.Dylan;
import me.hapyl.fight.game.heroes.doctor.DrEd;
import me.hapyl.fight.game.heroes.echo.Echo;
import me.hapyl.fight.game.heroes.ender.Ender;
import me.hapyl.fight.game.heroes.engineer.Engineer;
import me.hapyl.fight.game.heroes.frostbite.Freazly;
import me.hapyl.fight.game.heroes.harbinger.Harbinger;
import me.hapyl.fight.game.heroes.healer.Healer;
import me.hapyl.fight.game.heroes.heavy_knight.SwordMaster;
import me.hapyl.fight.game.heroes.hercules.Hercules;
import me.hapyl.fight.game.heroes.himari.Himari;
import me.hapyl.fight.game.heroes.inferno.Inferno;
import me.hapyl.fight.game.heroes.jester.Jester;
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
import me.hapyl.fight.game.heroes.ronin.Ronin;
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
import me.hapyl.fight.game.heroes.warden.Warden;
import me.hapyl.fight.game.heroes.witcher.WitcherClass;
import me.hapyl.fight.game.heroes.zealot.Zealot;
import me.hapyl.fight.game.talents.alchemist.CauldronAbility;
import me.hapyl.fight.game.talents.alchemist.IntoxicationPassive;
import me.hapyl.fight.game.talents.alchemist.PotionBundle;
import me.hapyl.fight.game.talents.alchemist.RandomPotion;
import me.hapyl.fight.game.talents.archer.HawkeyePassive;
import me.hapyl.fight.game.talents.archer.ShockDart;
import me.hapyl.fight.game.talents.archer.TripleShot;
import me.hapyl.fight.game.talents.aurora.CelesteArrow;
import me.hapyl.fight.game.talents.aurora.EtherealArrow;
import me.hapyl.fight.game.talents.aurora.GuardianAngel;
import me.hapyl.fight.game.talents.bloodfiend.BloodCup;
import me.hapyl.fight.game.talents.bloodfiend.BloodfiendPassive;
import me.hapyl.fight.game.talents.bloodfiend.SpectralForm;
import me.hapyl.fight.game.talents.bloodfiend.TwinClaws;
import me.hapyl.fight.game.talents.bloodfiend.candlebane.CandlebaneTalent;
import me.hapyl.fight.game.talents.bloodfiend.chalice.BloodChaliceTalent;
import me.hapyl.fight.game.talents.bounty_hunter.BountyHunterPassive;
import me.hapyl.fight.game.talents.bounty_hunter.GrappleHookTalent;
import me.hapyl.fight.game.talents.bounty_hunter.ShortyShotgun;
import me.hapyl.fight.game.talents.bounty_hunter.SmokeBombTalent;
import me.hapyl.fight.game.talents.dark_mage.*;
import me.hapyl.fight.game.talents.doctor.BlockMaelstromPassive;
import me.hapyl.fight.game.talents.doctor.ConfusionPotion;
import me.hapyl.fight.game.talents.doctor.HarvestBlocks;
import me.hapyl.fight.game.talents.dylan.*;
import me.hapyl.fight.game.talents.echo.EchoTrapTalent;
import me.hapyl.fight.game.talents.echo.EchoWorldTalent;
import me.hapyl.fight.game.talents.ender.EnderPassive;
import me.hapyl.fight.game.talents.ender.TeleportPearl;
import me.hapyl.fight.game.talents.ender.TransmissionBeacon;
import me.hapyl.fight.game.talents.engineer.*;
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
import me.hapyl.fight.game.talents.heavy_knight.SwordMasterPassive;
import me.hapyl.fight.game.talents.heavy_knight.Slash;
import me.hapyl.fight.game.talents.heavy_knight.Updraft;
import me.hapyl.fight.game.talents.heavy_knight.Uppercut;
import me.hapyl.fight.game.talents.hercules.HerculesJump;
import me.hapyl.fight.game.talents.hercules.HerculesShift;
import me.hapyl.fight.game.talents.hercules.PlungePassive;
import me.hapyl.fight.game.talents.himari.DeadEye;
import me.hapyl.fight.game.talents.himari.LuckyDay;
import me.hapyl.fight.game.talents.himari.SpikeBarrier;
import me.hapyl.fight.game.talents.inferno.*;
import me.hapyl.fight.game.talents.jester.MusicBoxTalent;
import me.hapyl.fight.game.talents.jester.TakeACakeToTheFace;
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
import me.hapyl.fight.game.talents.ronin.ChargeAttack;
import me.hapyl.fight.game.talents.ronin.RoninDash;
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
import me.hapyl.fight.game.talents.taker.Shadowfall;
import me.hapyl.fight.game.talents.taker.SpiritualBonesPassive;
import me.hapyl.fight.game.talents.tamer.MineOBall;
import me.hapyl.fight.game.talents.tamer.TamingTheEarth;
import me.hapyl.fight.game.talents.tamer.TamingTheTime;
import me.hapyl.fight.game.talents.tamer.TamingTheWind;
import me.hapyl.fight.game.talents.techie.*;
import me.hapyl.fight.game.talents.troll.LastLaughPassive;
import me.hapyl.fight.game.talents.troll.Repulsor;
import me.hapyl.fight.game.talents.troll.TrollSpin;
import me.hapyl.fight.game.talents.vampire.*;
import me.hapyl.fight.game.talents.vortex.*;
import me.hapyl.fight.game.talents.warden.Disunion;
import me.hapyl.fight.game.talents.warden.Fracture;
import me.hapyl.fight.game.talents.warden.Paradox;
import me.hapyl.fight.game.talents.witcher.*;
import me.hapyl.fight.game.talents.zealot.BrokenHeartRadiation;
import me.hapyl.fight.game.talents.zealot.FerociousStrikes;
import me.hapyl.fight.game.talents.zealot.MaledictionVeil;
import me.hapyl.fight.game.talents.zealot.MalevolentHitshieldTalent;
import me.hapyl.fight.registry.AbstractStaticRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class TalentRegistry extends AbstractStaticRegistry<Talent> {

    /**
     * {@link Archer}
     */
    public static final TripleShot TRIPLE_SHOT;
    public static final ShockDart SHOCK_DART;
    public static final HawkeyePassive HAWKEYE_ARROW;

    /**
     * {@link Alchemist}
     */
    public static final RandomPotion POTION;
    public static final CauldronAbility CAULDRON;
    public static final PotionBundle POTION_BUNDLE;
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
    public static final Shadowfall SHADOWFALL;
    public static final SpiritualBonesPassive SPIRITUAL_BONES;

    /**
     * {@link JuJu}
     */
    public static final ArrowShieldTalent ARROW_SHIELD;
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
    @Deprecated public static final Bloodshift BLOODSHIFT;
    @Deprecated public static final BatSwarm BAT_SWARM;

    public static final BloodDebtTalent BLOOD_DEBT;
    public static final BatTransferTalent BAT_TRANSFER;
    public static final VampirePassive VAMPIRE_PASSIVE;

    /**
     * {@link BountyHunter}
     */
    public static final ShortyShotgun SHORTY;
    public static final GrappleHookTalent GRAPPLE;
    public static final SmokeBombTalent SMOKE_BOMB;
    public static final BountyHunterPassive BOUNTY_HUNTER_PASSIVE;

    /**
     * {@link SwordMaster}
     */
    public static final Uppercut UPPERCUT;
    public static final Updraft UPDRAFT;
    public static final Slash SLASH;
    public static final SwordMasterPassive SWORD_MASTER_PASSIVE;

    /**
     * {@link Orc}
     */
    public static final OrcGrowl ORC_GROWN;
    public static final OrcAxe ORC_AXE;
    public static final DontAngerMePassive ORC_PASSIVE;

    /**
     * {@link Engineer}
     */
    @Deprecated public static final EngineerSpotter ENGINEER_SENTRY;
    @Deprecated public static final EngineerRecall ENGINEER_RECALL;
    public static final EngineerSentry ENGINEER_TURRET;
    public static final EngineerDispenser ENGINEER_DISPENSER;
    public static final MagneticAttractionPassive ENGINEER_PASSIVE;

    /**
     * {@link Bloodfiend}
     */
    public static final TwinClaws TWIN_CLAWS;
    public static final CandlebaneTalent CANDLEBANE;
    public static final BloodChaliceTalent BLOOD_CHALICE;
    public static final SpectralForm SPECTRAL_FORM;
    public static final BloodCup BLOOD_CUP;
    public static final BloodfiendPassive SUCCULENCE;

    /**
     * {@link Zealot}
     */
    public static final BrokenHeartRadiation BROKEN_HEART_RADIATION;
    public static final MalevolentHitshieldTalent MALEVOLENT_HITSHIELD;
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
    public static final GuardianAngel GUARDIAN_ANGEL;

    /**
     * {@link Nyx}
     */
    public static final WitherRosePath WITHER_ROSE_PATH;
    public static final ChaosGround CHAOS_GROUND;
    public static final NyxPassive NYX_PASSIVE;

    /**
     * {@link Echo}
     */
    public static final EchoWorldTalent ECHO_WORLD;
    public static final EchoTrapTalent ECHO_TRAP;

    /**
     * {@link Jester}
     */
    public static final MusicBoxTalent MUSIC_BOX;
    public static final TakeACakeToTheFace TAKE_A_CAKE_TO_THE_FACE;

    /**
     * {@link Ronin}
     */
    public static final ChargeAttack CHARGE_ATTACK;
    public static final RoninDash RONIN_DASH;

    /**
     * {@link Himari}
     */
    public static final LuckyDay LUCKY_DAY;
    public static final DeadEye DEAD_EYE;
    public static final SpikeBarrier SPIKE_BARRIER;

    /**
     * {@link Warden}
     */
    public static final Fracture FRACTURE;
    public static final Disunion DISUNION;
    public static final Paradox PARADOX;

    /**
     * {@link Inferno}
     */
    public static final FirePitTalent FIRE_PIT;
    public static final DemonSplitTalentQuazii DEMON_SPLIT_QUAZII;
    public static final DemonSplitTalentTyphoeus DEMON_SPLIT_TYPHOEUS;
    public static final DemonKindPassiveTalent DEMON_KIND;
    
    /**
     * {@link Dylan}
     */
    public static final SummonWhelp SUMMON_WHELP;
    public static final HellfireWard HELLFIRE_WARD;
    public static final WhelpAttack WHELP_ATTACK;
    public static final Blightwhirl BLIGHTWHIRL;
    
    public static final DylanPassive DYLAN_PASSIVE;
    
    /*/ don't put anything below here /*/
    private static final Set<Talent> values;


    static {
        AbstractStaticRegistry.ensure(TalentRegistry.class, Talent.class);

        values = new LinkedHashSet<>();

        /*/ ⬇️ Register below ⬇️ /*/

        TRIPLE_SHOT = register("triple_shot", TripleShot::new);
        SHOCK_DART = register("shock_dart", ShockDart::new);
        HAWKEYE_ARROW = register("hawkeye_arrow", HawkeyePassive::new);

        POTION = register("potion", RandomPotion::new);
        CAULDRON = register("cauldron", CauldronAbility::new);
        POTION_BUNDLE = register("potion_bundle", PotionBundle::new);
        INTOXICATION = register("intoxication", IntoxicationPassive::new);

        MOONSLITE_PILLAR = register("moonslite_pillar", MoonPillarTalent::new);
        MOONSLITE_BOMB = register("moonslite_bomb", MoonSliteBomb::new);
        MOON_GRAVITY = register("moon_gravity", GravityZone::new);
        MOON_PASSIVE = register("moon_passive", MoonPassive::new);

        HERCULES_DASH = register("hercules_dash", HerculesShift::new);
        HERCULES_UPDRAFT = register("hercules_updraft", HerculesJump::new);
        PLUNGE = register("plunge", PlungePassive::new);

        MAGE_TRANSMISSION = register("mage_transmission", MageTransmission::new);
        ARCANE_MUTE = register("arcane_mute", ArcaneMute::new);
        SOUL_HARVEST = register("soul_harvest", SoulHarvestPassive::new);

        FLOWER_ESCAPE = register("flower_escape", FlowerEscape::new);
        FLOWER_BREEZE = register("flower_breeze", FlowerBreeze::new);
        EXCELLENCY = register("excellency", ExcellencyPassive::new);

        TROLL_SPIN = register("troll_spin", TrollSpin::new);
        REPULSOR = register("repulsor", Repulsor::new);
        TROLL_PASSIVE = register("troll_passive", LastLaughPassive::new);

        MINE_O_BALL = register("mine_o_ball", MineOBall::new);
        TAMING_THE_WIND = register("taming_the_wind", TamingTheWind::new);
        TAMING_THE_EARTH = register("taming_the_earth", TamingTheEarth::new);
        TAMING_THE_TIME = register("taming_the_time", TamingTheTime::new);

        PARANOIA = register("paranoia", Paranoia::new);
        SHADOW_SHIFT = register("shadow_shift", ShadowShift::new);
        IN_THE_SHADOWS = register("in_the_shadows", InTheShadowsPassive::new);

        CONFUSION_POTION = register("confusion_potion", ConfusionPotion::new);
        HARVEST = register("harvest", HarvestBlocks::new);
        BLOCK_SHIELD = register("block_shield", BlockMaelstromPassive::new);

        TELEPORT_PEARL = register("teleport_pearl", TeleportPearl::new);
        TRANSMISSION_BEACON = register("transmission_beacon", TransmissionBeacon::new);
        ENDER_PASSIVE = register("ender_passive", EnderPassive::new);

        SPARK_MOLOTOV = register("spark_molotov", Molotov::new);
        SPARK_FLASH = register("spark_flash", SparkFlash::new);
        FIRE_GUY = register("fire_guy", FireGuyPassive::new);

        SHADOW_PRISM = register("shadow_prism", ShadowPrism::new);
        SHROUDED_STEP = register("shrouded_step", ShroudedStep::new);
        SECRET_SHADOW_WARRIOR_TECHNIQUE = register("secret_shadow_warrior_technique", DarkCoverPassive::new);
        SHADOW_SWITCH = register("shadow_switch", ShadowSwitch::new);
        DARK_COVER = register("dark_cover", DarkCover::new);
        SHADOW_ASSASSIN_CLONE = register("shadow_assassin_clone", ShadowAssassinClone::new);
        SHADOW_ENERGY = register("shadow_energy", ShadowEnergyPassive::new);

        AARD = register("aard", Aard::new);
        IGNY = register("igny", Igny::new);
        KVEN = register("kven", Kven::new);
        AKCIY = register("akciy", Akciy::new);
        IRDEN = register("irden", Irden::new);
        COMBO_SYSTEM = register("combo_system", ComboPassive::new);

        VORTEX_SLASH = register("vortex_slash", VortexSlash::new);
        VORTEX_STAR = register("vortex_star", VortexStarTalent::new);
        STAR_ALIGNER = register("star_aligner", StarAligner::new);
        LIKE_A_DREAM = register("like_a_dream", LikeADreamPassive::new);
        EYES_OF_THE_GALAXY = register("eyes_of_the_galaxy", EyesOfTheGalaxyPassive::new);

        ICICLES = register("icicles", Icicles::new);
        ICE_CAGE = register("ice_cage", IceCageTalent::new);
        ICY_SHARDS = register("icy_shards", IcyShardsPassive::new);
        CHILL_AURA = register("chill_aura", ChillAuraPassive::new);

        BLINDING_CURSE = register("blinding_curse", BlindingCurse::new);
        SLOWING_AURA = register("slowing_aura", SlowingAura::new);
        HEALING_AURA = register("healing_aura", HealingAura::new);
        SHADOW_CLONE = register("shadow_clone", ShadowClone::new);
        DARK_MAGE_PASSIVE = register("dark_mage_passive", WitherRosePassive::new);

        STONE_CASTLE = register("stone_castle", StoneCastle::new);
        DISCHARGE = register("discharge", Discharge::new);
        SPEAR = register("spear", Spear::new);
        SLOWNESS_POTION = register("slowness_potion", SlownessPotion::new);
        SHIELDED = register("shielded", QuantumEnergyPassive::new);

        NINJA_DASH = register("ninja_dash", NinjaDash::new);
        NINJA_SMOKE = register("ninja_smoke", NinjaSmoke::new);
        FLEET_FOOT = register("fleet_foot", NinjaStylePassive::new);

        FATAL_REAP = register("fatal_reap", FatalReap::new);
        DEATH_SWAP = register("death_swap", DeathSwap::new);
        SHADOWFALL = register("shadowfall", Shadowfall::new);
        SPIRITUAL_BONES = register("spiritual_bones", SpiritualBonesPassive::new);

        ARROW_SHIELD = register("arrow_shield", ArrowShieldTalent::new);
        CLIMB = register("climb", Climb::new);
        TRICKS_OF_THE_JUNGLE = register("tricks_of_the_jungle", TricksOfTheJungle::new);
        POISON_ZONE = register("poison_zone", PoisonZone::new);
        JUJU_PASSIVE = register("juju_passive", ClimbPassive::new);

        BLAST_PACK = register("blast_pack", BlastPack::new);
        SWOOPER_SMOKE_BOMB = register("swooper_smoke_bomb", SmokeBomb::new);
        BLINK = register("blink", Blink::new);
        SWOOPER_PASSIVE = register("swooper_passive", SwooperPassive::new);

        SUBMERGE = register("submerge", SubmergeTalent::new);
        WHIRLPOOL = register("whirlpool", Whirlpool::new);
        SHARK_PASSIVE = register("shark_passive", SharkPassive::new);

        BLACK_HOLE = register("black_hole", BlackHole::new);
        ENTITY_DARKNESS = register("entity_darkness", EntityDarkness::new);
        LIBRARIAN_SHIELD = register("librarian_shield", LibrarianShield::new);
        WEAPON_DARKNESS = register("weapon_darkness", WeaponDarkness::new);

        STANCE = register("stance", MeleeStance::new);
        TIDAL_WAVE = register("tidal_wave", TidalWaveTalent::new);
        RIPTIDE = register("riptide", RiptidePassive::new);

        SABOTEUR = register("saboteur", Saboteur::new);
        CIPHER_LOCK = register("cipher_lock", CipherLock::new);
        TRAP_CAGE = register("trap_cage", TrapCage::new);
        TRAP_WIRE = register("trap_wire", TrapWire::new);
        NEURAL_THEFT = register("neural_theft", NeuralTheftPassive::new);

        LASER_EYE = register("laser_eye", LaserEye::new);

        TOTEM = register("totem", TotemTalent::new);
        TOTEM_IMPRISONMENT = register("totem_imprisonment", TotemImprisonment::new);
        SHAMAN_MARK = register("shaman_mark", ShamanMarkTalent::new);
        OVERHEAL = register("overheal", OverhealPassive::new);
        SLIMY_GUNK = register("slimy_gunk", SlimeGunkTalent::new);
        TOTEM_LINK = register("totem_link", ArcaneLinkPassive::new);

        HEALING_ORB = register("healing_orb", HealingOrb::new);
        REVIVE_TOTEM = register("revive_totem", ReviveTotem::new);
        REVIVE = register("revive", RevivePassive::new);

        VAMPIRE_PET = register("vampire_pet", VampirePet::new);
        BLOODSHIFT = register("bloodshift", Bloodshift::new);
        BLOOD_DEBT = register("blood_debt", BloodDebtTalent::new);
        BAT_SWARM = register("bat_swarm", BatSwarm::new);
        BAT_TRANSFER = register("bat_transfer", BatTransferTalent::new);
        VAMPIRE_PASSIVE = register("vampire_passive", VampirePassive::new);

        SHORTY = register("shorty", ShortyShotgun::new);
        GRAPPLE = register("grapple", GrappleHookTalent::new);
        SMOKE_BOMB = register("smoke_bomb", SmokeBombTalent::new);
        BOUNTY_HUNTER_PASSIVE = register("bounty_hunter_passive", BountyHunterPassive::new);
        
        UPPERCUT = register("uppercut", Uppercut::new);
        UPDRAFT = register("updraft", Updraft::new);
        SLASH = register("slash", Slash::new);
        SWORD_MASTER_PASSIVE = register("sword_master_passive", SwordMasterPassive::new);

        // Orc talents
        ORC_GROWN = register("orc_grown", OrcGrowl::new);
        ORC_AXE = register("orc_axe", OrcAxe::new);
        ORC_PASSIVE = register("orc_passive", DontAngerMePassive::new);

        // Engineer talents
        ENGINEER_SENTRY = register("engineer_sentry", EngineerSpotter::new);
        ENGINEER_TURRET = register("engineer_turret", EngineerSentry::new);
        ENGINEER_DISPENSER = register("engineer_dispenser", EngineerDispenser::new);
        ENGINEER_RECALL = register("engineer_recall", EngineerRecall::new);
        ENGINEER_PASSIVE = register("engineer_passive", MagneticAttractionPassive::new);

        TWIN_CLAWS = register("twin_claws", TwinClaws::new);
        CANDLEBANE = register("candlebane", CandlebaneTalent::new);
        BLOOD_CHALICE = register("blood_chalice", BloodChaliceTalent::new);
        SPECTRAL_FORM = register("spectral_form", SpectralForm::new);
        BLOOD_CUP = register("blood_cup", BloodCup::new);
        SUCCULENCE = register("succulence", BloodfiendPassive::new);

        BROKEN_HEART_RADIATION = register("broken_heart_radiation", BrokenHeartRadiation::new);
        MALEVOLENT_HITSHIELD = register("malevolent_hitshield", MalevolentHitshieldTalent::new);
        FEROCIOUS_STRIKES = register("ferocious_strikes", FerociousStrikes::new);
        MALEDICTION_VEIL = register("malediction_veil", MaledictionVeil::new);

        EXTRA_CUT = register("extra_cut", ExtraCut::new);
        SWAYBLADE = register("swayblade", Swayblade::new);
        SECOND_WIND = register("second_wind", SecondWind::new);

        CELESTE_ARROW = register("celeste_arrow", CelesteArrow::new);
        ETHEREAL_ARROW = register("ethereal_arrow", EtherealArrow::new);
        GUARDIAN_ANGEL = register("guardian_angel", GuardianAngel::new);

        WITHER_ROSE_PATH = register("wither_rose_path", WitherRosePath::new);
        CHAOS_GROUND = register("chaos_ground", ChaosGround::new);
        NYX_PASSIVE = register("nyx_passive", NyxPassive::new);

        ECHO_TRAP = register("echo", EchoTrapTalent::new);
        ECHO_WORLD = register("echo_world", EchoWorldTalent::new);

        MUSIC_BOX = register("music_box", MusicBoxTalent::new);
        TAKE_A_CAKE_TO_THE_FACE = register("take_a_cake_to_the_face", TakeACakeToTheFace::new);

        CHARGE_ATTACK = register("charge_attack", ChargeAttack::new);
        RONIN_DASH = register("ronin_dash", RoninDash::new);

        LUCKY_DAY = register("lucky_day", LuckyDay::new);
        DEAD_EYE = register("dead_eye", DeadEye::new);
        SPIKE_BARRIER = register("spike_barrier", SpikeBarrier::new);

        FRACTURE = register("fracture", Fracture::new);
        DISUNION = register("disunion", Disunion::new);
        PARADOX = register("paradox", Paradox::new);

        FIRE_PIT = register("fire_pit", FirePitTalent::new);
        DEMON_SPLIT_QUAZII = register("demon_split_quazii", DemonSplitTalentQuazii::new);
        DEMON_SPLIT_TYPHOEUS = register("demon_split_typhoeus", DemonSplitTalentTyphoeus::new);
        DEMON_KIND = register("demon_kind", DemonKindPassiveTalent::new);
        
        SUMMON_WHELP = register("summon_whelp", SummonWhelp::new);
        HELLFIRE_WARD = register("hellfire_ward", HellfireWard::new);
        BLIGHTWHIRL  = register("blightwhirl", Blightwhirl::new);
        WHELP_ATTACK = register("whelp_attack", WhelpAttack::new);
        
        DYLAN_PASSIVE = register("dylan_passive", DylanPassive::new);
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

    private static <E extends Talent> E register(@Nonnull String key, @Nonnull KeyFunction<E> fn) {
        final E talent = fn.apply(Key.ofString(key));

        values.add(talent);
        return talent;
    }

}
