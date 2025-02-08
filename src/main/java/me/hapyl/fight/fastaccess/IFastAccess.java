package me.hapyl.fight.fastaccess;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.gui.styled.Styled;
import me.hapyl.fight.gui.styled.StyledTexture;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public interface IFastAccess extends Styled {

    int slot();

    default void setDefaultSlots() {
        setHeader(new ItemBuilder(Material.PAINTING)
                .setName("Quick Access")
                .addLore()
                .addSmartLore("Modify quick access to your likings!")
                .asIcon());

        setPanelItem(
                5,
                StyledTexture.TNT
                        .toBuilder()
                        .setName("&cReset Quick Access")
                        .addLore()
                        .addSmartLore("Reset the quick access to default, removing any action assigned to it.")
                        .addLore()
                        .addLore(Color.ERROR + "Click to reset!")
                        .asIcon(),
                player -> {
                    final int slot = slot();

                    CF.getProfile(player).getFastAccess().setFastAccess(slot, null);
                    player.closeInventory();

                    // Fx
                    Message.success(player, "Reset Quick Access for slot {%s}!".formatted(slot));
                    Message.sound(player, Sound.ENTITY_WOLF_WHINE, 1.0f);
                    Message.sound(player, Sound.ENTITY_GENERIC_EXPLODE, 1.25f);
                }
        );
    }

    @Nonnull
    static String guiName(int slot) {
        return "Modify Quick Access [%s]".formatted(slot + 1);
    }

}
