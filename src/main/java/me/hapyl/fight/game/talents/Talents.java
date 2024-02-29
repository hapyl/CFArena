package me.hapyl.fight.game.talents;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.fight.CF;
import me.hapyl.fight.exception.HandleNotSetException;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.alchemist.CauldronAbility;
import me.hapyl.fight.game.talents.archive.alchemist.RandomPotion;
import me.hapyl.fight.game.talents.archive.archer.ShockDark;
import me.hapyl.fight.game.talents.archive.archer.TripleShot;
import me.hapyl.fight.game.talents.archive.bloodfiend.BloodCup;
import me.hapyl.fight.game.talents.archive.bloodfiend.BloodfiendPassive;
import me.hapyl.fight.game.talents.archive.bloodfiend.TwinClaws;
import me.hapyl.fight.game.talents.archive.bloodfiend.candlebane.CandlebaneTalent;
import me.hapyl.fight.game.talents.archive.bloodfiend.chalice.BloodChaliceTalent;
import me.hapyl.fight.game.talents.archive.bounty_hunter.GrappleHookTalent;
import me.hapyl.fight.game.talents.archive.bounty_hunter.ShortyShotgun;
import me.hapyl.fight.game.talents.archive.dark_mage.BlindingCurse;
import me.hapyl.fight.game.talents.archive.dark_mage.HealingAura;
import me.hapyl.fight.game.talents.archive.dark_mage.ShadowClone;
import me.hapyl.fight.game.talents.archive.dark_mage.SlowingAura;
import me.hapyl.fight.game.talents.archive.doctor.ConfusionPotion;
import me.hapyl.fight.game.talents.archive.doctor.HarvestBlocks;
import me.hapyl.fight.game.talents.archive.ender.EnderPassive;
import me.hapyl.fight.game.talents.archive.ender.TeleportPearl;
import me.hapyl.fight.game.talents.archive.ender.TransmissionBeacon;
import me.hapyl.fight.game.talents.archive.engineer.EngineerRecall;
import me.hapyl.fight.game.talents.archive.engineer.EngineerSentry;
import me.hapyl.fight.game.talents.archive.engineer.EngineerTurret;
import me.hapyl.fight.game.talents.archive.frostbite.IceBarrier;
import me.hapyl.fight.game.talents.archive.frostbite.IceCageTalent;
import me.hapyl.fight.game.talents.archive.frostbite.Icicles;
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
import me.hapyl.fight.game.talents.archive.juju.PoisonZone;
import me.hapyl.fight.game.talents.archive.juju.TricksOfTheJungle;
import me.hapyl.fight.game.talents.archive.km.LaserEye;
import me.hapyl.fight.game.talents.archive.knight.Discharge;
import me.hapyl.fight.game.talents.archive.knight.SlownessPotion;
import me.hapyl.fight.game.talents.archive.knight.Spear;
import me.hapyl.fight.game.talents.archive.knight.StoneCastle;
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
import me.hapyl.fight.game.talents.archive.rogue.ExtraCut;
import me.hapyl.fight.game.talents.archive.rogue.SecondWind;
import me.hapyl.fight.game.talents.archive.rogue.Swayblade;
import me.hapyl.fight.game.talents.archive.shadow_assassin.*;
import me.hapyl.fight.game.talents.archive.shaman.ShamanMarkTalent;
import me.hapyl.fight.game.talents.archive.shaman.SlimeGunkTalent;
import me.hapyl.fight.game.talents.archive.shaman.TotemImprisonment;
import me.hapyl.fight.game.talents.archive.shaman.TotemTalent;
import me.hapyl.fight.game.talents.archive.shark.SubmergeTalent;
import me.hapyl.fight.game.talents.archive.shark.Whirlpool;
import me.hapyl.fight.game.talents.archive.spark.Molotov;
import me.hapyl.fight.game.talents.archive.spark.SparkFlash;
import me.hapyl.fight.game.talents.archive.sun.SyntheticSun;
import me.hapyl.fight.game.talents.archive.swooper.BlastPack;
import me.hapyl.fight.game.talents.archive.swooper.Blink;
import me.hapyl.fight.game.talents.archive.swooper.SmokeBomb;
import me.hapyl.fight.game.talents.archive.swooper.SwooperPassive;
import me.hapyl.fight.game.talents.archive.taker.DeathSwap;
import me.hapyl.fight.game.talents.archive.taker.FatalReap;
import me.hapyl.fight.game.talents.archive.taker.SpiritualBonesPassive;
import me.hapyl.fight.game.talents.archive.tamer.MineOBall;
import me.hapyl.fight.game.talents.archive.tamer.TamingTheEarth;
import me.hapyl.fight.game.talents.archive.tamer.TamingTheTime;
import me.hapyl.fight.game.talents.archive.tamer.TamingTheWind;
import me.hapyl.fight.game.talents.archive.techie.*;
import me.hapyl.fight.game.talents.archive.troll.Repulsor;
import me.hapyl.fight.game.talents.archive.troll.TrollSpin;
import me.hapyl.fight.game.talents.archive.vampire.BatSwarm;
import me.hapyl.fight.game.talents.archive.vampire.VampirePet;
import me.hapyl.fight.game.talents.archive.vortex.StarAligner;
import me.hapyl.fight.game.talents.archive.vortex.VortexSlash;
import me.hapyl.fight.game.talents.archive.vortex.VortexStarTalent;
import me.hapyl.fight.game.talents.archive.witcher.*;
import me.hapyl.fight.game.talents.archive.zealot.BrokenHeartRadiation;
import me.hapyl.fight.game.talents.archive.zealot.FerociousStrikes;
import me.hapyl.fight.game.talents.archive.zealot.MaledictionVeil;
import me.hapyl.fight.game.talents.archive.zealot.MalevolentHitshield;
import me.hapyl.spigotutils.module.util.BFormat;
import me.hapyl.spigotutils.module.util.Compute;
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
     * {@link me.hapyl.fight.game.heroes.archive.archer.Archer}
     */
    TRIPLE_SHOT(new TripleShot()),
    SHOCK_DARK(new ShockDark()),
    HAWKEYE_ARROW(new PassiveTalent(
            "Hawkeye Arrow",
            "Fully charged shots while sneaking have &b25%&7 chance to fire a hawkeye arrow that homes to nearby enemies.",
            Material.ENDER_EYE,
            Talent.Type.DAMAGE
    )),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.alchemist.Alchemist}
     */
    POTION(new RandomPotion()),
    CAULDRON(new CauldronAbility()),
    INTOXICATION(new PassiveTalent(
            "Intoxication", """
            Drinking potions will increase &eIntoxication &7level that will decrease constantly.
                        
            Having high &eIntoxication&7 levels isn't good for your body!
            """,
            Material.DRAGON_BREATH,
            Talent.Type.ENHANCE
    )),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.moonwalker.Moonwalker}
     */
    MOONSLITE_PILLAR(new MoonPillarTalent()),
    @Deprecated MOONSLITE_BOMB(new MoonSliteBomb()),
    MOON_GRAVITY(new GravityZone()),
    TARGET(new PassiveTalent("Space Suit", "Your suit grants you slow falling ability.", Material.FEATHER, Talent.Type.ENHANCE)),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.hercules.Hercules}
     */
    HERCULES_DASH(new HerculesShift()),
    HERCULES_UPDRAFT(new HerculesJump()),
    PLUNGE(new PassiveTalent(
            "Plunge",
            "While airborne, &e&lSNEAK &7to perform plunging attack, dealing damage to nearby enemies.",
            Material.COARSE_DIRT,
            Talent.Type.ENHANCE
    )),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.mage.Mage}
     */
    MAGE_TRANSMISSION(new MageTransmission()),
    ARCANE_MUTE(new ArcaneMute()),
    SOUL_HARVEST(new PassiveTalent(
            "Soul Harvest",
            "Deal &bmelee &7damage to gain soul fragment as fuel for your &e&lSoul &e&lEater&7's range attacks.",
            Material.SKELETON_SPAWN_EGG,
            Talent.Type.IMPAIR
    )),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.pytaria.Pytaria}
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
            Talent.Type.ENHANCE
    )),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.troll.Troll}
     */
    TROLL_SPIN(new TrollSpin()),
    REPULSOR(new Repulsor()),
    TROLL_PASSIVE(new PassiveTalent(
            "Last Laugh",
            "Your hits have &b0.1% &7chance to instantly kill enemy.",
            Material.BLAZE_POWDER,
            Talent.Type.ENHANCE
    )),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.tamer.Tamer}
     */
    MINE_O_BALL(new MineOBall()),
    TAMING_THE_WIND(new TamingTheWind()),
    TAMING_THE_EARTH(new TamingTheEarth()),
    TAMING_THE_TIME(new TamingTheTime()),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.nightmare.Nightmare}
     */
    PARANOIA(new Paranoia()),
    SHADOW_SHIFT(new ShadowShift()),
    IN_THE_SHADOWS(new PassiveTalent(
            "In the Shadows",
            "While in moody light, your %s&7 and %s&7 increases.".formatted(AttributeType.ATTACK, AttributeType.SPEED),
            Material.DRIED_KELP,
            Talent.Type.ENHANCE
    )),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.doctor.DrEd}
     */
    CONFUSION_POTION(new ConfusionPotion()),
    HARVEST(new HarvestBlocks()),
    BLOCK_SHIELD(new PassiveTalent(
            "Block Maelstrom",
            "Creates a block that orbits around you, dealing damage based on the element upon contact with opponents.____&7Refreshes every &b10s&7.",
            Material.BRICK,
            Talent.Type.DEFENSE
    )),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.ender.Ender}
     */
    TELEPORT_PEARL(new TeleportPearl()),
    TRANSMISSION_BEACON(new TransmissionBeacon()),
    ENDER_PASSIVE(new EnderPassive()),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.spark.Spark}
     */
    SPARK_MOLOTOV(new Molotov()),
    SPARK_FLASH(new SparkFlash()),
    FIRE_GUY(new PassiveTalent("Fire Guy", "You're completely immune to &clava &7and &cfire &7damage.", Material.LAVA_BUCKET)),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.shadow_assassin.ShadowAssassin}
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
            Accumulate %1$s while using abilities in &9Stealth&7 mode.
                        
            Spend %1$s to use empowered abilities in &cFury&7 mode.
            """.formatted(Named.SHADOW_ENERGY),
            Material.CHORUS_FRUIT,
            Talent.Type.ENHANCE
    )),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.witcher.WitcherClass}
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
     * {@link me.hapyl.fight.game.heroes.archive.vortex.Vortex}
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
     * {@link me.hapyl.fight.game.heroes.archive.frostbite.Freazly}
     */
    ICICLES(new Icicles()),
    ICE_CAGE(new IceCageTalent()),
    ICE_BARRIER(new IceBarrier()),
    CHILL_AURA(new PassiveTalent("Chill Aura", """
            You emmit a &bchill aura&7, that &bslows&7 and decreases enemies %s in small AoE.
            """.formatted(AttributeType.ATTACK_SPEED), Material.LIGHT_BLUE_DYE)),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.dark_mage.DarkMage}
     */
    BLINDING_CURSE(new BlindingCurse()),
    SLOWING_AURA(new SlowingAura()),
    HEALING_AURA(new HealingAura()),
    SHADOW_CLONE(new ShadowClone()),
    DARK_MAGE_PASSIVE(new PassiveTalent("Wither Blood", """
            Upon taking &cdamage&7, there is a small chance to &8wither&7 the attacker.
            """, Material.WITHER_ROSE, Talent.Type.IMPAIR)),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.knight.BlastKnight}
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
     * {@link me.hapyl.fight.game.heroes.archive.ninja.Ninja}
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
     * {@link me.hapyl.fight.game.heroes.archive.taker.Taker}
     */
    FATAL_REAP(new FatalReap()),
    DEATH_SWAP(new DeathSwap()),
    SPIRITUAL_BONES(new SpiritualBonesPassive()),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.juju.JuJu}
     */
    ARROW_SHIELD(new ArrowShield()),
    @Deprecated CLIMB(new Climb()),
    TRICKS_OF_THE_JUNGLE(new TricksOfTheJungle()),
    POISON_ZONE(new PoisonZone()),
    JUJU_PASSIVE(new PassiveTalent(
            "Climb", """
            Raised by the &ajungle&7, Juju mastered the ability to &2climb&7 &nanything&7.

            &6&lJUMP&7 no the wall to grab onto it and &bdescend&7 slowly.
            &6&lSNEAK&7 to climb upwards.
            """, Material.LEATHER_BOOTS
    )),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.swooper.Swooper}
     */
    BLAST_PACK(new BlastPack()),
    SWOOPER_SMOKE_BOMB(new SmokeBomb()),
    @Deprecated BLINK(new Blink()),
    SWOOPER_PASSIVE(new SwooperPassive()),

    // Shark
    SUBMERGE(new SubmergeTalent()),
    WHIRLPOOL(new Whirlpool()),
    CLAW_CRITICAL(new PassiveTalent(
            "Oceanborn/Sturdy Claws", """
            &b&lOceanborn:
            While in water, your speed and damage is drastically increased.
                        
            &b&lSturdy Claws:
            Critical hits summons an ancient creature from beneath that deals extra damage and heals you!
            """,
            Material.MILK_BUCKET
    )),

    // Librarian
    BLACK_HOLE(new BlackHole()),
    ENTITY_DARKNESS(new EntityDarkness()),
    LIBRARIAN_SHIELD(new LibrarianShield()),
    WEAPON_DARKNESS(new WeaponDarkness()),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.harbinger.Harbinger}
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
     * {@link me.hapyl.fight.game.heroes.archive.techie.Techie}
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
            """.formatted(Named.ENERGY), Material.CHAINMAIL_HELMET, Talent.Type.IMPAIR
    )),

    // Killing Machine
    LASER_EYE(new LaserEye()),
    //GRENADE(new ShellGrande()),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.shaman.Shaman}
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
     * {@link me.hapyl.fight.game.heroes.archive.bounty_hunter.BountyHunter}
     */
    SHORTY(new ShortyShotgun()),
    GRAPPLE(new GrappleHookTalent()),
    SMOKE_BOMB(new PassiveTalent("Smoke Bomb", """
            Whenever your &chealth&7 falls &nbelow&7 &c50%&7, you gain a &aSmoke Bomb&7.
                        
            Throw it to create a &8smoke field&7 that &3blinds&7 everyone inside it and grant you a &bspeed boost&7.
            """, Material.ENDERMAN_SPAWN_EGG
    )),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.heavy_knight.SwordMaster}
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
     * {@link me.hapyl.fight.game.heroes.archive.orc.Orc}
     */
    ORC_GROWN(new OrcGrowl()),
    ORC_AXE(new OrcAxe()),
    ORC_PASSIVE(new PassiveTalent("Don't Anger Me", """
            Taking &ncontinuous&7 &cdamage&7 within the set time window will trigger %s for &b3s&7.
            """.formatted(Named.BERSERK), Material.FERMENTED_SPIDER_EYE)),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.engineer.Engineer}
     */
    ENGINEER_SENTRY(new EngineerSentry()),
    ENGINEER_TURRET(new EngineerTurret()),
    ENGINEER_RECALL(new EngineerRecall()),
    ENGINEER_PASSIVE(new PassiveTalent("Magnetic Attraction", """
            Every few seconds you'll receive an Iron Ingot.
            Use it to build stuff!""", Material.IRON_INGOT)),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.bloodfield.Bloodfiend}
     */
    TWIN_CLAWS(new TwinClaws()),
    CANDLEBANE(new CandlebaneTalent()),
    BLOOD_CHALICE(new BloodChaliceTalent()),
    BLOOD_CUP(new BloodCup()),
    SUCCULENCE(new BloodfiendPassive()),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.zealot.Zealot}
     */
    BROKEN_HEART_RADIATION(new BrokenHeartRadiation()),
    MALEVOLENT_HITSHIELD(new MalevolentHitshield()),
    FEROCIOUS_STRIKES(new FerociousStrikes()),
    @Deprecated MALEDICTION_VEIL(new MaledictionVeil()),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.rogue.Rogue}
     */
    EXTRA_CUT(new ExtraCut()),
    SWAYBLADE(new Swayblade()),
    SECOND_WIND(new SecondWind()),

    // ???,
    SYNTHETIC_SUN(new SyntheticSun()),

    ;

    private final static Map<Talent.Type, List<Talents>> BY_TYPE;

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
    public static List<Talents> byType(@Nonnull Talent.Type type) {
        return BY_TYPE.getOrDefault(type, Lists.newArrayList());
    }

    private static String format(String textBlock, @Nullable Object... format) {
        if (format == null || format.length == 0) {
            return textBlock;
        }

        return BFormat.format(textBlock, format);
    }
}
