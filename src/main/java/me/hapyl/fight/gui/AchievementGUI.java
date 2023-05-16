package me.hapyl.fight.gui;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.achievement.Achievement;
import me.hapyl.fight.game.achievement.AchievementRegistry;
import me.hapyl.fight.game.achievement.Category;
import me.hapyl.fight.util.ItemStacks;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.PlayerPageGUI;
import me.hapyl.spigotutils.module.inventory.gui.SlotPattern;
import me.hapyl.spigotutils.module.inventory.gui.SmartComponent;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.LinkedList;

public class AchievementGUI extends PlayerPageGUI<Achievement> {

    private final AchievementRegistry registry;
    private Category category;

    public AchievementGUI(Player player) {
        this(player, Category.GAMEPLAY);
    }

    public AchievementGUI(Player player, Category category) {
        super(player, "Achievements", 6);

        registry = Main.getPlugin().getAchievementRegistry();

        setCategory(category);
        openInventory(1);
    }

    @Override
    public void postProcessInventory(Player player, int page) {
        fillItem(0, 8, ItemStacks.BLACK_BAR);

        // Update header
        final SmartComponent component = newSmartComponent();

        for (Category value : Category.values()) {
            final boolean currentCategory = value == category;

            component.add(new ItemBuilder(value.getMaterial()).setName(value.getName())
                    .addLore()
                    .addSmartLore(value.getDescription())
                    .predicate(currentCategory, ItemBuilder::glow)
                    .addLore()
                    .addLoreIf("&aCurrently selected!", currentCategory)
                    .addLoreIf("&eClick to select!", !currentCategory)
                    .asIcon(), click -> {
                if (currentCategory) {
                    return;
                }

                setCategory(value);
                openInventory(1);

                // Fx
                PlayerLib.playSound(player, Sound.ITEM_BOOK_PAGE_TURN, 1.0f);
            });
        }

        component.apply(this, SlotPattern.DEFAULT, 0);
    }

    public void setCategory(Category category) {
        this.category = category;
        final LinkedList<Achievement> achievements = registry.byCategory(category);

        // remove hidden non-complete achievement
        achievements.removeIf(achievement -> achievement.isHidden() && !achievement.hasCompletedAtLeastOnce(player));

        achievements.sort((a, b) -> {
            if (a.hasCompletedAtLeastOnce(player) && !b.hasCompletedAtLeastOnce(player)) {
                return -1;
            }
            else if (!a.hasCompletedAtLeastOnce(player) && b.hasCompletedAtLeastOnce(player)) {
                return 1;
            }
            return 0;
        });

        // Update contents
        setContents(achievements);
    }

    @Nonnull
    @Override
    public ItemStack asItem(Player player, Achievement achievement, int index, int page) {
        final int completeCount = achievement.getCompleteCount(player);
        final boolean completed = achievement.hasCompletedAtLeastOnce(player);

        final ItemBuilder builder = new ItemBuilder(completed ? Material.DIAMOND : Material.COAL);

        // Set count
        builder.setAmount(achievement.getTier(completeCount));

        // Name and Lore
        builder.setName(achievement.getName());
        builder.addLore();
        builder.addSmartLore(achievement.getDescription());

        // Format
        achievement.format(player, builder);

        return builder.asIcon();
    }

    @Override
    public void onClick(Player player, Achievement achievement, int index, int page) {
    }

}
