package me.hapyl.fight.fastaccess;

import me.hapyl.fight.Message;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledPageGUI;
import me.hapyl.fight.gui.styled.StyledTexture;
import me.hapyl.fight.registry.Registries;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public class FastAccessGUI extends StyledPageGUI<FastAccess> implements IFastAccess {

    private final PlayerProfile profile;
    private final PlayerFastAccess playerFastAccess;
    private final int slot;

    public FastAccessGUI(@Nonnull PlayerProfile profile, int slot, @Nonnull Category category) {
        super(profile.getPlayer(), IFastAccess.guiName(slot), Size.FIVE);

        this.profile = profile;
        this.playerFastAccess = profile.getFastAccess();
        this.slot = slot;

        // Set contents
        final List<FastAccess> fastAccess = Registries.getFastAccess().values(player);
        fastAccess.removeIf(access -> access.getCategory() != category);

        setContents(fastAccess);

        // sticky item fix
        GameTask.runLater(this::update, 1);
    }

    @Nonnull
    @Override
    public ItemStack asItem(@Nonnull Player player, FastAccess content, int index, int page) {
        return content.create(player).addLore().addLore(Color.BUTTON + "Click to set!").asIcon();
    }

    @Override
    public void onClick(@Nonnull Player player, @Nonnull FastAccess content, int index, int page, @Nonnull ClickType clickType) {
        playerFastAccess.setFastAccess(slot, content);
        closeInventory();

        // Fx
        Message.success(player, "Set Quick Access slot {%s} to {%s}!".formatted(slot, content.getName()));
        Message.sound(player, Sound.ENTITY_VILLAGER_YES, 1.0f);
    }

    @Override
    public void onUpdate() {
        // Category select
        setPanelItem(
                1, StyledTexture.ARROW_LEFT.asButton("Select Category", "return to category selection"), player -> new FastAccessCategoryGUI(profile, slot)
        );

        setDefaultSlots();
    }

    @Override
    public int slot() {
        return slot;
    }

}
