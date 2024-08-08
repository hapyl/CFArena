package me.hapyl.fight.game.talents;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.fight.CF;
import me.hapyl.fight.exception.HandleNotSetException;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.GamePlayer;
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
import me.hapyl.fight.game.heroes.heavy_knight.SwordMaster;
import me.hapyl.fight.game.heroes.hercules.Hercules;
import me.hapyl.fight.game.heroes.juju.JuJu;
import me.hapyl.fight.game.heroes.knight.BlastKnight;
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
import me.hapyl.fight.game.heroes.vortex.Vortex;
import me.hapyl.fight.game.heroes.witcher.WitcherClass;
import me.hapyl.fight.game.heroes.zealot.Zealot;
import me.hapyl.fight.game.talents.alchemist.CauldronAbility;
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
import me.hapyl.fight.game.talents.dark_mage.BlindingCurse;
import me.hapyl.fight.game.talents.dark_mage.HealingAura;
import me.hapyl.fight.game.talents.dark_mage.ShadowClone;
import me.hapyl.fight.game.talents.dark_mage.SlowingAura;
import me.hapyl.fight.game.talents.doctor.ConfusionPotion;
import me.hapyl.fight.game.talents.doctor.HarvestBlocks;
import me.hapyl.fight.game.talents.echo.EchoTalent;
import me.hapyl.fight.game.talents.ender.EnderPassive;
import me.hapyl.fight.game.talents.ender.TeleportPearl;
import me.hapyl.fight.game.talents.ender.TransmissionBeacon;
import me.hapyl.fight.game.talents.engineer.EngineerRecall;
import me.hapyl.fight.game.talents.engineer.EngineerSentry;
import me.hapyl.fight.game.talents.engineer.EngineerTurret;
import me.hapyl.fight.game.talents.frostbite.IceCageTalent;
import me.hapyl.fight.game.talents.frostbite.Icicles;
import me.hapyl.fight.game.talents.frostbite.IcyShardsPassive;
import me.hapyl.fight.game.talents.harbinger.MeleeStance;
import me.hapyl.fight.game.talents.harbinger.TidalWaveTalent;
import me.hapyl.fight.game.talents.healer.HealingOrb;
import me.hapyl.fight.game.talents.healer.ReviveTotem;
import me.hapyl.fight.game.talents.heavy_knight.Slash;
import me.hapyl.fight.game.talents.heavy_knight.Updraft;
import me.hapyl.fight.game.talents.heavy_knight.Uppercut;
import me.hapyl.fight.game.talents.hercules.HerculesJump;
import me.hapyl.fight.game.talents.hercules.HerculesShift;
import me.hapyl.fight.game.talents.juju.ArrowShield;
import me.hapyl.fight.game.talents.juju.Climb;
import me.hapyl.fight.game.talents.juju.PoisonZone;
import me.hapyl.fight.game.talents.juju.TricksOfTheJungle;
import me.hapyl.fight.game.talents.km.LaserEye;
import me.hapyl.fight.game.talents.knight.Discharge;
import me.hapyl.fight.game.talents.knight.SlownessPotion;
import me.hapyl.fight.game.talents.knight.Spear;
import me.hapyl.fight.game.talents.knight.StoneCastle;
import me.hapyl.fight.game.talents.librarian.BlackHole;
import me.hapyl.fight.game.talents.librarian.EntityDarkness;
import me.hapyl.fight.game.talents.librarian.LibrarianShield;
import me.hapyl.fight.game.talents.librarian.WeaponDarkness;
import me.hapyl.fight.game.talents.mage.ArcaneMute;
import me.hapyl.fight.game.talents.mage.MageTransmission;
import me.hapyl.fight.game.talents.moonwalker.GravityZone;
import me.hapyl.fight.game.talents.moonwalker.MoonPassive;
import me.hapyl.fight.game.talents.moonwalker.MoonPillarTalent;
import me.hapyl.fight.game.talents.moonwalker.MoonSliteBomb;
import me.hapyl.fight.game.talents.nightmare.Paranoia;
import me.hapyl.fight.game.talents.nightmare.ShadowShift;
import me.hapyl.fight.game.talents.ninja.NinjaDash;
import me.hapyl.fight.game.talents.ninja.NinjaSmoke;
import me.hapyl.fight.game.talents.nyx.NyxPassive;
import me.hapyl.fight.game.talents.nyx.WitherImitation;
import me.hapyl.fight.game.talents.orc.OrcAxe;
import me.hapyl.fight.game.talents.orc.OrcGrowl;
import me.hapyl.fight.game.talents.pytaria.FlowerBreeze;
import me.hapyl.fight.game.talents.pytaria.FlowerEscape;
import me.hapyl.fight.game.talents.rogue.ExtraCut;
import me.hapyl.fight.game.talents.rogue.SecondWind;
import me.hapyl.fight.game.talents.rogue.Swayblade;
import me.hapyl.fight.game.talents.shadow_assassin.*;
import me.hapyl.fight.game.talents.shaman.ShamanMarkTalent;
import me.hapyl.fight.game.talents.shaman.SlimeGunkTalent;
import me.hapyl.fight.game.talents.shaman.TotemImprisonment;
import me.hapyl.fight.game.talents.shaman.TotemTalent;
import me.hapyl.fight.game.talents.shark.SharkPassive;
import me.hapyl.fight.game.talents.shark.SubmergeTalent;
import me.hapyl.fight.game.talents.shark.Whirlpool;
import me.hapyl.fight.game.talents.spark.Molotov;
import me.hapyl.fight.game.talents.spark.SparkFlash;
import me.hapyl.fight.game.talents.sun.SyntheticSun;
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
import me.hapyl.fight.game.talents.troll.Repulsor;
import me.hapyl.fight.game.talents.troll.TrollSpin;
import me.hapyl.fight.game.talents.vampire.BatSwarm;
import me.hapyl.fight.game.talents.vampire.VampirePet;
import me.hapyl.fight.game.talents.vortex.StarAligner;
import me.hapyl.fight.game.talents.vortex.VortexSlash;
import me.hapyl.fight.game.talents.vortex.VortexStarTalent;
import me.hapyl.fight.game.talents.witcher.*;
import me.hapyl.fight.game.talents.zealot.BrokenHeartRadiation;
import me.hapyl.fight.game.talents.zealot.FerociousStrikes;
import me.hapyl.fight.game.talents.zealot.MaledictionVeil;
import me.hapyl.fight.game.talents.zealot.MalevolentHitshield;
import me.hapyl.eterna.module.util.BFormat;
import me.hapyl.eterna.module.util.Compute;
import org.bukkit.Material;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
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

    /**
     * {@link Archer}
     */
    TRIPLE_SHOT(new TripleShot()),
    SHOCK_DARK(new ShockDark()),
    HAWKEYE_ARROW(new HawkeyePassive()),

    /**
     * {@link Alchemist}
     */
    POTION(new RandomPotion()),
    CAULDRON(new CauldronAbility()),
    INTOXICATION(new PassiveTalent(
            "Intoxication", """
            Drinking potions will increase &eIntoxication &7level that will decrease constantly.
                        
            Having high &eIntoxication&7 levels isn't good for your body!
            """,
            Material.DRAGON_BREATH,
            TalentType.ENHANCE
    )),

    /**
     * {@link Moonwalker}
     */
    MOONSLITE_PILLAR(new MoonPillarTalent()),
    @Deprecated MOONSLITE_BOMB(new MoonSliteBomb()),
    MOON_GRAVITY(new GravityZone()),
    MOON_PASSIVE(new MoonPassive()),

    /**
     * {@link Hercules}
     */
    HERCULES_DASH(new HerculesShift()),
    HERCULES_UPDRAFT(new HerculesJump()),
    PLUNGE(new PassiveTalent(
            "Plunge",
            "While airborne, &e&lSNEAK &7to perform plunging attack, dealing damage to nearby enemies.",
            Material.COARSE_DIRT,
            TalentType.ENHANCE
    )),

    /**
     * {@link Mage}
     */
    MAGE_TRANSMISSION(new MageTransmission()),
    ARCANE_MUTE(new ArcaneMute()),
    SOUL_HARVEST(new PassiveTalent(
            "Soul Harvest",
            "Deal &bmelee &7damage to gain soul fragment as fuel for your &e&lSoul &e&lEater&7's range attacks.",
            Material.SKELETON_SPAWN_EGG,
            TalentType.IMPAIR
    )),

    /**
     * {@link Pytaria}
     */
    FLOWER_ESCAPE(new FlowerEscape()),
    FLOWER_BREEZE(new FlowerBreeze()),
    EXCELLENCY(new PassiveTalent(
            "Excellency",
            "The less &chealth&7 Pytaria has, the more her %s and %s increases. But her %s significantly decreases.".formatted(
                    AttributeType.ATTACK,
                    AttributeType.CRIT_CHANCE,
                    AttributeType.DEFENSE
            ), Material.ROSE_BUSH,
            TalentType.ENHANCE
    )),

    /**
     * {@link Troll}
     */
    TROLL_SPIN(new TrollSpin()),
    REPULSOR(new Repulsor()),
    TROLL_PASSIVE(new PassiveTalent(
            "Last Laugh",
            "Your hits have &b0.1% &7chance to instantly kill enemy.",
            Material.BLAZE_POWDER,
            TalentType.ENHANCE
    )),

    /**
     * {@link Tamer}
     */
    MINE_O_BALL(new MineOBall()),
    TAMING_THE_WIND(new TamingTheWind()),
    TAMING_THE_EARTH(new TamingTheEarth()),
    TAMING_THE_TIME(new TamingTheTime()),

    /**
     * {@link Nightmare}
     */
    PARANOIA(new Paranoia()),
    SHADOW_SHIFT(new ShadowShift()),
    IN_THE_SHADOWS(new PassiveTalent(
            "In the Shadows",
            "While in moody light, your %s&7 and %s&7 increases.".formatted(AttributeType.ATTACK, AttributeType.SPEED),
            Material.DRIED_KELP,
            TalentType.ENHANCE
    )),

    /**
     * {@link DrEd}
     */
    CONFUSION_POTION(new ConfusionPotion()),
    HARVEST(new HarvestBlocks()),
    BLOCK_SHIELD(new PassiveTalent(
            "Block Maelstrom",
            "Creates a block that orbits around you, dealing damage based on the element upon contact with opponents.____&7Refreshes every &b10s&7.",
            Material.BRICK,
            TalentType.DEFENSE
    )),

    /**
     * {@link Ender}
     */
    TELEPORT_PEARL(new TeleportPearl()),
    TRANSMISSION_BEACON(new TransmissionBeacon()),
    ENDER_PASSIVE(new EnderPassive()),

    /**
     * {@link Spark}
     */
    SPARK_MOLOTOV(new Molotov()),
    SPARK_FLASH(new SparkFlash()),
    FIRE_GUY(new PassiveTalent("Fire Guy", "You're completely immune to &clava &7and &cfire &7damage.", Material.LAVA_BUCKET)),

    /**
     * {@link ShadowAssassin}
     */
    @Deprecated SHADOW_PRISM(new ShadowPrism()),
    @Deprecated SHROUDED_STEP(new ShroudedStep()),
    @Deprecated SECRET_SHADOW_WARRIOR_TECHNIQUE(new PassiveTalent(
            "Dark Cover",
            "As an assassin, you have mastered the ability to stay in the shadows.____While &e&lSNEAKING&7, you become completely invisible, but cannot deal damage and your footsteps are visible.",
            Material.NETHERITE_CHESTPLATE
    )),

    SHADOW_SWITCH(new ShadowSwitch()),
    DARK_COVER(new DarkCover()),
    SHADOW_ASSASSIN_CLONE(new ShadowAssassinClone()),
    SHADOW_ENERGY(new PassiveTalent(
            "Shadow Energy", """
            Accumulate %1$s while using talents in &9Stealth&7 mode.
                        
            Spend %1$s to use empowered talents in &cFury&7 mode.
            """.formatted(Named.SHADOW_ENERGY),
            Material.CHORUS_FRUIT,
            TalentType.ENHANCE
    )),

    /**
     * {@link WitcherClass}
     */
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

    /**
     * {@link Vortex}
     */
    VORTEX_SLASH(new VortexSlash()),
    VORTEX_STAR(new VortexStarTalent()),
    STAR_ALIGNER(new StarAligner()),
    LIKE_A_DREAM(new PassiveTalent(
            "Like a Dream", """
            &6Linking&7 to an &eAstral Star&7 grants you one stack of %1$s&7.
                        
            Each %1$s&7 stack increases your &6astral&7 damage by &b15%%&7.
                        
            &8;;Lose one stack after not gaining a stack for 5s.
            """.formatted(Named.ASTRAL_SPARK),
            Material.RED_BED
    )),

    @Deprecated EYES_OF_THE_GALAXY(new PassiveTalent(
            "Eyes of the Galaxy",
            "Astral Stars you place will glow different colors:____&eYellow &7indicates a placed star.____&bAqua &7indicates closest star that will be consumed upon teleport.____&aGreen &7indicates star you will blink to upon teleport.",
            Material.ENDER_EYE
    )),

    /**
     * {@link Freazly}
     */
    ICICLES(new Icicles()),
    ICE_CAGE(new IceCageTalent()),
    ICY_SHARDS(new IcyShardsPassive()),
    CHILL_AURA(new PassiveTalent("Chill Aura", """
            You emmit a &bchill aura&7, that &bslows&7 and decreases enemies %s in small AoE.
            """.formatted(AttributeType.ATTACK_SPEED), Material.LIGHT_BLUE_DYE)),

    /**
     * {@link DarkMage}
     */
    BLINDING_CURSE(new BlindingCurse()),
    SLOWING_AURA(new SlowingAura()),
    HEALING_AURA(new HealingAura()),
    SHADOW_CLONE(new ShadowClone()),
    DARK_MAGE_PASSIVE(new PassiveTalent(
            "Wither Rose", """
            Dealing &4damage&7 plants a %1$s&7 into the &cenemy&7.
                        
            &nEach&7 stack of %1$s increases the &nduration&7 of your ultimate.
            """.formatted(Named.WITHER_ROSE), Material.WITHER_ROSE)
    ),

    /**
     * {@link BlastKnight}
     */
    STONE_CASTLE(new StoneCastle()),
    DISCHARGE(new Discharge()),
    @Deprecated SPEAR(new Spear()),
    @Deprecated SLOWNESS_POTION(new SlownessPotion()),
    SHIELDED(new PassiveTalent(
            "Quantum Energy", """
            &8Blocking&7 damage with your &bshield&7 will accumulate &dQuantum Energy&7.
                        
            Using &a%s&7 can manipulate the energy to create &fNova Explosion&7.
            """.formatted(DISCHARGE.talent.getName()),
            Material.SHIELD
    )),

    /**
     * {@link Ninja}
     */
    NINJA_DASH(new NinjaDash()),
    NINJA_SMOKE(new NinjaSmoke()),
    FLEET_FOOT(new PassiveTalent(
            "Ninja Style", """
            Ninjas are fast and agile.
                        
            You gain %s &7boost, can &bdouble jump&7 and don't take &3fall&7 damage!
            """.formatted(AttributeType.SPEED), Material.ELYTRA
    )),

    /**
     * {@link Taker}
     */
    FATAL_REAP(new FatalReap()),
    DEATH_SWAP(new DeathSwap()),
    SPIRITUAL_BONES(new SpiritualBonesPassive()),

    /**
     * {@link JuJu}
     */
    ARROW_SHIELD(new ArrowShield()),
    @Deprecated CLIMB(new Climb()),
    TRICKS_OF_THE_JUNGLE(new TricksOfTheJungle()),
    POISON_ZONE(new PoisonZone()),
    JUJU_PASSIVE(new PassiveTalent(
            "Climb", """
            Raised by the &ajungle&7, Juju mastered the ability to &2climb&7 &nanything&7.

            &6Jump&7 on a wall to grab onto it and &bdesccent&7 slowly.
            &6Sneak&7 to climb upwards.
            &6Double Jump&7 to dash backwards.
            """, Material.LEATHER_BOOTS
    )),

    /**
     * {@link Swooper}
     */
    BLAST_PACK(new BlastPack()),
    SWOOPER_SMOKE_BOMB(new SmokeBomb()),
    @Deprecated BLINK(new Blink()),
    SWOOPER_PASSIVE(new SwooperPassive()),

    /**
     * {@link Shark}
     */
    SUBMERGE(new SubmergeTalent()),
    WHIRLPOOL(new Whirlpool()),
    SHARK_PASSIVE(new SharkPassive()),

    // Librarian
    BLACK_HOLE(new BlackHole()),
    ENTITY_DARKNESS(new EntityDarkness()),
    LIBRARIAN_SHIELD(new LibrarianShield()),
    WEAPON_DARKNESS(new WeaponDarkness()),

    /**
     * {@link Harbinger}
     */
    STANCE(new MeleeStance()),
    TIDAL_WAVE(new TidalWaveTalent()),
    RIPTIDE(new PassiveTalent(
            "Riptide", """
            &nFully&7 &ncharged&7 shots in %1$s or &bcritical&7 hits in %2$s apply the %3$s effect to enemies.
                        
            Hitting opponents affected by %3$s in the aforementioned ways will trigger &bRiptide Slash&7, which rapidly deals damage.
            """.formatted(Named.STANCE_RANGE, Named.STANCE_MELEE, Named.RIPTIDE), Material.HEART_OF_THE_SEA
    )),

    /**
     * {@link Techie}
     */
    SABOTEUR(new Saboteur()),
    CIPHER_LOCK(new CipherLock()),

    @Deprecated TRAP_CAGE(new TrapCage()),
    @Deprecated TRAP_WIRE(new TrapWire()),
    NEURAL_THEFT(new PassiveTalent(
            "Neural Theft", """
            At &bintervals&7, &bhack&7 all &fbugged&7 opponents and send the data to &nyou&7 and your &nteammates&7.
                        
            &oThe data includes:
            └ Enemy's &blocation&7.
            └ Enemy's &c❤ Health&7.
            └ Enemy's %1$s.
                        
            Also, &4steal&7 a small amount of %1$s from each hacked enemy.
            """.formatted(Named.ENERGY), Material.CHAINMAIL_HELMET, TalentType.IMPAIR
    )),

    // Killing Machine
    LASER_EYE(new LaserEye()),
    //GRENADE(new ShellGrande()),

    /**
     * {@link Shaman}
     */
    TOTEM(new TotemTalent()),
    TOTEM_IMPRISONMENT(new TotemImprisonment()),
    SHAMAN_MARK(new ShamanMarkTalent()),
    OVERHEAL(new PassiveTalent(Named.OVERHEAL.getName(), """
            When &ahealing&7 an &a&nally&7 who is already at &c&nfull&7 &c&nhealth&7, the excess &ahealing&7 is converted into %1$s.
                        
            When &nyou&7 or &nyour&7 allies deal &cdamage&7, it's increased by your %1$s.
            &8;;The Overheal is consumed with the damage.
            """.formatted(Named.OVERHEAL), Material.GLISTERING_MELON_SLICE)),

    SLIMY_GUNK(new SlimeGunkTalent()),

    @Deprecated TOTEM_LINK(new PassiveTalent("Arcane Linkage", """
            Your &atotems&7 are linked by an invisible chain.
                        
            &cEnemies&7 passing through a chain will take &cdamage&7.
            """,
            Material.CHAIN
    )),

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
                        
            &6;;Healing, damage boost, duration and cooldown are based on the number of stacks consumed.
            """,
            Material.REDSTONE
    )),

    /**
     * {@link BountyHunter}
     */
    SHORTY(new ShortyShotgun()),
    GRAPPLE(new GrappleHookTalent()),
    SMOKE_BOMB(new PassiveTalent("Smoke Bomb", """
            Whenever your &chealth&7 falls &nbelow&7 &c50%&7, you gain a &aSmoke Bomb&7.
                        
            Throw it to create a &8smoke field&7 that &3blinds&7 everyone inside it and grant you a &bspeed boost&7.
            """, Material.ENDERMAN_SPAWN_EGG
    )),

    /**
     * {@link SwordMaster}
     */
    UPPERCUT(new Uppercut()),
    UPDRAFT(new Updraft()),
    SLASH(new Slash()),
    SWORD_MASTER_PASSIVE(
            new PassiveTalent("Perfect Sequence", """
                    Using %1$s ❱ %2$s ❱ %3$s in quick &2&nsuccession&7 &cempowers&7 and &nresets&7 the cooldown of %3$s.
                    """.formatted(UPPERCUT, UPDRAFT, SLASH), Material.CLOCK)
                    .setCooldownSec(5)
    ),

    /**
     * {@link Orc}
     */
    ORC_GROWN(new OrcGrowl()),
    ORC_AXE(new OrcAxe()),
    ORC_PASSIVE(new PassiveTalent("Don't Anger Me", """
            Taking &ncontinuous&7 &cdamage&7 within the set time window will trigger %s for &b3s&7.
            """.formatted(Named.BERSERK), Material.FERMENTED_SPIDER_EYE)),

    /**
     * {@link Engineer}
     */
    ENGINEER_SENTRY(new EngineerSentry()),
    ENGINEER_TURRET(new EngineerTurret()),
    ENGINEER_RECALL(new EngineerRecall()),
    ENGINEER_PASSIVE(new PassiveTalent("Magnetic Attraction", """
            Every few seconds you'll receive an Iron Ingot.
            Use it to build stuff!
            """, Material.IRON_INGOT)),

    /**
     * {@link Bloodfiend}
     */
    TWIN_CLAWS(new TwinClaws()),
    CANDLEBANE(new CandlebaneTalent()),
    BLOOD_CHALICE(new BloodChaliceTalent()),
    BLOOD_CUP(new BloodCup()),
    SUCCULENCE(new BloodfiendPassive()),

    /**
     * {@link Zealot}
     */
    BROKEN_HEART_RADIATION(new BrokenHeartRadiation()),
    MALEVOLENT_HITSHIELD(new MalevolentHitshield()),
    FEROCIOUS_STRIKES(new FerociousStrikes()),
    @Deprecated MALEDICTION_VEIL(new MaledictionVeil()),

    /**
     * {@link Rogue}
     */
    EXTRA_CUT(new ExtraCut()),
    SWAYBLADE(new Swayblade()),
    SECOND_WIND(new SecondWind()),

    /**
     * {@link Aurora}
     */
    CELESTE_ARROW(new CelesteArrow()),
    ETHEREAL_ARROW(new EtherealArrow()),
    DIVINE_INTERVENTION(new DivineIntervention()),

    /**
     * {@link Nyx}
     */
    WITHER_IMITATION(new WitherImitation()),
    NYX_PASSIVE(new NyxPassive()),

    /**
     * {@link Echo}
     */
    ECHO(new EchoTalent()),

    // ???,
    SYNTHETIC_SUN(new SyntheticSun()),

    ;

    private final static Map<TalentType, List<Talents>> BY_TYPE;

    static {
        BY_TYPE = Maps.newHashMap();

        for (Talents enumTalent : values()) {
            final Talent talent = enumTalent.talent;

            if (talent == null) {
                continue;
            }

            BY_TYPE.compute(talent.getType(), Compute.listAdd(enumTalent));
        }
    }

    private final Talent talent;

    Talents(Talent talent) {
        if (talent instanceof UltimateTalent) {
            throw new IllegalArgumentException("ultimate talent enum initiation");
        }

        this.talent = talent;
        this.talent.setHandle(this);

        if (talent instanceof Listener listener) {
            CF.registerEvents(listener);
        }
    }

    public void startCd(GamePlayer player) {
        getTalent().startCd(player);
    }

    @Nonnull
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
        if (talent == null) {
            throw new HandleNotSetException(this);
        }

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

    @Override
    public String toString() {
        return Color.GREEN + getName() + Color.GRAY;
    }

    @Nonnull
    public static List<Talents> byType(@Nonnull TalentType type) {
        return BY_TYPE.getOrDefault(type, Lists.newArrayList());
    }

    private static String format(String textBlock, @Nullable Object... format) {
        if (format == null || format.length == 0) {
            return textBlock;
        }

        return BFormat.format(textBlock, format);
    }
}
