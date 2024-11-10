package me.hapyl.fight.gui.styled.profile.achievement;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.game.achievement.Achievement;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledPageGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class AchievementAbstractGUI extends StyledPageGUI<Achievement> {

    public AchievementAbstractGUI(Player player, String type, List<Achievement> achievements) {
        super(player, "Achievements (%s)".formatted(type), Size.FIVE);

        setContents(achievements);
        openInventory(1);
    }

    @Nonnull
    @Override
    public final ItemStack asItem(@Nonnull Player player, Achievement content, int index, int page) {
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

    @Nonnull
    public abstract ItemStack headerItem();

    @Override
    public final void onUpdate() {
        setHeader(headerItem());
    }

    @Nonnull
    @Override
    public final ReturnData getReturnData() {
        return ReturnData.of("Achievements", AchievementGUI::new);
    }
}
