package me.hapyl.fight.game.talents;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.talents.storage.TestChargeTalent;
import me.hapyl.fight.game.talents.storage.alchemist.CauldronAbility;
import me.hapyl.fight.game.talents.storage.alchemist.RandomPotion;
import me.hapyl.fight.game.talents.storage.archer.ShockDark;
import me.hapyl.fight.game.talents.storage.archer.TripleShot;
import me.hapyl.fight.game.talents.storage.darkmage.BlindingCurse;
import me.hapyl.fight.game.talents.storage.darkmage.HealingAura;
import me.hapyl.fight.game.talents.storage.darkmage.ShadowClone;
import me.hapyl.fight.game.talents.storage.darkmage.SlowingAura;
import me.hapyl.fight.game.talents.storage.doctor.ConfusionPotion;
import me.hapyl.fight.game.talents.storage.doctor.HarvestBlocks;
import me.hapyl.fight.game.talents.storage.ender.TeleportPearl;
import me.hapyl.fight.game.talents.storage.ender.TransmissionBeacon;
import me.hapyl.fight.game.talents.storage.freazly.IceCone;
import me.hapyl.fight.game.talents.storage.harbinger.MeleeStance;
import me.hapyl.fight.game.talents.storage.harbinger.TidalWave;
import me.hapyl.fight.game.talents.storage.healer.HealingPotion;
import me.hapyl.fight.game.talents.storage.healer.ReviveTotem;
import me.hapyl.fight.game.talents.storage.hercules.HerculesJump;
import me.hapyl.fight.game.talents.storage.hercules.HerculesShift;
import me.hapyl.fight.game.talents.storage.juju.ArrowShield;
import me.hapyl.fight.game.talents.storage.juju.Climb;
import me.hapyl.fight.game.talents.storage.km.LaserEye;
import me.hapyl.fight.game.talents.storage.km.ShellGrande;
import me.hapyl.fight.game.talents.storage.knight.SlownessPotion;
import me.hapyl.fight.game.talents.storage.knight.Spear;
import me.hapyl.fight.game.talents.storage.librarian.BlackHole;
import me.hapyl.fight.game.talents.storage.librarian.EntityDarkness;
import me.hapyl.fight.game.talents.storage.librarian.LibrarianShield;
import me.hapyl.fight.game.talents.storage.librarian.WeaponDarkness;
import me.hapyl.fight.game.talents.storage.mage.ArcaneMute;
import me.hapyl.fight.game.talents.storage.mage.MageTransmission;
import me.hapyl.fight.game.talents.storage.moonwalker.MoonSliteBomb;
import me.hapyl.fight.game.talents.storage.moonwalker.MoonslitePillar;
import me.hapyl.fight.game.talents.storage.nightmare.Paranoia;
import me.hapyl.fight.game.talents.storage.nightmare.ShadowShift;
import me.hapyl.fight.game.talents.storage.ninja.NinjaDash;
import me.hapyl.fight.game.talents.storage.ninja.NinjaSmoke;
import me.hapyl.fight.game.talents.storage.pytaria.FlowerBreeze;
import me.hapyl.fight.game.talents.storage.pytaria.FlowerEscape;
import me.hapyl.fight.game.talents.storage.shadowassassin.ShadowPrism;
import me.hapyl.fight.game.talents.storage.shadowassassin.ShroudedStep;
import me.hapyl.fight.game.talents.storage.shaman.ResonanceType;
import me.hapyl.fight.game.talents.storage.shaman.Totem;
import me.hapyl.fight.game.talents.storage.shaman.TotemTalent;
import me.hapyl.fight.game.talents.storage.shark.Submerge;
import me.hapyl.fight.game.talents.storage.shark.Whirlpool;
import me.hapyl.fight.game.talents.storage.spark.Molotov;
import me.hapyl.fight.game.talents.storage.spark.SparkFlash;
import me.hapyl.fight.game.talents.storage.swooper.BlastPack;
import me.hapyl.fight.game.talents.storage.swooper.Blink;
import me.hapyl.fight.game.talents.storage.taker.DeathSwap;
import me.hapyl.fight.game.talents.storage.taker.FatalReap;
import me.hapyl.fight.game.talents.storage.taker.SpiritualBonesPassive;
import me.hapyl.fight.game.talents.storage.tamer.MineOBall;
import me.hapyl.fight.game.talents.storage.techie.TrapCage;
import me.hapyl.fight.game.talents.storage.techie.TrapWire;
import me.hapyl.fight.game.talents.storage.troll.Repulsor;
import me.hapyl.fight.game.talents.storage.troll.TrollSpin;
import me.hapyl.fight.game.talents.storage.vampire.BatSwarm;
import me.hapyl.fight.game.talents.storage.vampire.VampirePet;
import me.hapyl.fight.game.talents.storage.vortex.StarAligner;
import me.hapyl.fight.game.talents.storage.vortex.VortexStar;
import me.hapyl.fight.game.talents.storage.witcher.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

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
            "Drinking potions will increase &eIntoxication &7level that will decrease constantly.__Keeping an eye on &eIntoxication &7level is a good idea, who knows what might happen...",
            Material.DRAGON_BREATH
    )),

    // Moonwalker
    MOONSLITE_PILLAR(new MoonslitePillar()),
    MOONSLITE_BOMB(new MoonSliteBomb()),
    TARGET(new PassiveTalent("Space Suit", "You suit grants you slow falling ability.", Material.FEATHER)),

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
            "When Pytaria's &chealth &7is lower or equal to &c50%&7, her damage is increased by &b50%&7.", Material.ROSE_BUSH
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
            "As a assassin, you have mastered ability to stay in the shadows.____While &e&lSNEAKING&7, you become completely invisible, but cannot deal damage and your footsteps are visible.",
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
            "Dealing &bcontinuous damage&7 to the &bsame target&7 will increase your combo, greater combo hits deals &cincreased damage&7.",
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
            "Elusive Burst",
            "Fully charged shots while sneaking will infuse your arrow.____Infused arrows exploded into small clusters dealing big damage upon hit.",
            Material.PEONY
    )),

    // Swooper
    BLAST_PACK(new BlastPack()),
    BLINK(new Blink()),
    SNIPER_SCOPE(new PassiveTalent(
            "Sniper Scope",
            "&e&lSNEAK &7to activate sniper scope and increase your rifle's damage and distance.",
            Material.SPYGLASS
    )),

    // Shark
    SUBMERGE(new Submerge()),
    WHIRLPOOL(new Whirlpool()),
    CLAW_CRITICAL(new PassiveTalent(
            "Oceanborn/Sturdy Claws",
            "&b&lOceanborn:__&While in water, your speed and damage is drastically increased.____&b&lSturdy Claws:__&7Your hits have &b10% &7chance to &ccrit&7!__Critical hits summons an ancient creature from beneath that deals extra damage and heals you!",
            Material.MILK_BUCKET
    )),

    // Librarian
    BLACK_HOLE(new BlackHole()),
    ENTITY_DARKNESS(new EntityDarkness()),
    LIBRARIAN_SHIELD(new LibrarianShield()),
    WEAPON_DARKNESS(new WeaponDarkness()),

    // Harbinger
    STANCE(new MeleeStance()),
    TIDAL_WAVE(new TidalWave()),
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
            "Every &b10s&7, hacks into opponents revealing their location and health.____&b&lCYber Hack:__&7A small virus that reveals opponent's location, slows them and applies &6&lVulnerability&7 for short duration.",
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
    HEALING_POTION(new HealingPotion()),
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
            "Blood Thirst",
            "&cYour health is constantly drained.____Whenever you or your bats hit an opponent, you will gain a stack of &bblood&7, up to &b10&7 stacks.____Drink the blood to &cincrease your damage&7 and &cheal yourself&7.____&6Healing, damage boost, duration and cooldown is based on the amount of stacks consumed.",
            Material.REDSTONE
    )),

    // test (keep last)
    TestChargeTalent(new TestChargeTalent());

    public static class Handle {
    }

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

    public static Talents fromTalent(Talent talent) {
        for (Talents value : values()) {
            if (value.getTalent() == talent) {
                return value;
            }
        }

        throw new IllegalArgumentException("non-registered talent");
    }

    public void startCd(Player player) {
        getTalent().startCd(player);
    }

    public String getName() {
        return getTalent().getName();
    }

    @Nonnull
    public Talent getTalent() {
        return talent;
    }

}
