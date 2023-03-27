package me.hapyl.fight.gui;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.heroes.ComplexHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.util.ItemStacks;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
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
        final List<Heroes> list = Heroes.playableRespectFavourites(player);

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

            final Heroes hero = list.get(i);
            final Hero heroClass = hero.getHero();
            final boolean isFavourite = hero.isFavourite(player);

            if (hero.isLocked(player)) {
                // TODO: 023, Mar 23, 2023 -> impl locked heroes
            }

            final ItemBuilder builder = new ItemBuilder(heroClass.getItem())
                    .setName("&a" + hero.getHero().getName())
                    .addLore("&8/hero " + hero.name().toLowerCase(Locale.ROOT))
                    .addLore()
                    .addLore("&7Role: &b%s", hero.getHero().getRole().getName())
                    .addLore()
                    .addSmartLore(heroClass.getAbout(), "&7&o", 35);

            if (heroClass instanceof ComplexHero) {
                builder.addLore();
                builder.addLore("&6&lComplex Hero!");
                builder.addSmartLore(
                        "This hero is more difficult to play than others. Thus is &nnot&e&o recommended for newer players.",
                        "&e&o"
                );
            }

            // usage lore
            builder.addLore().addLore("&e&lLEFT CLICK &7to select").addLore("&e&lRIGHT CLICK &7for details");

            setItem(slot, builder.predicate(isFavourite, ItemBuilder::glow).toItemStack());
            setClick(
                    slot,
                    pl -> Manager.current().setSelectedHero(player, hero),
                    ClickType.LEFT,
                    ClickType.SHIFT_LEFT
            );
            setClick(slot, pl -> new HeroPreviewGUI(player, hero, start), ClickType.RIGHT, ClickType.SHIFT_RIGHT);
        }

        openInventory();
    }
}
