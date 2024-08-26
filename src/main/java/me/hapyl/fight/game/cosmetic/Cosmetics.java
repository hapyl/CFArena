package me.hapyl.fight.game.cosmetic;

import com.google.common.collect.Maps;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CosmeticEntry;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.archive.*;
import me.hapyl.fight.game.cosmetic.archive.gadget.FireworkGadget;
import me.hapyl.fight.game.cosmetic.archive.gadget.dice.DiceGadget;
import me.hapyl.fight.game.cosmetic.archive.gadget.dice.HighClassDice;
import me.hapyl.fight.registry.KeyedEnum;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.StaticUUID;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.util.Compute;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

// Yeah, don't use enums as registries 🤪
public enum Cosmetics implements RareItem, BelongsToCollection, KeyedEnum {

    ////////////////////
    // Kill Cosmetics //
    ////////////////////
    BLOOD(new Cosmetic("Blood", "A classic redstone particles mimicking blood.", Type.KILL, Rarity.COMMON, Material.REDSTONE) {
        @Override
        public void onDisplay(Display display) {
            display.particle(Particle.BLOCK, 20, 0.4d, 0.4d, 0.4d, 0.0f, Bukkit.createBlockData(Material.REDSTONE_BLOCK));
            display.sound(Sound.BLOCK_STONE_BREAK, 0.0f);
        }
    }),

    COOKIE_MADNESS(new Cosmetic("Cookie Madness", "More cookies! Moooore!", Type.KILL, Rarity.UNCOMMON, Material.COOKIE) {
        @Override
        public void onDisplay(Display display) {
            display.repeat(10, 2, (r, tick) -> {
                display.item(Material.COOKIE, 60);
                display.sound(Sound.ENTITY_ITEM_PICKUP, 0.0f + (tick * 0.1f));
            });
        }
    }),

    SQUID_LAUNCH(new SquidLaunchCosmetic()),
    GROUND_PUNCH(new GroundPunchCosmetic()),
    GIANT_SWORD(new GiantSwordCosmetic()),
    LIGHTNING(new Cosmetic("Light Strike", "Strikes a lightning effect.", Type.KILL, Rarity.COMMON, Material.LIGHTNING_ROD) {
        @Override
        public void onDisplay(Display display) {
            final World world = display.getLocation().getWorld();
            if (world == null) {
                return;
            }
            world.strikeLightningEffect(display.getLocation());
        }
    }),

    COUTURE_KILL(new CoutureCosmetic(Type.KILL)),
    MUSIC_KILL(new MusicCosmetic()),

    /////////////////////
    // Death Cosmetics //
    /////////////////////
    SCARY_DOOKIE(new Cosmetic("Scary Dookie", "The ultimate scare.", Type.DEATH, Rarity.RARE, Material.COCOA_BEANS) {
        @Override
        public void onDisplay(Display display) {
            final Item item = display.item(Material.COCOA_BEANS, 6000);
            final Player player = display.getPlayer();
            final String name = player == null ? "Someones" : player.getName();

            item.setCustomName(Chat.format("&c&l%s's Dookie".formatted(name)));
            item.setCustomNameVisible(true);
            item.setVelocity(new Vector(0.0d, 0.2d, 0.0d));

            display.sound(Sound.ENTITY_PLAYER_BURP, 1.25f);
            display.sound(Sound.ENTITY_HORSE_SADDLE, 1.75f);
        }
    }),

    FINAL_MESSAGE(new FinalMessageCosmetic()),
    BIG_BLAST(new BigBlastCosmetic()),
    COUTURE_DEATH(new CoutureCosmetic(Type.DEATH)),
    STYLISH_FALL(new StylishFallCosmetic()),
    ELECTROCUTE(new ElectrocuteCosmetic()),
    EMERALD_EXPLOSION(new EmeraldExplosion()),
    BONES_AND_SHREDS(new BonesAndShredsCosmetic()),
    FLOWER_POT(new FlowerPotCosmetic()),

    ///////////////
    // Contrails //
    ///////////////
    MUSIC(new MusicContrail()),
    RAINBOW(new RainbowContrail()),
    BED_ROCKING(new BedRockingContrail()),
    FLOWER_PATH(new FlowerPathContrail()),
    SHADOW_TRAIL(new ShadowTrail()),

    //////////////
    // Prefixes //
    //////////////
    FIGHTER(new PrefixCosmetic(
            "Fighter",
            "Show everyone who you really are.",
            "&a⚔",
            Rarity.COMMON
    ).setIcon(Material.WOODEN_SWORD)),

    OCTAVE(new PrefixCosmetic("Octave", "♪ ♪♫ ♪ ♫♫", "&d♫", Rarity.RARE).setIcon(Material.NOTE_BLOCK)),
    STAR(new PrefixCosmetic("Star", "I'm on a roll!", "&e★", Rarity.EPIC).setIcon(Material.GOLD_NUGGET)),
    BIOHAZARD(new PrefixCosmetic("Biohazard", "Put your mask on!", "&a☣", Rarity.EPIC).setIcon(Material.SLIME_BALL)),
    LOVE(new PrefixCosmetic("Love", "Love is...", "&c♥", Rarity.RARE).setIcon(Material.APPLE)),
    PEACE(new PrefixCosmetic("Peace", "Peace!", "&2&l✌", Rarity.LEGENDARY).setIcon(Material.WHITE_WOOL).setExclusive(true)),
    HAPPY(new PrefixCosmetic("Happy", "Just be happy!", "&a☻", Rarity.COMMON).setIcon(Material.EMERALD)),

    GENDER_MALE(new PrefixCosmetic(
            "Gender: Male",
            "Express your gender!",
            "&b♂",
            Rarity.RARE
    ).setIcon(Material.SOUL_LANTERN)),

    GENDER_FEMALE(new PrefixCosmetic(
            "Gender: Female",
            "Express your gender!",
            "&d♀",
            Rarity.RARE
    ).setIcon(Material.LANTERN)),

    ANNIHILATOR(new PrefixCosmetic(
            "Annihilator",
            "Show me what you got!",
            "&4&kx &4☠ &4&kx",
            Rarity.LEGENDARY
    ).setIcon(Material.WITHER_SKELETON_SKULL)),

    SUNNY(new PrefixCosmetic(
            "Sunny",
            "It's nice weather outside :)",
            "&e☀",
            Rarity.EPIC
    ).setIcon(Material.GOLD_BLOCK)),

    RAINY(new PrefixCosmetic(
            "Rainy",
            "I've got my umbrella!",
            "&b🌧",
            Rarity.EPIC
    ).setIcon(Material.WATER_BUCKET)),

    GLITCH(new PrefixCosmetic(
            "Glitch",
            "Is this thing on?",
            "&a&l&k💻",
            Rarity.RARE
    ).setIcon(Material.REDSTONE_TORCH)),

    CUPCAKE(new PrefixCosmetic(
            "Cupcake",
            "Tasty!~",
            "&d🧁",
            Rarity.EPIC
    ).setIcon(Material.PUMPKIN_PIE)),

    ACCESS_DENIED(new PrefixCosmetic(
            "Access Denied",
            "There is no way out!",
            "&4\uD83D\uDEAB",
            Rarity.EPIC
    ).setIcon(Material.BARRIER)),

    KISS_KISS(new PrefixCosmetic(
            "Kiss, Kiss",
            "x",
            "&c\uD83D\uDC8B",
            Rarity.RARE
    ).setIcon(Material.GOLDEN_APPLE)),

    MONKEY(new PrefixCosmetic(
            "Monkey!",
            "I'm monkey!",
            new Color("#674e38") + "🐵",
            Rarity.RARE
    ).setIcon(Material.BROWN_WOOL)),

    SMILEY(new PrefixCosmetic(
            "Smiley",
            "Smiling through the day.",
            "&f&lツ",
            Rarity.RARE
    ).setIcon(Material.PUFFERFISH)),

    TRIANGLE(new PrefixCosmetic(
            "Triangle",
            "Is it aligned?",
            "&3📐",
            Rarity.RARE
    ).setIcon(Material.WARPED_STAIRS)),

    DRAGON(new PrefixCosmetic(
            "Dragon",
            "How do I train it?",
            Color.MAROON + "🐉",
            Rarity.EPIC
    ).setIcon(Material.DRAGON_HEAD)),

    DICE_STATUS(new PrefixCosmetic(
            "High Class",
            "Straight from Kickback City!",
            Color.BLACK + "🎲",
            Rarity.LEGENDARY
    ).setIcon(Material.MUSIC_DISC_STAL).setExclusive(true)),

    RELIC_HUNTER(new PrefixCosmetic(
            "Relic Hunter",
            "Gotta find them all.",
            Color.DIAMOND + "💎",
            Rarity.LEGENDARY
    ).setIcon(Material.DIAMOND).setExclusive(true)),

    CRAB_RAVE(new PrefixCosmetic(
            "Crab Rave",
            "You can hear it, can't you?",
            Color.CRAB + "🦀",
            Rarity.UNCOMMON
    ).setIcon(Material.NAUTILUS_SHELL)),

    CAT_PREFIX(new PrefixCosmetic("Cat", "Meow?", "&9&l\uD83D\uDC31", Rarity.UNCOMMON).setIcon(Material.COD)),

    // Admin exclusive prefixes
    HAPYL_PREFIX(new AdminPrefixCosmetic("hapyl", Color.DARK_RED.bold() + "ዞ", StaticUUID.HAPYL)),
    DIDEN_PREFIX(new AdminPrefixCosmetic("DiDenPro", Color.DIDEN.bold() + "⨵", StaticUUID.DIDEN)),

    /////////////////
    // Win Effects //
    /////////////////

    /**
     * Should not explicitly be used.
     */
    @Deprecated FIREWORKS(new FireworksWinEffect().setExclusive(true)),
    AVALANCHE(new AvalancheWinEffect()),
    TWERK(new TwerkWinEffect()),
    HELL_BOUND(new HellboundWinEffect()),

    /////////////
    // Gadgets //
    /////////////
    FIREWORK(new FireworkGadget()),
    DICE(new DiceGadget()),
    DICE_HIGH_CLASS(new HighClassDice()),
    ;

    private final static Map<Type, List<Cosmetics>> byType = Maps.newHashMap();
    private final static Map<Rarity, List<Cosmetics>> byRarity = Maps.newHashMap();

    static {
        for (Cosmetics enumCosmetic : values()) {
            if (enumCosmetic.cosmetic instanceof Disabled) {
                continue;
            }

            final Cosmetic cosmetic = enumCosmetic.getCosmetic();
            final Type type = cosmetic.getType();
            final Rarity rarity = cosmetic.getRarity();

            byType.compute(type, Compute.listAdd(enumCosmetic));
            byRarity.compute(rarity, Compute.listAdd(enumCosmetic));
        }
    }

    private final Cosmetic cosmetic;
    @Nullable
    private CosmeticCollection collection;

    Cosmetics(Cosmetic cosmetic) {
        this.cosmetic = cosmetic;
        this.cosmetic.setHandle(this);
    }

    public Cosmetic getCosmetic() {
        return cosmetic;
    }

    public boolean isValidForCrate() {
        return !(cosmetic instanceof Disabled) && !cosmetic.isExclusive();
    }

    @Nonnull
    public Type getType() {
        return cosmetic.getType();
    }

    public boolean isUnlocked(Player player) {
        return PlayerDatabase.getDatabase(player).cosmeticEntry.hasCosmetic(this);
    }

    public void setUnlocked(Player player, boolean flag) {
        final CosmeticEntry cosmeticEntry = PlayerDatabase.getDatabase(player).cosmeticEntry;

        if (flag) {
            cosmeticEntry.addOwned(this);
        }
        else {
            cosmeticEntry.unsetSelected(this);
            cosmeticEntry.removeOwned(this);
        }
    }

    public boolean isSelected(Player player) {
        return PlayerDatabase.getDatabase(player).cosmeticEntry.getSelected(getType()) == this;
    }

    public void select(Player player) {
        PlayerDatabase.getDatabase(player).cosmeticEntry.setSelected(getType(), this);
    }

    public void deselect(Player player) {
        PlayerDatabase.getDatabase(player).cosmeticEntry.unsetSelected(getType());
    }

    @Nonnull
    @Override
    public Rarity getRarity() {
        return cosmetic.getRarity();
    }

    @Nonnull
    @Override
    public String getId() {
        return name();
    }

    @Nonnull
    public <T> T getCosmetic(Class<T> clazz) {
        if (clazz.isInstance(cosmetic)) {
            return clazz.cast(cosmetic);
        }

        throw new IllegalArgumentException("%s cannot be cast to %s".formatted(this, clazz.getSimpleName()));
    }

    @Nullable
    @Override
    public CosmeticCollection getCollection() {
        return collection;
    }

    @Override
    public void setCollection(@Nullable CosmeticCollection collection) {
        this.collection = collection;
    }

    public boolean canObtain(@Nonnull OfflinePlayer player) {
        return cosmetic.canObtain(player);
    }

    public boolean isExclusive() {
        return !cosmetic.isExclusive();
    }

    @Nonnull
    public static List<Cosmetics> getByType(Type type) {
        return CFUtils.copyMapList(byType, type);
    }

    @Nonnull
    public static List<Cosmetics> getByRarity(@Nonnull Rarity rarity) {
        return CFUtils.copyMapList(byRarity, rarity);
    }

    @Nullable
    public static Cosmetics getSelected(Player player, Type type) {
        return PlayerDatabase.getDatabase(player).cosmeticEntry.getSelected(type);
    }
}
