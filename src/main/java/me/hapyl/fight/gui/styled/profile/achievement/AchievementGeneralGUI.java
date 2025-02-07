package me.hapyl.fight.gui.styled.profile.achievement;

import me.hapyl.fight.game.achievement.Category;
import me.hapyl.fight.gui.styled.StyledItem;
import me.hapyl.fight.registry.Registries;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class AchievementGeneralGUI extends AchievementAbstractGUI {
    public AchievementGeneralGUI(Player player) {
        super(player, "General", Registries.getAchievements().byCategory(Category.GAMEPLAY));
    }

    @Nonnull
    @Override
    public ItemStack headerItem() {
        return StyledItem.ICON_ACHIEVEMENTS_GENERAL.asIcon();
    }
}
