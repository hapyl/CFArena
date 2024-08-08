package me.hapyl.fight.gui.styled.profile.achievement;

import com.google.common.collect.Lists;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.achievement.AchievementRegistry;
import me.hapyl.fight.game.achievement.Category;
import me.hapyl.fight.game.achievement.Tier;
import me.hapyl.fight.game.achievement.TieredAchievement;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledTexture;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.util.BFormat;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.LinkedList;

// This definitely cannot be a PageGUI.
public class AchievementTieredGUI extends StyledGUI {

    private static final int ACHIEVEMENTS_PER_PAGE = 7;

    private final LinkedList<TieredAchievement> achievements;
    private final int maxPages;

    private int page;

    public AchievementTieredGUI(Player player) {
        super(player, "Achievements (Tiered)", Size.FIVE);

        final AchievementRegistry registry = Main.getPlugin().getAchievementRegistry();
        page = 1;

        achievements = Lists.newLinkedList();
        registry.byCategory(Category.TIERED).forEach(achievement -> {
            if (!(achievement instanceof TieredAchievement tieredAchievement)) {
                throw new IllegalArgumentException("%s is not a tiered achievement but assigned as such!".formatted(achievement.getId()));
            }

            achievements.add(tieredAchievement);
        });

        maxPages = achievements.size() / ACHIEVEMENTS_PER_PAGE + 1;
        openInventory();
    }

    @Override
    public void onUpdate() {
        // Page arrows
        if (page > 1) {
            setItem(
                    47,
                    StyledTexture.ARROW_LEFT.asIcon("&aPrevious Page", Color.BUTTON + "Click to open the previous page!"),
                    pl -> {
                        page++;
                        update();
                    }
            );
        }
        else if (page < maxPages) {
            setItem(
                    51,
                    StyledTexture.ARROW_RIGHT.asIcon("&aNext Page", Color.BUTTON.bold() + "Click to open the next page!"),
                    pl -> {
                        page--;
                        update();
                    }
            );
        }

        int slot = 1;
        for (int i = 0; i < ACHIEVEMENTS_PER_PAGE; i++, slot++) {
            final int index = (page - 1) * ACHIEVEMENTS_PER_PAGE + i;

            if (index < 0 || index >= achievements.size()) {
                break;
            }

            final TieredAchievement achievement = achievements.get(index);
            final Tier[] tiers = achievement.getTiers();
            final int completeCount = achievement.getCompleteCount(player);

            int tierSlot = slot + (9 * 4);

            for (Tier tier : tiers) {
                final int numericTier = tier.getTier();
                final boolean lastTier = tier == tiers[tiers.length - 1];
                final boolean isTierComplete = completeCount >= numericTier;

                final ItemBuilder builder = lastTier
                        ? (isTierComplete ? StyledTexture.ACHIEVEMENT_TIERED_COMPLETE : StyledTexture.ACHIEVEMENT_TIERED_INCOMPLETE).toBuilder()
                        : (isTierComplete ? ItemBuilder.of(Material.LIME_STAINED_GLASS_PANE) : ItemBuilder.of(Material.RED_STAINED_GLASS_PANE)
                );

                builder.setAmount(tier.getIndex() + 1);
                builder.setName(achievement.getName() + " " + tier.getRoman());
                builder.addLore();
                builder.addSmartLore(BFormat.format(achievement.getDescription(), "&f" + numericTier + "&7"));
                builder.addLore();
                builder.addLore("&b&lTIER REWARD:" + achievement.checkmark(isTierComplete));
                builder.addLore(achievement.formatPointReward(tier.getReward()));
                builder.addLore();

                if (isTierComplete) {
                    builder.addLore("&a&lTIER COMPLETE!");
                }
                else {
                    builder.addLore("&a&lPROGRESS:");
                    builder.addLore("&c%s&7/&a%s &8%.2f%%".formatted(
                            completeCount,
                            numericTier,
                            ((float) completeCount / (float) numericTier * (float) 100)
                    ));
                }

                setItem(tierSlot, builder.asIcon());
                tierSlot -= 9;
            }
        }
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Achievements", AchievementGUI::new);
    }

}
