package me.hapyl.fight.game.crate;

import me.hapyl.eterna.module.registry.KeyedEnum;
import me.hapyl.eterna.module.util.WeightedCollection;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.cosmetic.WeightedDrop;
import me.hapyl.fight.game.crate.convert.Product;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.util.EnumWrapper;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static me.hapyl.fight.registry.Registries.getCosmetics;

@Deprecated // legacy
public enum Crates implements KeyedEnum, WeightedDrop, EnumWrapper<Crate>, Product<Long> {

    NOVICE(
            new Crate("Novice Crate")
                    .with(getCosmetics().BLOOD, getCosmetics().GROUND_PUNCH, getCosmetics().COOKIE_MADNESS)
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

        @Nonnull
        @Override
        public String formatProduct(@Nonnull Long amount) {
            return Rarity.COMMON.getColor() + super.formatProduct(amount);
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

        @Nonnull
        @Override
        public String formatProduct(@Nonnull Long amount) {
            return Rarity.UNCOMMON.getColor() + super.formatProduct(amount);
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

        @Nonnull
        @Override
        public String formatProduct(@Nonnull Long amount) {
            return Rarity.RARE.getColor() + super.formatProduct(amount);
        }
    },

    TITLE(
            new Crate("Prefix Crate")
                    .with(Type.PREFIX)
    ) {
        @Override
        public int getWeight() {
            return 30;
        }
    },

    LEGACY(
            new Crate("Legacy Crate")
                    .with(
                            getCosmetics().BLOOD,
                            getCosmetics().COOKIE_MADNESS,
                            getCosmetics().SQUID_LAUNCH,
                            getCosmetics().GROUND_PUNCH,
                            getCosmetics().GIANT_SWORD,
                            getCosmetics().LIGHTNING
                    )
                    .with(getCosmetics().SCARY_DOOKIE, getCosmetics().FINAL_MESSAGE, getCosmetics().BIG_BLAST, getCosmetics().ELECTROCUTE)
                    .with(getCosmetics().MUSIC, getCosmetics().RAINBOW, getCosmetics().FLOWER_PATH)
                    .with(
                            getCosmetics().FIGHTER,
                            getCosmetics().OCTAVE,
                            getCosmetics().STAR,
                            getCosmetics().BIOHAZARD,
                            getCosmetics().LOVE,
                            getCosmetics().HAPPY,
                            getCosmetics().GENDER_MALE,
                            getCosmetics().GENDER_FEMALE,
                            getCosmetics().ANNIHILATOR,
                            getCosmetics().RAINY,
                            getCosmetics().GLITCH
                    )
                    .with(getCosmetics().AVALANCHE, getCosmetics().TWERK)
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
    public Crate getWrapped() {
        return getCrate();
    }

    @Nonnull
    @Override
    public String getName() {
        return crate.getName();
    }

    @Nonnull
    @Override
    public String getDescription() {
        return EnumWrapper.super.getDescription();
    }

    public Crate getCrate() {
        return crate;
    }

    @Override
    public void subtractProduct(@Nonnull PlayerDatabase database, @Nonnull Long value) {
        database.crateEntry.removeCrate(this, value);
    }

    @Nonnull
    @Override
    public Long getProduct(@Nonnull PlayerDatabase database) {
        return database.crateEntry.getCrates(this);
    }

    @Override
    public int getWeight() {
        return 0;
    }

    @Nonnull
    public static Crates randomCrate() {
        return WEIGHTED.getRandomElementOrDefault(Crates.COMMON);
    }

    public static void grant(@Nonnull GamePlayer player, @Nullable Crates crate) {
        if (crate == null) {
            return;
        }

        player.getDatabase().crateEntry.addCrate(crate);

        // Display/Fx
        player.sendMessage(CrateLocation.PREFIX + Color.SUCCESS + "Received " + crate.getName() + "!");

        player.playWorldSound(Sound.ENTITY_VILLAGER_YES, 0.75f);
        player.playWorldSound(Sound.BLOCK_WOOD_PLACE, 0.75f);
        player.playWorldSound(Sound.BLOCK_CHEST_LOCKED, 1.25f);
    }
}
