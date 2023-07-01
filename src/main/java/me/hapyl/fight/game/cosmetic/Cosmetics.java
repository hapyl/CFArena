package me.hapyl.fight.game.cosmetic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.cosmetic.storage.*;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.DisabledCommand;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum Cosmetics {

    BLOOD(new Cosmetic("Blood", "A classic redstone particles mimicking blood.", Type.KILL, Rarity.COMMON, Material.REDSTONE) {
        @Override
        public void onDisplay(Display display) {
            display.particle(Particle.BLOCK_CRACK, 20, 0.4d, 0.4d, 0.4d, 0.0f, Bukkit.createBlockData(Material.REDSTONE_BLOCK));
            display.sound(Sound.BLOCK_STONE_BREAK, 0.0f);
        }
    }),

    COOKIE_MADNESS(new Cosmetic("Cookie Madness", "More cookies! Mo-o-ore!", Type.KILL, Rarity.UNCOMMON, Material.COOKIE) {
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

    // Death Cosmetics
    SCARY_DOOKIE(new Cosmetic("Scary Dookie", "The ultimate scare.", Type.DEATH, Rarity.RARE, Material.COCOA_BEANS) {
        @Override
        public void onDisplay(Display display) {
            final Item item = display.item(Material.COCOA_BEANS, 6000);
            final Player player = display.getPlayer();
            final String name = player == null ? "Someones" : player.getName();

            item.setCustomName(Chat.format("&c&l%s's Dookie", name));
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

    // Contrails
    MUSIC(new MusicContrail()),
    RAINBOW(new RainbowContrail()),
    BED_ROCKING(new BedRockingContrail()),
    FLOWER_PATH(new FlowerPathContrail()),
    SHADOW_TRAIL(new ShadowTrail()),

    // Prefixes
    FIGHTER(new PrefixCosmetic(
            "Fighter",
            "Show everyone who you really are.",
            "&a[&lFighter&a]",
            Rarity.COMMON
    ).setIcon(Material.WOODEN_SWORD)),

    OCTAVE(new PrefixCosmetic("Octave", "♪ ♪♫ ♪ ♫♫", "&d♪&lOctave&d♫", Rarity.RARE).setIcon(Material.NOTE_BLOCK)),

    STAR(new PrefixCosmetic("Star", "I'm on a roll!", "&e★&6&lStar&e☆", Rarity.EPIC).setIcon(Material.GOLD_NUGGET)),

    BIOHAZARD(new PrefixCosmetic("Biohazard", "Put your mask on!", "&a☢&2&lBiohazard&a☣", Rarity.EPIC).setIcon(Material.SLIME_BALL)),

    LOVE(new PrefixCosmetic("Love", "Love is...", "&c♥&d&lLove&c❤", Rarity.RARE).setIcon(Material.APPLE)),
    PEACE(new PrefixCosmetic("Peace", "Peace!", "&2&l✌", Rarity.LEGENDARY).setIcon(Material.WHITE_WOOL)),
    HAPPY(new PrefixCosmetic("Happy", "Just be happy!", "&a☺&lHappy&a☻", Rarity.COMMON).setIcon(Material.EMERALD)),

    GENDER_MALE(new PrefixCosmetic(
            "Gender: Male",
            "Express your gender!",
            "&b♂&3&lMale&b♂",
            Rarity.RARE
    ).setIcon(Material.SOUL_LANTERN)),

    GENDER_FEMALE(new PrefixCosmetic(
            "Gender: Female",
            "Express your gender!",
            "&d♀&5&lFemale&d♀",
            Rarity.RARE
    ).setIcon(Material.LANTERN)),

    ANNIHILATOR(new PrefixCosmetic(
            "Annihilator",
            "Show me what you got!",
            "&c☠&4&lAnnihilator&c&l☠",
            Rarity.LEGENDARY
    ).setIcon(Material.WITHER_SKELETON_SKULL)),

    SUNNY(new PrefixCosmetic(
            "Sunny",
            "It's a nice weather outside :)",
            "&e☀&6&lSunny&e☀",
            Rarity.EPIC
    ).setIcon(Material.GOLD_BLOCK)),

    RAINY(new PrefixCosmetic(
            "Rainy",
            "I've got my umbrella!",
            "&b🌧&3&lRainy&b☂",
            Rarity.EPIC
    ).setIcon(Material.WATER_BUCKET)),

    GLITCH(new PrefixCosmetic(
            "Glitch",
            "Is this thing on?",
            "&a✚&lGl&k&l1&atch&a&k&l✚&R",
            Rarity.RARE
    ).setIcon(Material.REDSTONE_TORCH)),

    // Win Effects
    FIREWORKS(new FireworksWinEffect(), true),
    AVALANCHE(new AvalancheWinEffect()),
    TWERK(new TwerkWinEffect()),

    ;

    private final static Map<Type, List<Cosmetics>> byType = Maps.newHashMap();

    static {
        for (Cosmetics value : values()) {
            if (value.ignore || value.cosmetic instanceof DisabledCommand) {
                continue;
            }

            byType.computeIfAbsent(value.getCosmetic().getType(), k -> Lists.newArrayList()).add(value);
        }
    }

    private final boolean ignore;
    private final Cosmetic cosmetic;

    Cosmetics(Cosmetic cosmetic) {
        this(cosmetic, false);
    }

    Cosmetics(Cosmetic cosmetic, boolean force) {
        this.cosmetic = cosmetic;
        this.ignore = force;
    }

    public Cosmetic getCosmetic() {
        return cosmetic;
    }

    @Nonnull
    public Type getType() {
        return cosmetic.getType();
    }

    public boolean isUnlocked(Player player) {
        return PlayerDatabase.getDatabase(player).getCosmetics().hasCosmetic(this);
    }

    public boolean isSelected(Player player) {
        return PlayerDatabase.getDatabase(player).getCosmetics().getSelected(getType()) == this;
    }

    // static members
    public static List<Cosmetics> getByType(Type type) {
        return new ArrayList<>(byType.getOrDefault(type, Lists.newArrayList()));
    }

    @Nullable
    public static Cosmetics getSelected(Player player, Type type) {
        return PlayerDatabase.getDatabase(player).getCosmetics().getSelected(type);
    }
}
