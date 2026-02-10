package me.hapyl.fight.game.cosmetic;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.inventory.gui.GUIEventListener;
import me.hapyl.eterna.module.inventory.gui.SlotPattern;
import me.hapyl.eterna.module.inventory.gui.SmartComponent;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.entry.CosmeticEntry;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledTexture;
import me.hapyl.fight.gui.styled.profile.PlayerProfileGUI;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CollectionGUI extends StyledGUI implements GUIEventListener {

    public CollectionGUI(Player player) {
        super(player, "Collection", Size.FOUR);

        openInventory();
    }
    
    @Override
    public void onOpen(@Nonnull InventoryOpenEvent event) {
        PlayerLib.playSound(player, Sound.BLOCK_CHEST_OPEN, 1.0f);
    }
    
    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Profile", PlayerProfileGUI::new);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        
        final CosmeticEntry cosmetics = CF.getDatabase(player).cosmeticEntry;
        final SmartComponent component = newSmartComponent();

        setHeader(StyledTexture.ICON_COSMETICS.asIcon());

        for (Type type : Type.values()) {
            final String name = type.getName();
            final Cosmetic selected = cosmetics.getSelected(type);

            component.add(ItemBuilder.of(type.getMaterial(), name)
                    .addSmartLore("&7" + type.getDescription())
                    .addLore()
                    .addLore(Color.SUCCESS.color("Selected: %s".formatted(
                            selected == null ? Color.WARM_GRAY + "None!" : Color.MINT_GREEN + selected.getName()))
                    )
                    .addLore()
                    .addLore(Color.BUTTON + "Click to browse %s!".formatted(name))
                    .asIcon(), player -> new CosmeticGUI(player, type));
        }

        component.apply(this, SlotPattern.CHUNKY, 2);
    }
}
