package me.hapyl.fight.game.heroes;

import com.google.common.collect.Maps;
import me.hapyl.fight.database.collection.HeroStatsCollection;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Described;
import me.hapyl.fight.util.Named;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;

public class CachedHeroItem {

    private final Hero hero;
    private final HeroStatsCollection stats;

    private final Map<Type, ItemStack> itemMap;
    //private final Map<Type, Map<Language, ItemStack>> itemMap;

    public CachedHeroItem(Hero hero) {
        this.hero = hero;
        this.stats = hero.getStats();

        this.itemMap = Maps.newHashMap();
    }

    @Nonnull
    public ItemStack getItem(@Nonnull Type type) {
        return itemMap.computeIfAbsent(type, fn -> type.createItem(this));
    }

    public enum Type {
        SELECT {
            @Nonnull
            @Override
            ItemStack createItem(@Nonnull CachedHeroItem item) {
                final Hero hero = item.hero;
                final PlayerRating averageRating = item.stats.getAverageRating();

                final ItemBuilder builder = new ItemBuilder(hero.getItem())
                        .setName(hero.toString())
                        .addLore("&8/hero " + hero.getHandle().name().toLowerCase(Locale.ROOT))
                        .addLore()
                        .addLore("&7Archetype: " + hero.getArchetype())
                        .addLoreIf(
                                "&7Affiliation: " + hero.getAffiliation(),
                                hero.getAffiliation() != Affiliation.NOT_SET
                        )
                        .addLoreIf("&7Player Rating: " + averageRating, averageRating != null)
                        .addLore();

                final HeroAttributes attributes = hero.getAttributes();
                builder.addLore("&e&lAttributes:");
                builder.addLore(attributes.getLore(AttributeType.MAX_HEALTH));
                builder.addLore(attributes.getLore(AttributeType.ATTACK));
                builder.addLore(attributes.getLore(AttributeType.DEFENSE));
                builder.addLore(attributes.getLore(AttributeType.SPEED));

                builder.addLore();
                builder.addTextBlockLore(hero.getDescription(), "&8&o", 35, CFUtils.DISAMBIGUATE);

                if (hero instanceof ComplexHero) {
                    builder.addTextBlockLore("""
                                                        
                            &6&lComplex Hero!
                            This hero is more difficult to play than others. Thus is &nnot&7 recommended for newer players.
                            """);
                }

                // Usage
                builder.addLore().addLore("&eLeft Click to select").addLore("&6Right Click for details");

                return builder.asIcon();
            }
        },

        DETAILS {
            @Nonnull
            @Override
            ItemStack createItem(@Nonnull CachedHeroItem item) {
                final Hero hero = item.hero;
                final HeroStatsCollection stats = item.stats;

                final ItemBuilder builder = new ItemBuilder(hero.getItem());
                final Archetype archetype = hero.getArchetype();
                final Affiliation affiliation = hero.getAffiliation();
                final Gender gender = hero.getGender();
                final Race race = hero.getRace();

                builder.setName(hero.toString());

                appendLore(builder, "Archetype", archetype, null);
                appendLore(builder, "Affiliation", affiliation, Affiliation.NOT_SET);

                // Player rating
                final PlayerRating averageRating = stats.getAverageRating();
                if (averageRating != null) {
                    builder.addLore();
                    builder.addLore("&7Player Rating: " + averageRating);
                    builder.addSmartLore("Player rating is calculated by players voting.", "&8&o");
                }

                if (gender != Gender.UNKNOWN || race != Race.UNKNOWN) {
                    builder.addLore();
                }

                builder.addLoreIf("Gender: " + gender, gender != Gender.UNKNOWN);
                builder.addLoreIf("Race: " + race, race != Race.UNKNOWN);

                // Attributes
                builder.addLore().addLore("&e&lAttributes:");
                final HeroAttributes attributes = hero.getAttributes();

                attributes.forEachMandatoryAndNonDefault((type, value) -> {
                    builder.addLore(" &7%s: &b%s", type.getName(), type.getFormatted(attributes));
                });

                builder.addLore();
                builder.addTextBlockLore(hero.getDescription(), "&8&o", 35, CFUtils.DISAMBIGUATE);

                return builder.asIcon();
            }
        };

        @Nonnull
        ItemStack createItem(@Nonnull CachedHeroItem cachedHeroItem) {
            throw new IllegalStateException();
        }

        private static <T extends Enum<T> & Named> void appendLore(ItemBuilder builder, String name, T named, @Nullable T nullValue) {
            if (named == null || named == nullValue) {
                return;
            }

            builder.addLore();
            builder.addLore("&7%s: %s".formatted(name, named.toString()));

            if (named instanceof Described described) {
                builder.addSmartLore(described.getDescription(), "&8&o");
            }
        }
    }

}
