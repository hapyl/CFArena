package me.hapyl.fight.gui;

import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class HeroPreviewGUI extends PlayerGUI {

    private final int[] ABILITY_SLOTS = new int[] { 13, 14, 15, 22, 23, 24 };
    private final Heroes heroes;

    public HeroPreviewGUI(Player player, Heroes heroes) {
        super(player, "Hero Preview - " + heroes.getHero().getName(), 5);
        this.heroes = heroes;
        update();
    }

    public void update() {
        final Hero hero = heroes.getHero();
        final ItemStack blackBar = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("&f").toItemStack();

        for (int i = 0; i < getSize(); i++) {
            if ((i < 8 || i >= getSize() - 8) || i % 9 == 0 || i % 9 == 8) {
                setItem(i, blackBar);
            }
        }

        setItem(
                18,
                ItemBuilder
                        .playerHead(
                                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==")
                        .setName("&aGo Back")
                        .setLore("&7To Hero Selection")
                        .toItemStack(),
                HeroSelectGUI::new
        );

        setItem(
                11,
                new ItemBuilder(hero.getItem())
                        .setName("&a%s", hero.getName())
                        .addLore()
                        .addLore("&7Role: &b%s", hero.getRole().getName())
                        .addSmartLore(hero.getRole().getDescription(), "&8&o")
                        .addLore()
                        .addSmartLore(hero.getAbout(), " &7&o")
                        .toItemStack()
        );

        setItem(29, hero.getWeapon().getItem());
        setItem(32, abilityItemOrNull(hero.getUltimate()));

        setAbilityItems(hero);

        // favourite item
        final boolean favourite = heroes.isFavourite(getPlayer());
        setItem(
                26,
                new ItemBuilder(favourite ? Material.LIME_DYE : Material.GRAY_DYE)
                        .setName("&aFavourite")
                        .addLore()
                        .addSmartLore("Favourite heroes appear first in hero selection screen.")
                        .addLore()
                        .addLore("&eClick to %s your favourite list.", (favourite ? "remove from" : "add to"))
                        .predicate(favourite, ItemBuilder::glow)
                        .toItemStack(),
                player -> {
                    heroes.setFavourite(player, !favourite);
                    PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
                    Chat.sendMessage(
                            player,
                            "%s %s %s from your favourites.",
                            (favourite ? "removed" : "added"),
                            hero.getName(),
                            (favourite ? "from" : "to")
                    );
                    update();
                }
        );

        formatDebug();
        fixAbilityItemsCount();
        openInventory();
    }

    private void setAbilityItems(Hero hero) {
        int slot = 0;
        for (Talent talent : hero.getTalentsSorted()) {
            if (talent != null) {
                setItem(ABILITY_SLOTS[slot++], abilityItemOrNull(talent));
            }
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

    private void fixAbilityItemsCount() {
        for (int i = 0, slot = 13, amount = 1; i < 6; i++, slot += (slot % 9 == 6 ? 7 : 1)) {
            final ItemStack item = getInventory().getItem(slot);
            if (item == null) {
                continue;
            }
            item.setAmount(amount++);
        }
    }

    private ItemStack abilityItemOrNull(Talent talent) {
        if (talent == null) {
            return new ItemStack(Material.AIR);
        }
        return talent.getItem();
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
