package me.hapyl.fight.game.cosmetic;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.registry.KeyFunction;
import me.hapyl.eterna.module.registry.SimpleRegistry;
import me.hapyl.eterna.module.util.Compute;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.contrail.*;
import me.hapyl.fight.game.cosmetic.death.*;
import me.hapyl.fight.game.cosmetic.gadget.BalloonGadgetCosmetic;
import me.hapyl.fight.game.cosmetic.gadget.FireworkGadgetCosmetic;
import me.hapyl.fight.game.cosmetic.gadget.SnowballGadgetCosmetic;
import me.hapyl.fight.game.cosmetic.gadget.dice.DiceGadgetCosmetic;
import me.hapyl.fight.game.cosmetic.gadget.dice.HighClassDiceCosmetic;
import me.hapyl.fight.game.cosmetic.kill.*;
import me.hapyl.fight.game.cosmetic.prefix.AdminPrefixCosmetic;
import me.hapyl.fight.game.cosmetic.prefix.PrefixCosmetic;
import me.hapyl.fight.game.cosmetic.win.AvalancheWinCosmetic;
import me.hapyl.fight.game.cosmetic.win.FireworksWinCosmetic;
import me.hapyl.fight.game.cosmetic.win.HellboundWinCosmetic;
import me.hapyl.fight.game.cosmetic.win.TwerkWinCosmetic;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.StaticUUID;
import org.bukkit.Material;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class CosmeticRegistry extends SimpleRegistry<Cosmetic> {

    ////////////////////////////
    // *=* Kill Cosmetics *=* //
    ////////////////////////////
    public final BloodCosmetic BLOOD;
    public final CookieMadnessCosmetic COOKIE_MADNESS;
    public final SquidLaunchCosmetic SQUID_LAUNCH;
    public final GroundPunchCosmetic GROUND_PUNCH;
    public final GiantSwordCosmetic GIANT_SWORD;
    public final LightningStrikeCosmetic LIGHTNING;
    public final AbstractCouture COUTURE_KILL;
    public final MusicCosmetic MUSIC_KILL;

    /////////////////////////////
    // *=* Death Cosmetics *=* //
    /////////////////////////////
    public final ScaryDookieCosmetic SCARY_DOOKIE;
    public final FinalMessageCosmetic FINAL_MESSAGE;
    public final BigBlastCosmetic BIG_BLAST;
    public final AbstractCouture COUTURE_DEATH;
    public final StylishFallCosmetic STYLISH_FALL;
    public final ElectrocuteCosmetic ELECTROCUTE;
    public final EmeraldExplosionCosmetic EMERALD_EXPLOSION;
    public final BonesAndShredsCosmetic BONES_AND_SHREDS;
    public final FlowerPotCosmetic FLOWER_POT;

    ////////////////////////////////
    // *=* Contrail Cosmetics *=* //
    ////////////////////////////////
    public final MusicContrailCosmetic MUSIC;
    public final RainbowContrailCosmetic RAINBOW;
    public final BedRockingContrailCosmetic BED_ROCKING;
    public final FlowerPathContrailCosmetic FLOWER_PATH;
    public final ShadowTrailCosmetic SHADOW_TRAIL;
    public final FireVeilCosmetic FIRE_VEIL;

    ///////////////////////////
    // *=* Win Cosmetics *=* //
    ///////////////////////////
    @ApiStatus.Internal public final FireworksWinCosmetic FIREWORKS;
    public final AvalancheWinCosmetic AVALANCHE;
    public final TwerkWinCosmetic TWERK;
    public final HellboundWinCosmetic HELL_BOUND;

    /////////////////////
    // *=* Gadgets *=* //
    /////////////////////
    public final FireworkGadgetCosmetic FIREWORK;
    public final DiceGadgetCosmetic DICE;
    public final HighClassDiceCosmetic DICE_HIGH_CLASS;
    public final SnowballGadgetCosmetic SNOWBALL_GADGET;
    public final BalloonGadgetCosmetic BALLOON;

    //////////////////////////////
    // *=* Prefix Cosmetics *=* //
    //////////////////////////////
    public final PrefixCosmetic FIGHTER;
    public final PrefixCosmetic OCTAVE;
    public final PrefixCosmetic STAR;
    public final PrefixCosmetic BIOHAZARD;
    public final PrefixCosmetic LOVE;
    public final PrefixCosmetic PEACE;
    public final PrefixCosmetic HAPPY;
    public final PrefixCosmetic GENDER_MALE;
    public final PrefixCosmetic GENDER_FEMALE;
    public final PrefixCosmetic ANNIHILATOR;
    public final PrefixCosmetic SUNNY;
    public final PrefixCosmetic RAINY;
    public final PrefixCosmetic GLITCH;
    public final PrefixCosmetic CUPCAKE;
    public final PrefixCosmetic ACCESS_DENIED;
    public final PrefixCosmetic KISS_KISS;
    public final PrefixCosmetic MONKEY;
    public final PrefixCosmetic SMILEY;
    public final PrefixCosmetic TRIANGLE;
    public final PrefixCosmetic DRAGON;
    public final PrefixCosmetic DICE_STATUS;
    public final PrefixCosmetic RELIC_HUNTER;
    public final PrefixCosmetic CRAB_RAVE;
    public final PrefixCosmetic CAT_PREFIX;
    public final PrefixCosmetic HAPYL_PREFIX;
    public final PrefixCosmetic DIDEN_PREFIX;

    // This is a little trick used for store to display that player has all the cosmetics, a little hacky, but I think it's kinda clever. 🤓
    @ApiStatus.Internal public final NullCosmetic NULL_COSMETIC;

    private final Map<Type, List<Cosmetic>> byType;
    private final Map<Rarity, List<Cosmetic>> byRarity;

    public CosmeticRegistry() {
        byType = Maps.newHashMap();
        byRarity = Maps.newHashMap();

        ////////////////////////////
        // *=* Kill Cosmetics *=* //
        ////////////////////////////
        BLOOD = register("blood", BloodCosmetic::new);
        COOKIE_MADNESS = register("cookie_madness", CookieMadnessCosmetic::new);
        SQUID_LAUNCH = register("squid_launch", SquidLaunchCosmetic::new);
        GROUND_PUNCH = register("ground_punch", GroundPunchCosmetic::new);
        GIANT_SWORD = register("giant_sword", GiantSwordCosmetic::new);
        LIGHTNING = register("lightning", LightningStrikeCosmetic::new);
        COUTURE_KILL = register("couture_kill", AbstractCouture.KillCosmetic::new);
        MUSIC_KILL = register("music_kill", MusicCosmetic::new);

        /////////////////////////////
        // *=* Death Cosmetics *=* //
        /////////////////////////////
        SCARY_DOOKIE = register("scary_dookie", ScaryDookieCosmetic::new);
        FINAL_MESSAGE = register("final_message", FinalMessageCosmetic::new);
        BIG_BLAST = register("big_blast", BigBlastCosmetic::new);
        COUTURE_DEATH = register("couture_death", AbstractCouture.DeathCosmetic::new);
        STYLISH_FALL = register("stylish_fall", StylishFallCosmetic::new);
        ELECTROCUTE = register("electrocute", ElectrocuteCosmetic::new);
        EMERALD_EXPLOSION = register("emerald_explosion", EmeraldExplosionCosmetic::new);
        BONES_AND_SHREDS = register("bones_and_shreds", BonesAndShredsCosmetic::new);
        FLOWER_POT = register("flower_pot", FlowerPotCosmetic::new);

        ////////////////////////////////
        // *=* Contrail Cosmetics *=* //
        ////////////////////////////////
        MUSIC = register("music", MusicContrailCosmetic::new);
        RAINBOW = register("rainbow", RainbowContrailCosmetic::new);
        BED_ROCKING = register("bed_rocking", BedRockingContrailCosmetic::new);
        FLOWER_PATH = register("flower_path", FlowerPathContrailCosmetic::new);
        SHADOW_TRAIL = register("shadow_trail", ShadowTrailCosmetic::new);
        FIRE_VEIL = register("fire_veil", FireVeilCosmetic::new);

        ///////////////////////////
        // *=* Win Cosmetics *=* //
        ///////////////////////////
        FIREWORKS = register("fireworks", FireworksWinCosmetic::new);
        AVALANCHE = register("avalanche", AvalancheWinCosmetic::new);
        TWERK = register("twerk", TwerkWinCosmetic::new);
        HELL_BOUND = register("hell_bound", HellboundWinCosmetic::new);

        /////////////////////
        // *=* Gadgets *=* //
        /////////////////////
        FIREWORK = register("firework", FireworkGadgetCosmetic::new);
        DICE = register("dice", DiceGadgetCosmetic::new);
        DICE_HIGH_CLASS = register("dice_high_class", HighClassDiceCosmetic::new);
        SNOWBALL_GADGET = register("snowball_gadget", SnowballGadgetCosmetic::new);
        BALLOON = register("balloon", BalloonGadgetCosmetic::new);

        //////////////////////////////
        // *=* Prefix Cosmetics *=* //
        //////////////////////////////
        FIGHTER = registerPrefix("fighter", "Fighter", "Show everyone who you really are.", "&a⚔", Rarity.COMMON, Material.WOODEN_HOE);
        OCTAVE = registerPrefix("octave", "Octave", "♪ ♪♫ ♪ ♫♫", "&d♫", Rarity.RARE, Material.NOTE_BLOCK);
        STAR = registerPrefix("star", "Star", "I'm on a roll!", "&e★", Rarity.EPIC, Material.GOLD_NUGGET);
        BIOHAZARD = registerPrefix("biohazard", "Biohazard", "Put your mask on!", "&a☣", Rarity.EPIC, Material.SLIME_BALL);
        LOVE = registerPrefix("love", "Love", "Love is...", "&c♥", Rarity.RARE, Material.APPLE);
        PEACE = registerPrefix("peace", "Peace", "Peace!", "&2&l✌", Rarity.LEGENDARY, Material.WHITE_WOOL, true);
        HAPPY = registerPrefix("happy", "Happy", "Just be happy!", "&a☻", Rarity.COMMON, Material.EMERALD);
        GENDER_MALE = registerPrefix("gender_male", "Gender: Male", "Express your gender!", "&b♂", Rarity.RARE, Material.SOUL_LANTERN);
        GENDER_FEMALE = registerPrefix("gender_female", "Gender: Female", "Express your gender!", "&d♀", Rarity.RARE, Material.LANTERN);
        ANNIHILATOR = registerPrefix(
                "annihilator",
                "Annihilator",
                "Show me what you got!",
                "&4&kx &4☠ &4&kx",
                Rarity.LEGENDARY,
                Material.WITHER_SKELETON_SKULL
        );
        SUNNY = registerPrefix("sunny", "Sunny", "It's nice weather outside :)", "&e☀", Rarity.EPIC, Material.GOLD_BLOCK);
        RAINY = registerPrefix("rainy", "Rainy", "Where is my umbrella?!", "&b🌧", Rarity.EPIC, Material.WATER_BUCKET);
        GLITCH = registerPrefix("glitch", "Glitch", "Is this thing on?", "&a&l&k💻", Rarity.RARE, Material.REDSTONE_TORCH);
        CUPCAKE = registerPrefix("cupcake", "Cupcake", "Tasty!~", "&d🧁", Rarity.EPIC, Material.PUMPKIN_PIE);
        ACCESS_DENIED = registerPrefix(
                "access_denied",
                "Access Denied",
                "There is no way out!",
                "&4\uD83D\uDEAB",
                Rarity.EPIC,
                Material.BARRIER
        );
        KISS_KISS = registerPrefix("kiss_kiss", "Kiss, Kiss", "x", "&c\uD83D\uDC8B", Rarity.RARE, Material.GOLDEN_APPLE);
        MONKEY = registerPrefix("monkey", "Monkey!", "Return to monke!", new Color("#674e38") + "🐵", Rarity.RARE, Material.BROWN_WOOL);
        SMILEY = registerPrefix("smiley", "Smiley", "Smiling through the day.", "&f&lツ", Rarity.RARE, Material.PUFFERFISH);
        TRIANGLE = registerPrefix("triangle", "Triangle", "Is it aligned?", "&3📐", Rarity.RARE, Material.WARPED_STAIRS);
        DRAGON = registerPrefix("dragon", "Dragon", "How do I train it?", Color.MAROON + "🐉", Rarity.EPIC, Material.DRAGON_HEAD);
        DICE_STATUS = registerPrefix(
                "dice_status",
                "High Class",
                "Straight from Kickback City!",
                Color.BLACK + "🎲",
                Rarity.LEGENDARY,
                Material.MUSIC_DISC_STAL,
                true
        );
        RELIC_HUNTER = registerPrefix(
                "relic_hunter",
                "Relic Hunter",
                "Gotta find them all.",
                Color.DIAMOND + "💎",
                Rarity.LEGENDARY,
                Material.DIAMOND,
                true
        );
        CRAB_RAVE = registerPrefix(
                "crab_rave",
                "Crab Rave",
                "You can hear it, can't you?",
                Color.CRAB + "🦀",
                Rarity.UNCOMMON,
                Material.NAUTILUS_SHELL
        );
        CAT_PREFIX = registerPrefix("cat_prefix", "Cat", "Meow?", "&9&l\uD83D\uDC31", Rarity.UNCOMMON, Material.COD);
        HAPYL_PREFIX = register(
                "hapyl_prefix",
                key -> new AdminPrefixCosmetic(key, "hapyl", Color.DARK_RED.bold() + "ዞ", StaticUUID.HAPYL)
        );
        DIDEN_PREFIX = register(
                "diden_prefix",
                key -> new AdminPrefixCosmetic(key, "DiDenPro", Color.DIDEN.bold() + "⨵", StaticUUID.DIDEN)
        );

        // Other
        NULL_COSMETIC = register("null_cosmetic", NullCosmetic::new);
    }

    @Nonnull
    @Override
    public <E extends Cosmetic> E register(@Nonnull String key, @Nonnull KeyFunction<E> fn) {
        final E cosmetic = super.register(key, fn);

        // Don't include disabled cosmetics in the lists
        if (!(cosmetic instanceof Disabled)) {
            byType.compute(cosmetic.getType(), Compute.listAdd(cosmetic));
            byRarity.compute(cosmetic.getRarity(), Compute.listAdd(cosmetic));
        }

        return cosmetic;
    }

    @Nonnull
    public List<Cosmetic> byType(@Nonnull Type type) {
        return CFUtils.copyMapList(byType, type);
    }

    @Nonnull
    public List<Cosmetic> byRarity(@Nonnull Rarity rarity) {
        return CFUtils.copyMapList(byRarity, rarity);
    }

    @Nonnull
    private PrefixCosmetic registerPrefix(@Nonnull String key, @Nonnull String name, @Nonnull String description, @Nonnull String prefix, @Nonnull Rarity rarity, @Nonnull Material icon, boolean exclusive) {
        return register(key, k -> new PrefixCosmetic(k, name, description, prefix, rarity, icon, exclusive));
    }

    @Nonnull
    private PrefixCosmetic registerPrefix(@Nonnull String key, @Nonnull String name, @Nonnull String description, @Nonnull String prefix, @Nonnull Rarity rarity, @Nonnull Material icon) {
        return registerPrefix(key, name, description, prefix, rarity, icon, false);
    }

}
