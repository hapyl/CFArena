package me.hapyl.fight.game.heroes;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.collection.HeroStatsCollection;
import me.hapyl.fight.database.entry.MasteryEntry;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.cosmetic.skin.Skins;
import me.hapyl.fight.util.Described;
import me.hapyl.fight.util.Named;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

public class HeroPlayerItemMaker {

    private final Hero hero;
    private final HeroStatsCollection stats;

    public HeroPlayerItemMaker(Hero hero) {
        this.hero = hero;
        this.stats = hero.getStats();
    }

    @Nonnull
    public ItemStack makeItem(@Nonnull Type type, @Nonnull Player player) {
        return makeBuilder(type, player).asIcon();
    }

    @Nonnull
    public ItemBuilder makeBuilder(@Nonnull Type type, @Nonnull Player player) {
        final ItemBuilder builder = type.createItem(this, player);
        final Skins skin = PlayerDatabase.getDatabase(player).skinEntry.getSelected(hero);

        // FIXME: This was in makeItem() instead of makeBuilder(), if any problems occur put it back ig -h
        if (skin != null) {
            builder.setHeadTextureUrl(skin.getSkin().getEquipment().getHelmetTexture());
        }

        return builder;
    }

    public enum Type {
        SELECT {
            @Nonnull
            @Override
            ItemBuilder createItem(@Nonnull HeroPlayerItemMaker maker, @Nonnull Player player) {
                final Hero hero = maker.hero;
                final PlayerRating averageRating = maker.stats.getAverageRating();

                final ItemBuilder builder = new ItemBuilder(hero.getItem())
                        .setName(hero.toString())
                        .addLore("&8/hero " + hero.getDatabaseKey().key().toLowerCase(Locale.ROOT))
                        .addLore();

                // Archetypes
                final ArchetypeList archetypes = hero.getArchetypes();
                builder.addLore("&7Archetypes: " + archetypes.getSimpleDisplay());

                builder.addLoreIf(
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
                builder.addTextBlockLore(hero.getDescription(), "&8&o", 35);

                if (hero instanceof ComplexHero) {
                    builder.addTextBlockLore("""
                                                        
                            &6&lComplex Hero!
                            This hero is more difficult to play than others. Thus is &nnot&7 recommended for newer players.
                            """);
                }

                // Mastery
                final PlayerDatabase database = PlayerDatabase.getDatabase(player);
                final MasteryEntry entry = database.masteryEntry;
                final long exp = entry.getExp(hero);

                // Don't display mastery if never played this hero
                if (exp != 0) {
                    builder.addLore();
                    builder.addLore(entry.makeMasteryHeader(hero));
                    builder.addLore(entry.makeProgressBar(hero));
                }

                // Usage
                builder.addLore().addLore("&eLeft Click to select").addLore("&6Right Click for details");

                return builder;
            }
        },

        DETAILS {
            @Nonnull
            @Override
            ItemBuilder createItem(@Nonnull HeroPlayerItemMaker maker, @Nonnull Player player) {
                final Hero hero = maker.hero;
                final HeroStatsCollection stats = maker.stats;

                final ItemBuilder builder = new ItemBuilder(hero.getItem());
                final ArchetypeList archetypes = hero.getArchetypes();
                final Affiliation affiliation = hero.getAffiliation();
                final Gender gender = hero.getGender();
                final Race race = hero.getRace();

                builder.setName(hero.toString());
                builder.addLore();

                // Archetypes
                builder.addLore("&7Archetypes:");
                archetypes.forEach(archetype -> {
                    builder.addLore(" " + archetype.toString());
                    builder.addSmartLore(archetype.getDescription(), "&r  &7&o");
                });

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
                    builder.addLore(" &7%s: &b%s".formatted(type.getName(), type.getFormatted(attributes)));
                });

                builder.addLore();
                builder.addTextBlockLore(hero.getDescription(), "&8&o", 35);

                return builder;
            }
        };

        @Nonnull
        ItemBuilder createItem(@Nonnull HeroPlayerItemMaker item, @Nonnull Player player) {
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
