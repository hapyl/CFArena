package me.hapyl.fight.gui;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.heroes.PlayerSkinPreview;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.heroes.CachedHeroItem;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.playerskin.PlayerSkin;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.gui.styled.StyledTexture;
import me.hapyl.fight.util.ItemStacks;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import me.hapyl.spigotutils.module.inventory.gui.SlotPattern;
import me.hapyl.spigotutils.module.inventory.gui.SmartComponent;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.npc.ItemSlot;
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
            { 0, 0, 0, 0, 1, 1, 1, 0, 0 },
            { 0, 0, 0, 0, 1, 0, 1, 0, 0 },
            { 0, 0, 0, 0, 0, 1, 0, 0, 0 }
    });

    private final Heroes heroes;
    private final Set<Talent> attributeDisplay;

    public HeroPreviewGUI(Player player, Heroes heroes, int returnPage) {
        super(player, "Hero Preview - " + heroes.getHero().getName(), 5);
        this.heroes = heroes;
        this.attributeDisplay = Sets.newHashSet();

        update(returnPage);
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
                StyledTexture.ARROW_LEFT.asIcon("Go Back", "To Hero Selection"),
                player -> new HeroSelectGUI(player, index)
        );


        setItem(11, hero.getCachedHeroItem().getItem(CachedHeroItem.Type.DETAILS));

        final Weapon weapon = hero.getWeapon();
        setItem(29, weapon.getItem());

        if (weapon.hasAbilities()) {
            setItem(
                    38,
                    new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                            .setName(Color.GREEN + "This weapon has abilities!")
                            .addLore()
                            .addSmartLore("Remember to check the weapon! It has some unique abilities.")
                            .asIcon()
            );
        }

        final PlayerSkin skin = hero.getSkin();

        if (skin != null) {
            setItem(26, ItemBuilder.of(Material.LEATHER_CHESTPLATE, "&aHero-Specific Skin")
                    .addLore()
                    .addSmartLore("This hero uses a &bcustom skin&7 instead of armor, meaning your skin will be changed during the game.")
                    .addLore("&8You can turn this off in your settings.")
                    .addLore()
                    .addLore(Color.BUTTON + "Click to preview skin!")
                    .asIcon(), player -> {
                closeInventory();

                new PlayerSkinPreview(player, skin) {
                    @Override
                    public void onTaskStart() {
                        npc.setItem(ItemSlot.MAINHAND, hero.getWeapon().getItem());
                    }
                };
            });
        }

        final UltimateTalent ultimate = hero.getUltimate();
        final boolean showingUltimateAttributes = attributeDisplay.contains(ultimate);

        // Ultimate
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

            // Display attributes if needed
            if (talent.isDisplayAttributes()) {
                component.add(abilityItemOrAir(talent), player -> {
                    attributeDisplay.add(talent);
                    PlayerLib.plingNote(player, 2.0f);
                    update(index);
                });
            }
            else {
                component.add(abilityItemOrAir(talent));
            }
        });

        component.apply(this, PATTERN, 1);

        // Favourite
        final boolean favourite = heroes.isFavourite(getPlayer());

        setItem(
                17,
                new ItemBuilder(favourite ? Material.LIME_DYE : Material.GRAY_DYE)
                        .setName("&aFavourite")
                        .addLore()
                        .addSmartLore("Favourite heroes appear first in hero selection screen.")
                        .addLore()
                        .addLoreIf(Color.SUCCESS + "This hero is your favourite!", favourite)
                        .addLoreIf(Color.ERROR + "This hero is not your favourite!", !favourite)
                        .addLore(Color.BUTTON + ("Click to %s your favourite list.".formatted(favourite ? "remove from" : "add to")))
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
                ItemBuilder.of(Material.CREEPER_BANNER_PATTERN, "Global Statistics")
                        .addLore()
                        .addSmartLore("View global statistics of this hero, such as playtime, kills, deaths, etc.")
                        .addLore()
                        .addLore(Color.BUTTON + "Click to view!")
                        .asIcon(),
                player -> new HeroStatisticGUI(player, heroes, index)
        );

        fixAbilityItemsCount();
        openInventory();
    }

    @Nonnull
    private ItemStack abilityAttributeOrAir(Talent talent) {
        if (talent == null) {
            return ItemStacks.AIR;
        }

        return new ItemBuilder(talent.getItemAttributes())
                .addLore()
                .addLore(Color.BUTTON + "Click to hide details")
                .asIcon();
    }

    @Nonnull
    private ItemStack abilityItemOrAir(Talent talent) {
        if (talent == null) {
            return ItemStacks.AIR;
        }

        final boolean isDisplayAttributes = talent.isDisplayAttributes();

        return new ItemBuilder(talent.getItem())
                .addLoreIf("", isDisplayAttributes)
                .addLoreIf(Color.BUTTON + "Click for details", isDisplayAttributes)
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
