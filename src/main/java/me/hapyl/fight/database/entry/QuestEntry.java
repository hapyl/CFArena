package me.hapyl.fight.database.entry;

import me.hapyl.eterna.module.player.quest.Quest;
import me.hapyl.eterna.module.player.quest.QuestData;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.quest.CFQuestHandler;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class QuestEntry extends PlayerDatabaseEntry {
    public QuestEntry(@Nonnull PlayerDatabase database) {
        super(database, "quest");
    }

    public boolean hasCompleted(@Nonnull Quest quest) {
        return getCompletedAt(quest) != 0L;
    }

    public long getCompletedAt(@Nonnull Quest quest) {
        return fetchFromDocument(quest.getKeyAsString(), document -> {
            return document.get("completed_at", 0L);
        });
    }

    public void completeQuest(@Nonnull Quest quest) {
        fetchDocument(quest.getKeyAsString(), document -> {
            document.put("completed_at", System.currentTimeMillis());
        });
    }

    public void saveQuests(@Nonnull Set<QuestData> questDataSet) {
        for (QuestData data : questDataSet) {
            final Quest quest = data.getQuest();
            final int currentStage = data.getCurrentStage();
            final double progress = data.getCurrentStageProgress();

            fetchDocument(quest.getKeyAsString(), document -> {
                document.put("stage", currentStage);
                document.put("progress", progress);
            });
        }
    }

    public void resetQuest(@Nonnull Quest quest) {
        setValue(quest.getKeyAsString(), null);
    }

    @Nonnull
    public Set<QuestData> loadQuests() {
        final CFQuestHandler questHandler = CF.getQuestHandler();
        final Set<QuestData> questDataSet = new HashSet<>();

        final Player player = player().orElse(null);

        // Don't load quests for offline players
        if (player == null) {
            return questDataSet;
        }

        for (String stringKey : getDocument().keySet()) {
            fetchDocument(stringKey, document -> {
                // If completedAt exists, don't load
                // Could change to store it later but don't load at runtime
                if (document.containsKey("completed_at")) {
                    return;
                }

                final Key key = Key.ofStringOrNull(stringKey);

                if (key == null) {
                    Debug.warn("Invalid quest key: " + stringKey + ", skipping...");
                    return;
                }

                final Quest quest = questHandler.get(key);

                if (quest == null) {
                    Debug.warn("Tried to load unregistered quest: " + stringKey + ", skipping...");
                    return;
                }

                final int stage = document.get("stage", 0);
                final double progress = document.get("progress", 0.0d);

                questDataSet.add(QuestData.load(questHandler, player, quest, stage, progress));
            });
        }

        return questDataSet;
    }
}
