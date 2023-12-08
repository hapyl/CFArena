package me.hapyl.fight.game.tutorial;

import me.hapyl.fight.gui.styled.StyledTexture;
import me.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public abstract class TutorialGUI extends PlayerGUI {
    public TutorialGUI(Player player, String name) {
        super(player, "Tutorial %s %s".formatted(ARROW_FORWARD, name), 5);

        updateInventory();

        setItem(40, getButtonIcon(), this::onButtonClick);

        openInventory();
    }

    public void onButtonClick(Player player) {
        new Tutorial(player);
    }

    @Nonnull
    public ItemStack getButtonIcon() {
        return StyledTexture.ARROW_LEFT.asIcon("Go Back", "To Tutorials");
    }

    public abstract void updateInventory();
}
