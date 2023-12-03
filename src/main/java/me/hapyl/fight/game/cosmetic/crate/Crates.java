package me.hapyl.fight.game.cosmetic.crate;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.CosmeticCollection;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.WeightedDrop;
import me.hapyl.fight.util.EnumWrapper;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.WeightedCollection;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum Crates implements WeightedDrop, EnumWrapper<Crate> {

    NOVICE(
            new Crate("Novice Crate")
                    .with(Cosmetics.BLOOD, Cosmetics.GROUND_PUNCH, Cosmetics.COOKIE_MADNESS)
    ) {
        @Override
        public int getWeight() {
            return 50;
        }
    },

    COMMON(
            new Crate("Common Crate")
                    .with(Rarity.COMMON)
    ) {
        @Override
        public int getWeight() {
            return 70;
        }
    },

    UNCOMMON(
            new Crate("Uncommon Crate")
                    .with(Rarity.UNCOMMON)
    ) {
        @Override
        public int getWeight() {
            return 40;
        }
    },

    RARE(
            new Crate("Rare Crate")
                    .with(Rarity.RARE)
    ) {
        @Override
        public int getWeight() {
            return 30;
        }
    },

    TITLE(
            new Crate("Prefix Crate")
                    .with(CosmeticCollection.PREFIX)
    ) {
        @Override
        public int getWeight() {
            return 30;
        }
    },

    LEGACY(
            new Crate("Legacy Crate")
                    .with(
                            Cosmetics.BLOOD,
                            Cosmetics.COOKIE_MADNESS,
                            Cosmetics.SQUID_LAUNCH,
                            Cosmetics.GROUND_PUNCH,
                            Cosmetics.GIANT_SWORD,
                            Cosmetics.LIGHTNING
                    )
                    .with(Cosmetics.SCARY_DOOKIE, Cosmetics.FINAL_MESSAGE, Cosmetics.BIG_BLAST, Cosmetics.ELECTROCUTE)
                    .with(Cosmetics.MUSIC, Cosmetics.RAINBOW, Cosmetics.FLOWER_PATH)
                    .with(
                            Cosmetics.FIGHTER,
                            Cosmetics.OCTAVE,
                            Cosmetics.STAR,
                            Cosmetics.BIOHAZARD,
                            Cosmetics.LOVE,
                            Cosmetics.HAPPY,
                            Cosmetics.GENDER_MALE,
                            Cosmetics.GENDER_FEMALE,
                            Cosmetics.ANNIHILATOR,
                            Cosmetics.RAINY,
                            Cosmetics.GLITCH
                    )
                    .with(Cosmetics.AVALANCHE, Cosmetics.TWERK)
    ),

    ;

    private final static WeightedCollection<Crates> WEIGHTED = new WeightedCollection<>();

    static {
        for (Crates crate : values()) {
            if (!crate.isDroppable()) {
                continue;
            }

            WEIGHTED.add(crate, crate.getWeight());
        }
    }

    private final Crate crate;

    Crates(Crate crate) {
        this.crate = crate;
    }

    @Nonnull
    @Override
    public Crate get() {
        return getCrate();
    }

    public Crate getCrate() {
        return crate;
    }

    @Override
    public int getWeight() {
        return 0;
    }

    @Nonnull
    public static Crates randomCrate() {
        return WEIGHTED.getOrDefault(Crates.COMMON);
    }

    public static void grant(@Nonnull GamePlayer player, @Nullable Crates crate) {
        if (crate == null) {
            return;
        }

        player.getDatabase().crateEntry.addCrate(crate);

        // Display/Fx
        player.sendMessage(CrateChest.PREFIX + Color.SUCCESS + "Received " + crate.getName() + "!");

        PlayerLib.playSound(Sound.ENTITY_VILLAGER_YES, 0.75f);
        PlayerLib.playSound(Sound.BLOCK_WOOD_PLACE, 0.75f);
        PlayerLib.playSound(Sound.BLOCK_CHEST_LOCKED, 1.25f);
    }
}
