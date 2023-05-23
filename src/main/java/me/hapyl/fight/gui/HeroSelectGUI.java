package me.hapyl.fight.gui;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.heroes.ComplexHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.Origin;
import me.hapyl.fight.util.ItemStacks;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.Locale;

public class HeroSelectGUI extends PlayerGUI {

    private final int guiFitSize = 21;

    public HeroSelectGUI(Player player) {
        this(player, 0);
    }

    public HeroSelectGUI(Player player, int start) {
        super(player, "Hero Selection", 5);
        update(start);
    }

    private void update(int start) {
        clearEverything();
        final Player player = getPlayer();
        final List<Heroes> list = Heroes.playableRespectLockedFavourites(player);

        // add previous page button
        if (start >= guiFitSize) {
            setItem(38, ItemStacks.ARROW_PREV_PAGE, (pl) -> update(start - 21));
        }

        // add next page button
        if (list.size() - start > guiFitSize) {
            setItem(42, ItemStacks.ARROW_NEXT_PAGE, (pl) -> update(start + guiFitSize));
        }

        setCloseMenuItem(40);

        for (int i = start, slot = 10; i < start + guiFitSize; i++, slot += slot % 9 == 7 ? 3 : 1) {
            if (i >= list.size()) {
                break;
            }

            // if (hero == null) { slot -= slot % 9 == 7 ? 3 : 1; continue; }

            final Heroes enumHero = list.get(i);
            final Hero hero = enumHero.getHero();

            if (enumHero.isLocked(player)) {
                setItem(slot, ItemBuilder.of(Material.COAL, "&c???", "&8Locked!")
                                .addLore()
                                .addLore("&7Reach level &b%s &7to unlock!", hero.getMinimumLevel())
                                .asIcon(),
                        click -> {
                        }
                );
            }
            else {
                final ItemBuilder builder = new ItemBuilder(hero.getItem())
                        .setName("&a" + hero.getName())
                        .addLore("&8/hero " + enumHero.name().toLowerCase(Locale.ROOT))
                        .addLore()
                        .addLore("&7Role: &b%s", hero.getRole().getName())
                        .addLoreIf("&7Origin: &b%s".formatted(hero.getOrigin().getName()), hero.getOrigin() != Origin.NOT_SET)
                        .addLore();

                final HeroAttributes attributes = hero.getAttributes();
                builder.addLore("&6Attributes: ");
                builder.addLore(attributes.getLore(AttributeType.HEALTH));
                builder.addLore(attributes.getLore(AttributeType.ATTACK));
                builder.addLore(attributes.getLore(AttributeType.DEFENSE));
                builder.addLore(attributes.getLore(AttributeType.SPEED));
                builder.addLore("&eSee details for more!");

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

                setItem(slot, builder.asIcon());
                setClick(
                        slot,
                        pl -> Manager.current().setSelectedHero(player, enumHero),
                        ClickType.LEFT,
                        ClickType.SHIFT_LEFT
                );
                setClick(slot, pl -> new HeroPreviewGUI(player, enumHero, start), ClickType.RIGHT, ClickType.SHIFT_RIGHT);
            }
        }

        openInventory();
    }
}
