package me.hapyl.fight.game.heroes;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.async.HeroStatsAsynchronousDocument;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.skin.Skins;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

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
                final StringBuilder name = new StringBuilder(hero.getName());

                if (averageRating != null) {
                    name.append(" ").append(averageRating);
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
                
                for (AttributeType attributeType : AttributeType.values()) {
                    final double value = attributes.get(attributeType);
                    final double defaultValue = attributeType.defaultValue();
                    
                    if (!attributeType.isMandatory() && defaultValue == value) {
                        continue;
                    }
                    
                    builder.addLore(" &7%s: &b%s".formatted(attributeType.getName(), attributeType.getFormatted(attributes)));
                }
                
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

    }

}
