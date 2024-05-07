package me.hapyl.fight.gui;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.heroes.HeroPlayerItemMaker;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.util.ItemStacks;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.SlotPattern;
import me.hapyl.spigotutils.module.inventory.gui.SmartComponent;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class HeroPreviewGUI extends StyledGUI {

    private final SlotPattern PATTERN = new SlotPattern(new byte[][] {
            { 0, 0, 0, 0, 1, 1, 1, 0, 0 },
            { 0, 0, 0, 0, 1, 0, 1, 0, 0 },
            { 0, 0, 0, 0, 0, 1, 0, 0, 0 }
    });

    private final Heroes enumHero;
    private final Set<Talent> attributeDisplay;
    private final int returnPage;

    public HeroPreviewGUI(Player player, Heroes enumHero, int returnPage) {
        super(player, "Hero Preview - " + enumHero.getHero().getName(), Size.FIVE);
        this.enumHero = enumHero;
        this.attributeDisplay = Sets.newHashSet();
        this.returnPage = returnPage;

        openInventory();
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Hero Selection", player -> new HeroSelectGUI(player, returnPage), 2);
    }

    @Override
    public void onUpdate() {
        final Hero hero = enumHero.getHero();

        setHeader(hero.getItemMaker().makeItem(HeroPlayerItemMaker.Type.DETAILS, player));

        // Fill with panels
        fillColumn(0, ItemStacks.BLACK_BAR);
        fillColumn(8, ItemStacks.BLACK_BAR);

        // Set talents
        final SmartComponent component = new SmartComponent();

        hero.getTalentsSorted().forEach(talent -> {
            if (talent == null) {
                return;
            }

            // Display attributes
            if (attributeDisplay.contains(talent)) {
                component.add(talentAttributeOrAir(talent), player -> {
                    attributeDisplay.remove(talent);

                    plingAndUpdate();
                });
                return;
            }

            // Talent
            if (talent.isDisplayAttributes()) {
                component.add(talentItemOrAir(talent), player -> {
                    attributeDisplay.add(talent);

                    plingAndUpdate();
                });
            }
            else {
                component.add(talentItemOrAir(talent));
            }
        });

        component.apply(this, SlotPattern.DEFAULT, 2);
        fixTalentItemsCount();

        // Weapon
        final Weapon weapon = hero.getWeapon();
        setItem(30, weapon.getItem());

        // Ultimate
        final UltimateTalent ultimate = hero.getUltimate();
        final boolean showingUltimateAttributes = attributeDisplay.contains(ultimate);

        setItem(32, showingUltimateAttributes ? talentAttributeOrAir(ultimate) : talentItemOrAir(ultimate));

        if (showingUltimateAttributes) {
            setClick(32, player -> {
                attributeDisplay.remove(ultimate);

                plingAndUpdate();
            });
        }
        else {
            setClick(32, player -> {
                attributeDisplay.add(ultimate);

                plingAndUpdate();
            });
        }

        // Skins
        setItem(
                26,
                new ItemBuilder(Material.LEATHER_CHESTPLATE)
                        .setName("Skins")
                        .addLore()
                        .addLore("Change the appearance of this hero!")
                        .addLore()
                        .addLore(Color.BUTTON + "Click to open skin GUI!")
                        .asIcon(),
                player -> {
                    new SkinGUI(player, enumHero, returnPage);
                }
        );

        // Favourite
        final boolean favourite = enumHero.isFavourite(getPlayer());

        setItem(
                35,
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
                    enumHero.setFavourite(player, !favourite);

                    Chat.sendMessage(
                            player,
                            "&a%s %s %s from your favourites.".formatted(
                                    (favourite ? "Removed" : "Added"),
                                    hero.getName(),
                                    (favourite ? "from" : "to")
                            )
                    );

                    plingAndUpdate();
                }
        );

        // Global stats
        setItem(
                51,
                ItemBuilder.of(Material.CREEPER_BANNER_PATTERN, "Global Statistics")
                        .addLore()
                        .addSmartLore("View global statistics of this hero, such as playtime, kills, deaths, etc.")
                        .addLore()
                        .addLore(Color.BUTTON + "Click to view!")
                        .asIcon(),
                player -> new HeroStatisticGUI(player, enumHero, returnPage)
        );
    }

    private void plingAndUpdate() {
        PlayerLib.plingNote(player, 2.0f);
        update();
    }

    @Nonnull
    private ItemStack talentAttributeOrAir(Talent talent) {
        if (talent == null) {
            return ItemStacks.AIR;
        }

        return new ItemBuilder(talent.getItemAttributes())
                .addLore()
                .addLore(Color.BUTTON + "Click to hide details")
                .asIcon();
    }

    @Nonnull
    private ItemStack talentItemOrAir(Talent talent) {
        if (talent == null) {
            return ItemStacks.AIR;
        }

        final boolean isDisplayAttributes = talent.isDisplayAttributes();

        return new ItemBuilder(talent.getItem())
                .addLoreIf("", isDisplayAttributes)
                .addLoreIf(Color.BUTTON + "Click for details", isDisplayAttributes)
                .asIcon();
    }

    private void fixTalentItemsCount() {
        for (int i = 0, slot = 19, amount = 1; i < 6; i++, slot++) {
            final ItemStack item = getInventory().getItem(slot);
            if (item == null) {
                continue;
            }
            item.setAmount(amount++);
        }
    }

}
