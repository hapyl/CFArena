package me.hapyl.fight.fastaccess;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.inventory.gui.Filter;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledPageGUI;
import me.hapyl.fight.registry.Registries;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class FastAccessGUI extends StyledPageGUI<FastAccess> {

    private final PlayerProfile profile;
    private final PlayerFastAccess playerFastAccess;
    private final Filter<FastAccess, Category> filter;
    private final int slot;

    public FastAccessGUI(PlayerProfile profile, int slot) {
        super(profile.getPlayer(), "Modify Quick Access [" + (slot + 1) + "]", Size.FIVE);

        this.profile = profile;
        this.playerFastAccess = profile.getFastAccess();
        this.filter = new Filter<>(Category.class) {
            @Override
            public boolean isKeep(@Nonnull FastAccess fastAccess, @Nonnull Category category) {
                return fastAccess.getCategory() == category;
            }
        };
        this.slot = slot;

        updateContents();

        // sticky item fix
        GameTask.runLater(this::update, 1);
    }

    @Nonnull
    @Override
    public ItemStack asItem(Player player, FastAccess content, int index, int page) {
        return content.create(player).addLore().addLore(Color.BUTTON + "Click to set!").asIcon();
    }

    @Override
    public void onClick(@Nonnull Player player, @Nonnull FastAccess content, int index, int page, @Nonnull ClickType clickType) {
        playerFastAccess.setFastAccess(slot, content);
        closeInventory();
    }

    @Override
    public void onUpdate() {
        setHeader(new ItemBuilder(Material.PAINTING).setName("Quick Access")
                .addLore()
                .addSmartLore("Modify quick access to your likings!")
                .asIcon());

        // Sort button
        filter.setFilterItem(this, 48, (player, category) -> {
            updateContents();
            update();
        });

        // Remove item
        setPanelItem(5, new ItemBuilder(Material.REDSTONE_BLOCK)
                .setName("&cReset Quick Access")
                .addLore()
                .addSmartLore("Reset the quick access to default, removing any action assigned to it.")
                .addLore()
                .addLore(Color.ERROR + "Click to reset!")
                .asIcon(), player -> {
            playerFastAccess.setFastAccess(slot, null);
            closeInventory();
        });
    }

    private void updateContents() {
        setContents(filter.filter(Registries.getFastAccess().values(player)));
    }
}
