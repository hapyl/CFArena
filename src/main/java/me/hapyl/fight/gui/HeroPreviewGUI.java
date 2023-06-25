package me.hapyl.fight.gui;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.Origin;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.ItemStacks;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import me.hapyl.spigotutils.module.inventory.gui.SlotPattern;
import me.hapyl.spigotutils.module.inventory.gui.SmartComponent;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HeroPreviewGUI extends PlayerGUI {

    private final SlotPattern PATTERN = new SlotPattern(new byte[][] {
            { 0, 0, 0, 0, 1, 1, 1, 0, 0 }, { 0, 0, 0, 0, 1, 0, 1, 0, 0 }, { 0, 0, 0, 0, 0, 1, 0, 0, 0 }
    });

    private final Heroes heroes;
    private final Set<Talent> attributeDisplay;

    public HeroPreviewGUI(Player player, Heroes heroes, int selectIndex) {
        super(player, "Hero Preview - " + heroes.getHero().getName(), 5);
        this.heroes = heroes;
        this.attributeDisplay = Sets.newHashSet();

        update(selectIndex);
    }

    public void update(int index) {
        final Hero hero = heroes.getHero();
        final ItemStack blackBar = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("&f").toItemStack();

        for (int i = 0; i < getSize(); i++) {
            if ((i < 8 || i >= getSize() - 8) || i % 9 == 0 || i % 9 == 8) {
                setItem(i, blackBar);
            }
        }

        setItem(
                18,
                ItemBuilder.playerHead(
                                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ=="
                        )
                        .setName("&aGo Back")
                        .setLore("&7To Hero Selection")
                        .toItemStack(),
                player -> new HeroSelectGUI(player, index)
        );

        setItem(11, buildHeroPreview(hero));
        setItem(29, hero.getWeapon().getItem());

        final UltimateTalent ultimate = hero.getUltimate();
        final boolean showingUltimateAttributes = attributeDisplay.contains(ultimate);

        setItem(32, showingUltimateAttributes ? abilityAttributeOrAir(ultimate) : abilityItemOrAir(ultimate));

        if (showingUltimateAttributes) {
            setClick(32, player -> {
                attributeDisplay.remove(ultimate);
                PlayerLib.plingNote(player, 2.0f);
                update(index);
            });
        }
        else {
            setClick(32, player -> {
                attributeDisplay.add(ultimate);
                PlayerLib.plingNote(player, 2.0f);
                update(index);
            });
        }

        final SmartComponent component = newSmartComponent();

        hero.getTalentsSorted().forEach(talent -> {
            if (talent == null) {
                return;
            }

            if (attributeDisplay.contains(talent)) {
                component.add(abilityAttributeOrAir(talent), player -> {
                    attributeDisplay.remove(talent);
                    PlayerLib.plingNote(player, 2.0f);
                    update(index);
                });
                return;
            }

            if (talent instanceof PassiveTalent) {
                component.add(abilityItemOrAir(talent));
            }
            else {
                component.add(abilityItemOrAir(talent), player -> {
                    attributeDisplay.add(talent);
                    PlayerLib.plingNote(player, 2.0f);
                    update(index);
                });
            }
        });

        component.apply(this, PATTERN, 1);

        // favourite item
        final boolean favourite = heroes.isFavourite(getPlayer());
        setItem(
                17,
                new ItemBuilder(favourite ? Material.LIME_DYE : Material.GRAY_DYE).setName("&aFavourite")
                        .addLore()
                        .addSmartLore("Favourite heroes appear first in hero selection screen.")
                        .addLore()
                        .addLore("&eClick to %s your favourite list.", (favourite ? "remove from" : "add to"))
                        .predicate(favourite, ItemBuilder::glow)
                        .toItemStack(),
                player -> {
                    heroes.setFavourite(player, !favourite);

                    Chat.sendMessage(
                            player,
                            "&a%s %s %s from your favourites.",
                            (favourite ? "Removed" : "Added"),
                            hero.getName(),
                            (favourite ? "from" : "to")
                    );
                    PlayerLib.plingNote(player, 2.0f);

                    update(index);
                }
        );

        setItem(
                35,
                ItemBuilder.of(Material.CREEPER_BANNER_PATTERN, "Statistics", "Click to view this hero global statistics!").asIcon(),
                player -> new HeroStatisticGUI(player, heroes, index)
        );

        fixAbilityItemsCount();
        openInventory();
    }

    // FIXME (hapyl): 025, Jun 25: This should really be in hero class
    @Nonnull
    private ItemStack buildHeroPreview(Hero hero) {
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

        return builder.toItemStack();
    }

    @Nonnull
    private ItemStack abilityAttributeOrAir(Talent talent) {
        if (talent == null) {
            return ItemStacks.AIR;
        }

        return new ItemBuilder(talent.getItemAttributes())
                .addLore()
                .addLore("&eClick to hide attributes")
                .asIcon();
    }

    @Nonnull
    private ItemStack abilityItemOrAir(Talent talent) {
        if (talent == null) {
            return ItemStacks.AIR;
        }

        final boolean isPassive = talent instanceof PassiveTalent;
        return new ItemBuilder(talent.getItem())
                .addLoreIf("", !isPassive)
                .addLoreIf("&eClick to show attributes", !isPassive)
                .asIcon();
    }

    private void fixAbilityItemsCount() {
        for (int i = 0, slot = 13, amount = 1; i < 6; i++, slot += (slot % 9 == 6 ? 7 : 1)) {
            final ItemStack item = getInventory().getItem(slot);
            if (item == null) {
                continue;
            }
            item.setAmount(amount++);
        }
    }

    private void formatDebug() {
        if (!getPlayer().isOp()) {
            return;
        }

        final Hero hero = heroes.getHero();
        final Weapon weapon = hero.getWeapon();

        // Format weapon
        final Inventory inventory = getInventory();
        addLore(
                inventory.getItem(29),
                "",
                "&c[Debug]",
                " &bDamage: " + weapon.getDamage(),
                " &bId: " + (weapon.getId() == null ? "&cNo Id" : weapon.getId())
        );

        // Format Hero
        addLore(
                inventory.getItem(11),
                "",
                "&c[Debug]",
                " &bEnum: " + heroes.name(),
                " &bClass: " + heroes.getHero().getClass().getSimpleName()
        );

    }

    private void addLore(ItemStack stack, Object... lore) {
        if (stack == null || stack.getItemMeta() == null) {
            return;
        }

        final ItemMeta meta = stack.getItemMeta();
        List<String> existingLore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();

        for (final Object str : lore) {
            existingLore.add(Chat.format(str == null ? "null" : str.toString()));
        }

        meta.setLore(existingLore);
        stack.setItemMeta(meta);
    }


}
