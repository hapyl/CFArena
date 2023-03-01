package me.hapyl.fight.game.cosmetic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.fight.game.cosmetic.storage.*;
import me.hapyl.fight.game.database.Database;
import me.hapyl.fight.game.shop.Rarity;
import me.hapyl.fight.game.shop.ShopItem;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public enum Cosmetics {

    BLOOD(new Cosmetic("Blood", "A classic redstone particles mimicking blood.", 200, Type.KILL, Rarity.COMMON) {
        @Override
        public void onDisplay(Display display) {
            display.particle(Particle.BLOCK_CRACK, 20, 0.4d, 0.4d, 0.4d, 0.0f, Bukkit.createBlockData(Material.REDSTONE_BLOCK));
            display.sound(Sound.BLOCK_STONE_BREAK, 0.0f);
        }
    }),

    COOKIE_MADNESS(new Cosmetic("Cookie Madness", "More cookies! Mo-o-ore!", 400, Type.KILL, Rarity.UNCOMMON) {
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
    LIGHTNING(new Cosmetic("Light Strike", "Strikes a lightning effect.", 100, Type.KILL, Rarity.COMMON) {
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
    SCARY_DOOKIE(new Cosmetic("Scary Dookie", "The ultimate scare.", 1000, Type.DEATH, Rarity.RARE) {
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

    // Prefixes
    FIGHTER(new PrefixCosmetic(
            "Fighter",
            "Show everyone who you really are.",
            "&a[&lFighter&a]",
            ShopItem.NOT_PURCHASABLE,
            Rarity.COMMON
    ).setIcon(Material.WOODEN_SWORD)),

    /**
     * 	FIGHTER(Type.PREFIX, new Fighter()),
     * 	OCTAVE(Type.PREFIX, new Octave()),
     * 	STAR(Type.PREFIX, new Prefix("Star", "I'm on a roll!", "&e★&6&lStar&e☆", 1000, Material.GOLD_NUGGET, ShopItemRarity.EPIC)),
     * 	BIOHAZARD(Type.PREFIX, new Prefix("Biohazard", "Put your mask on!", "&a☢&2&lBiohazard&a☣", 1000, Material.SLIME_BALL, ShopItemRarity.EPIC)),
     * 	LOVE(Type.PREFIX, new Prefix("Love", "Love is...", "&c♥&d&lLove&c❤", 777, Material.APPLE, ShopItemRarity.RARE)),
     * 	HAPPY(Type.PREFIX, new Prefix("Happy", "Just be happy!", "&a☺&lHappy&a☻", 200, Material.EMERALD, ShopItemRarity.COMMON)),
     * 	// gender series
     * 	MALE(Type.PREFIX, new Prefix("Gender: Male", "Express your gender!", "&b♂&3&lMale&b♂", 300, Material.SOUL_LANTERN, ShopItemRarity.RARE)),
     * 	FEMALE(Type.PREFIX, new Prefix("Gender: Female", "Express your gender!", "&d♀&5&lFemale&d♀", 300, Material.LANTERN, ShopItemRarity.RARE)),
     * 	ANNIHILATOR(Type.PREFIX, new Prefix("Annihilator", "Show me what you got!", "&c☠&4&lAnnihilator&c&l☠", 1000, Material.WITHER_SKELETON_SKULL, ShopItemRarity.LEGENDARY)),
     * 	SUNNY(Type.PREFIX, new Prefix("Sunny", "It's a nice weather outside :)", "&e☀&6&lSunny&e☀", 500, Material.GOLD_BLOCK, ShopItemRarity.EPIC)),
     * 	RAINY(Type.PREFIX, new Prefix("Rainy", "I've got my umbrella!", "&b🌧&3&lRainy&b☂", 500, Material.WATER_BUCKET, ShopItemRarity.EPIC)),
     * 	GLITCH(Type.PREFIX, new Prefix("Glitch", "Is this thing on?", "&a✚&lGl&k&l1&atch&a&k&l✚&R", 666, Material.REDSTONE_TORCH, ShopItemRarity.RARE)),
     */

    ;

    private final static Map<Type, List<Cosmetic>> byType = Maps.newHashMap();

    static {
        for (Cosmetics value : values()) {
            byType.computeIfAbsent(value.getCosmetic().getType(), k -> Lists.newArrayList()).add(value.getCosmetic());
        }
    }

    private final Cosmetic cosmetic;

    Cosmetics(Cosmetic cosmetic) {
        this.cosmetic = cosmetic;
    }

    public static List<Cosmetic> getByType(Type type) {
        return byType.getOrDefault(type, Lists.newArrayList());
    }

    @Nullable
    public static Cosmetics getSelected(Player player, Type type) {
        return Database.getDatabase(player).getCosmetics().getSelected(type);
    }

    public Cosmetic getCosmetic() {
        return cosmetic;
    }

    public Type getType() {
        return cosmetic.getType();
    }

}
