package me.hapyl.fight.quest;

import me.hapyl.eterna.module.player.quest.Quest;
import me.hapyl.eterna.module.player.quest.QuestData;
import me.hapyl.eterna.module.player.quest.QuestHandler;
import me.hapyl.fight.CF;
import me.hapyl.fight.registry.Registries;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.Set;

public final class CFQuestHandler extends QuestHandler {
    public CFQuestHandler(@Nonnull JavaPlugin plugin) {
        super(plugin);

        // Register quests
        Registries.getNPCs().THE_EYE.registerQuests(this);
        Registries.getNPCs().STORE_OWNER.registerQuests(this);
    }

    @Override
    public void saveQuests(@Nonnull Player player, @Nonnull Set<QuestData> questDataSet) {
        CF.getDatabase(player).questEntry.saveQuests(questDataSet);
    }

    @Nonnull
    @Override
    public Set<QuestData> loadQuests(@Nonnull Player player) {
        return CF.getDatabase(player).questEntry.loadQuests();
    }

    @Override
    public boolean hasCompleted(@Nonnull Player player, @Nonnull Quest quest) {
        return CF.getDatabase(player).questEntry.hasCompleted(quest);
    }

    @Override
    public void completeQuest(@Nonnull Player player, @Nonnull Quest quest) {
        CF.getDatabase(player).questEntry.completeQuest(quest);
    }
}
