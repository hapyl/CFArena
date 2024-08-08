package me.hapyl.fight.game.cosmetic.gui;

import me.hapyl.fight.CF;
import me.hapyl.fight.database.entry.CosmeticEntry;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledPageGUI;
import me.hapyl.fight.ux.Notifier;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.inventory.gui.GUI;
import me.hapyl.eterna.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CosmeticGUI extends StyledPageGUI<Cosmetics> {

    private final Type type;

    public CosmeticGUI(Player player, Type type) {
        super(player, "Cosmetic " + GUI.ARROW_FORWARD + " " + type.getName(), Size.FOUR);
        this.type = type;

        final List<Cosmetics> cosmetics = Cosmetics.getByType(type);

        // Don't show unobtainable cosmetics unless magically selected
        cosmetics.removeIf(enumCosmetic -> {
            final Cosmetic cosmetic = enumCosmetic.getCosmetic();

            if (cosmetic.isExclusive()) {
                return !enumCosmetic.isUnlocked(player);
            }

            return false;
        });

        // Sort by owned
        cosmetics.sort((a, b) -> {
            if (a.isUnlocked(getPlayer()) && !b.isUnlocked(getPlayer())) {
                return -1;
            }
            else if (!a.isUnlocked(getPlayer()) && b.isUnlocked(getPlayer())) {
                return 1;
            }
            return 0;
        });

        setContents(cosmetics);
        setFit(Fit.SLIM);

        openInventory(1);
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Collection", CollectionGUI::new);
    }

    @Override
    public void onUpdate() {
        setHeader(ItemBuilder.of(type.getMaterial(), type.getName(), type.getDescription()).asIcon());

        final Cosmetics selected = Cosmetics.getSelected(player, type);

        // Unequip Button
        if (selected == null) {
            setPanelItem(7, ItemBuilder.of(Material.GRAY_DYE, "Unequip", "Nothing is equipped!").asIcon());
        }
        else {
            setPanelItem(7, ItemBuilder.of(Material.LIGHT_BLUE_DYE, "Unequip")
                            .addLore()
                            .addLore("Currently Selected")
                            .addLore("&a&l " + selected.getCosmetic().getName())
                            .addLore()
                            .addLore(Color.BUTTON + "Click to unequip!")
                            .asIcon(),
                    player -> {
                        final CosmeticEntry entry = CF.getDatabase(player).cosmeticEntry;

                        entry.unsetSelected(type);
                        update();

                        // Fx
                        PlayerLib.playSound(player, Sound.ITEM_ARMOR_EQUIP_LEATHER, 0.0f);
                    }
            );
        }
    }

    @Nonnull
    @Override
    public ItemStack asItem(Player player, Cosmetics content, int index, int page) {
        final Cosmetic cosmetic = content.getCosmetic();
        final ItemBuilder builder = cosmetic.createItem(player);

        // Check if player has the cosmetic
        if (content.isUnlocked(player)) {
            // Check if it's selected
            if (content.isSelected(player)) {
                builder.addLore();
                builder.addLore(Color.SUCCESS.bold() + "Selected!");
                builder.addLore(Color.BUTTON + "Click to deselect this cosmetic.");
                builder.glow();
            }
            else {
                builder.addLore();
                builder.addLore(Color.SUCCESS_DARKER.bold() + "Unlocked!");
                builder.addLore(Color.BUTTON + "Click to select this cosmetic.");
            }
        }
        else {
            builder.addLore();
            builder.addLore(Color.ERROR.bold() + "Locked!");
        }

        return builder.asIcon();
    }

    @Override
    public void onClick(@Nonnull Player player, @Nonnull Cosmetics content, int index, int page, @Nonnull ClickType clickType) {
        final Cosmetic cosmetic = content.getCosmetic();

        if (!content.isUnlocked(player)) {
            Notifier.error(player, "This cosmetic is locked!");
            PlayerLib.villagerNo(player);
            return;
        }

        if (content.isSelected(player)) {
            content.deselect(player);
            Notifier.success(player, Color.ERROR + "Deselected {Cosmetic}!", cosmetic.getName());
        }
        else {
            content.select(player);
            Notifier.success(player, "Selected {Cosmetic} as {Type}!", cosmetic.getName(), cosmetic.getType().getName());
        }

        PlayerLib.plingNote(player, 2.0f);
        update();
    }

}
