package me.hapyl.fight.gui.styled.profile.achievement;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.achievement.Achievement;
import me.hapyl.fight.game.achievement.AchievementRegistry;
import me.hapyl.fight.game.achievement.Category;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledItem;
import me.hapyl.fight.gui.styled.StyledPageGUI;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;

public class AchievementGeneralGUI extends StyledPageGUI<Achievement> {

    private final AchievementRegistry registry;

    public AchievementGeneralGUI(Player player) {
        super(player, "Achievements (General)", Size.FIVE);

        registry = Main.getPlugin().getAchievementRegistry();

        final LinkedList<Achievement> achievements = registry.byCategory(Category.GAMEPLAY);

        setContents(achievements);
        openInventory(1);
    }

    @Nonnull
    @Override
    public ItemStack asItem(Player player, Achievement content, int index, int page) {
        final boolean isComplete = content.isComplete(player);

        final ItemBuilder builder = ItemBuilder.of(isComplete ? Material.DIAMOND : Material.COAL);
        builder.setAmount(content.getPointReward());

        content.format(player, builder);

        if (isComplete) {
            builder.addLore();
            builder.addLore("&a&lCOMPLETED!");
            builder.addLore(Color.DEFAULT + content.getCompletedAtFormatted(player));
        }

        return builder.build();
    }

    @Override
    public void onUpdate() {
        setHeader(StyledItem.ICON_ACHIEVEMENTS_GENERAL.asIcon());
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Achievements", AchievementGUI::new);
    }
}
