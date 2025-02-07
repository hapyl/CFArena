package me.hapyl.fight.quest;

import me.hapyl.eterna.module.player.quest.QuestData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledPageGUI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class QuestGUI extends StyledPageGUI<QuestData> {
    public QuestGUI(@Nonnull Player player) {
        super(player, "Quests", Size.FIVE);
    }

    @Nonnull
    @Override
    public ItemStack asItem(@Nonnull Player player, QuestData content, int index, int page) {
        return null;
    }

    @Override
    public void onUpdate() {

    }
}
