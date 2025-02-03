package me.hapyl.fight.game.heroes;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.util.Described;
import me.hapyl.eterna.module.util.Named;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.async.HeroStatsAsynchronousDocument;
import me.hapyl.fight.database.entry.MasteryEntry;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.skin.Skins;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HeroPlayerItemMaker {

    private final Hero hero;
    private final HeroStatsAsynchronousDocument stats;

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
        final Skins skin = CF.getDatabase(player).skinEntry.getSelected(hero);

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

                // Mastery
                final PlayerDatabase database = CF.getDatabase(player);
                final MasteryEntry entry = database.masteryEntry;
                final long exp = entry.getExp(hero);

                final StringBuilder name = new StringBuilder(hero.getName());

                if (averageRating != null) {
                    name.append(" ").append(averageRating);
                }
                if (exp > 0) {
                    name.append(" ").append("&8(&6%s&8)".formatted(entry.getLevelString(hero)));
                }

                final ItemBuilder builder = new ItemBuilder(hero.getItem())
                        .setName(name.toString())
                        .addLore("&8/hero " + hero.getKeyAsString())
                        .addLore();

                // Archetypes
                final HeroProfile profile = hero.getProfile();

                builder.addLore(Color.DEFAULT.bold() + "ᴘʀᴏꜰɪʟᴇ");
                builder.addLore(" &7Archetypes: " + profile.getSimpleArchetypesDisplay());
                builder.addLore(" &7Affiliation: " + profile.getAffiliation());
                builder.addLore(" &7Gender: " + profile.getGender());
                builder.addLore(" &7Race: " + profile.getRace());

                final HeroAttributes attributes = hero.getAttributes();

                builder.addLore();
                builder.addLore(Color.DEFAULT.bold() + "ᴀᴛᴛʀɪʙᴜᴛᴇꜱ");
                builder.addLore(attributes.getLore(AttributeType.MAX_HEALTH));
                builder.addLore(attributes.getLore(AttributeType.ATTACK));
                builder.addLore(attributes.getLore(AttributeType.DEFENSE));
                builder.addLore(attributes.getLore(AttributeType.SPEED));

                builder.addLore();
                builder.addLore(Color.DEFAULT.bold() + "ᴅᴇꜱᴄʀɪᴘᴛɪᴏɴ");
                builder.addTextBlockLore(hero.getDescription(), "&8&o ", 35);

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
                final PlayerRating averageRating = maker.stats.getAverageRating();
                final ItemBuilder builder = new ItemBuilder(hero.getItem());

                builder.setName(hero.getName() + (averageRating != null ? " " + averageRating : ""));
                builder.addLore();

                // Archetypes
                final HeroProfile profile = hero.getProfile();

                builder.addLore(Color.DEFAULT.bold() + "ᴘʀᴏꜰɪʟᴇ");
                builder.addLore(" &7Archetypes:");
                profile.getArchetypes().forEach(archetype -> {
                    builder.addLore("  " + archetype.toString());
                });

                builder.addLore(" &7Affiliation: " + profile.getAffiliation());
                builder.addLore(" &7Gender: " + profile.getGender());
                builder.addLore(" &7Race: " + profile.getRace());


                // Attributes
                final HeroAttributes attributes = hero.getAttributes();

                builder.addLore();
                builder.addLore(Color.DEFAULT.bold() + "ᴀᴛᴛʀɪʙᴜᴛᴇꜱ");

                attributes.forEachMandatoryAndNonDefault((type, value) -> {
                    builder.addLore(" &7%s: &b%s".formatted(type.getName(), type.getFormatted(attributes)));
                });

                builder.addLore();
                builder.addLore(Color.DEFAULT.bold() + "ᴅᴇꜱᴄʀɪᴘᴛɪᴏɴ");
                builder.addTextBlockLore(hero.getDescription(), "&8&o ", 35);

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
