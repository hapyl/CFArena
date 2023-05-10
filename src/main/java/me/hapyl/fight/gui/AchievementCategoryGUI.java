package me.hapyl.fight.gui;

import me.hapyl.fight.game.achievement.Achievement;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.achievement.Category;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.PlayerPageGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.LinkedList;

public class AchievementCategoryGUI extends PlayerPageGUI<Achievements> {

    private final Category category;

    public AchievementCategoryGUI(Player player, Category category) {
        super(player, "Achievements", 6);

        this.category = category;

        final LinkedList<Achievements> achievements = Achievements.byCategory(category);

        // remove hidden non-complete achievement
        achievements.removeIf(achievement -> {
            return achievement.isHidden() && !achievement.isCompleted(player);
        });

        achievements.sort((a, b) -> {
            if (a.isCompleted(player) && !b.isCompleted(player)) {
                return -1;
            }
            else if (!a.isCompleted(player) && b.isCompleted(player)) {
                return 1;
            }
            return 0;
        });

        setContents(achievements);
        openInventory(1);
    }

    @Nonnull
    @Override
    public ItemStack asItem(Player player, Achievements enumAchievement, int index, int page) {
        final int completeCount = enumAchievement.getCompleteCount(player);
        final boolean completed = enumAchievement.isCompleted(player);

        final ItemBuilder builder = new ItemBuilder(completed ? Material.DIAMOND : Material.COAL);

        final Achievement achievement = enumAchievement.getAchievement();
        builder.setName(achievement.getName());
        builder.addLore();
        builder.addSmartLore(achievement.getDescription());

        // Rewards
        builder.addLore();
        builder.addLore("&7Rewards:");
        achievement.getRewards().forEach((requirement, reward) -> reward.display(player, builder));

        // TODO (hapyl): 020, Apr 20, 2023: Add progress for ProgressAchievements

        if (enumAchievement.isHidden() && !completed) {
            builder.setName("&7???");
            builder.setLore("&8???");
        }

        return builder.asIcon();
    }

    @Override
    public void onClick(Player player, Achievements achievements, int index, int page) {

    }

}
