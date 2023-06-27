package me.hapyl.fight.game.heroes;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Locale;

public class CachedHeroItem {

    private final Hero hero;

    private ItemStack selectItem;
    private ItemStack detailsItem;

    public CachedHeroItem(Hero hero) {
        this.hero = hero;
    }

    @Nonnull
    public ItemStack getDetailsItem() {
        if (detailsItem == null) {
            final ItemBuilder builder = new ItemBuilder(hero.getItem());
            final Archetype archetype = hero.getArchetype();

            builder.setName("&a%s", hero.getName())
                    .addLore()
                    //// Role
                    //.addLore("&7Role: &b%s", hero.getRole().getName())
                    //.addSmartLore(hero.getRole().getDescription(), "&8&o")
                    // Archetype
                    .addLore("&7Archetype: &b" + archetype)
                    .addSmartLore(archetype.getDescription(), "&8&o");

            // Fraction
            if (hero.getOrigin() != Origin.NOT_SET) {
                builder.addLore();
                builder.addLore("&7Origin: &b%s", hero.getOrigin().getName());
                builder.addSmartLore(hero.getOrigin().getDescription(), "&8&0");
            }

            // Attributes
            builder.addLore().addLore("&e&lAttributes:");
            final HeroAttributes attributes = hero.getAttributes();

            attributes.forEach((type, value) -> {
                builder.addLore(" &7%s: &b%s", type.getName(), type.getFormatted(attributes));
            });

            builder.addLore();
            builder.addSmartLore(hero.getDescription(), "&8&o");

            detailsItem = builder.asIcon();
        }

        return detailsItem;
    }

    @Nonnull
    public ItemStack getSelectItem() {
        if (selectItem == null) {
            final ItemBuilder builder = new ItemBuilder(hero.getItem())
                    .setName("&a" + hero.getName())
                    .addLore("&8/hero " + Heroes.byHandle(hero).name().toLowerCase(Locale.ROOT))
                    .addLore()
                    //.addLore("&7Role: &b%s", hero.getRole().getName())
                    .addLore("&7Archetype: &b" + hero.getArchetype())
                    .addLoreIf("&7Origin: &b%s".formatted(hero.getOrigin().getName()), hero.getOrigin() != Origin.NOT_SET)
                    .addLore();

            final HeroAttributes attributes = hero.getAttributes();
            builder.addLore("&e&lAttributes: ");
            builder.addLore(attributes.getLore(AttributeType.HEALTH));
            builder.addLore(attributes.getLore(AttributeType.ATTACK));
            builder.addLore(attributes.getLore(AttributeType.DEFENSE));
            builder.addLore(attributes.getLore(AttributeType.SPEED));

            builder.addLore();
            builder.addSmartLore(hero.getDescription(), "&8&o");

            if (hero instanceof ComplexHero) {
                builder.addLore();
                builder.addTextBlockLore("""
                        &6&lComplex Hero!
                        This hero is more difficult to play than others. Thus is &nnot&7 recommended for newer players.
                        """);
            }

            // Usage
            builder.addLore().addLore("&eLeft Click to select").addLore("&6Right Click for details");
            selectItem = builder.asIcon();
        }

        return selectItem;
    }

}
