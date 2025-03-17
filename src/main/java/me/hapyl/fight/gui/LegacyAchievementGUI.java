package me.hapyl.fight.gui;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.inventory.gui.PlayerPageGUI;
import me.hapyl.eterna.module.inventory.gui.SlotPattern;
import me.hapyl.eterna.module.inventory.gui.SmartComponent;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.fight.Message;
import me.hapyl.fight.game.achievement.Achievement;
import me.hapyl.fight.game.achievement.AchievementRegistry;
import me.hapyl.fight.game.achievement.Category;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.util.ItemStacks;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.LinkedList;

public class LegacyAchievementGUI extends PlayerPageGUI<Achievement> {

    private final AchievementRegistry registry;
    private Category category;

    public LegacyAchievementGUI(Player player) {
        this(player, Category.GAMEPLAY);
    }

    public LegacyAchievementGUI(Player player, Category category) {
        super(player, "Achievements", 6);

        registry = Registries.achievements();

        Message.error(player, "&lKeep in mind this is a legacy GUI, and it will not be updated or/and fixed!");

        setCategory(category);
        openInventory(1);
    }

    @Override
    public void postProcessInventory(Player player, int page) {
        fillItem(0, 8, ItemStacks.BLACK_BAR);
        fillItem(45, 53, ItemStacks.BLACK_BAR);

        setPreviousPageSlot(47);
        setNextPageSlot(51);
        setCloseMenuItem(49);

        // Update header
        final SmartComponent component = newSmartComponent();

        for (Category value : Category.values()) {
            final boolean currentCategory = value == category;

            component.add(new ItemBuilder(Material.STONE).setName(value.getName())
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
        builder.setAmount(achievement.getPointReward());

        // Format
        builder.setName(achievement.getName());
        builder.addLore();
        builder.addSmartLore(achievement.getDescription());

        return builder.asIcon();
    }

}
