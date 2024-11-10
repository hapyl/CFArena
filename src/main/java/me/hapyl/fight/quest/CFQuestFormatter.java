package me.hapyl.fight.quest;

import me.hapyl.eterna.module.player.quest.Quest;
import me.hapyl.eterna.module.player.quest.QuestFormatter;
import me.hapyl.eterna.module.player.quest.QuestObjective;
import me.hapyl.eterna.module.player.quest.QuestPreRequirement;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class CFQuestFormatter implements QuestFormatter {
    @Override
    public void sendObjectiveNew(@Nonnull Player player, @Nonnull QuestObjective objective) {

    }

    @Override
    public void sendObjectiveComplete(@Nonnull Player player, @Nonnull QuestObjective objective) {

    }

    @Override
    public void sendObjectiveFailed(@Nonnull Player player, @Nonnull QuestObjective objective) {

    }

    @Override
    public void sendQuestStartedFormat(@Nonnull Player player, @Nonnull Quest quest) {

    }

    @Override
    public void sendQuestCompleteFormat(@Nonnull Player player, @Nonnull Quest quest) {

    }

    @Override
    public void sendQuestFailedFormat(@Nonnull Player player, @Nonnull Quest quest) {

    }

    @Override
    public void sendCannotStartQuestAlreadyCompleted(@Nonnull Player player, @Nonnull Quest quest) {

    }

    @Override
    public void sendPreRequirementNotMet(@Nonnull Player player, @Nonnull QuestPreRequirement preRequirement) {

    }

    @Override
    public void sendCannotStartQuestAlreadyStarted(@Nonnull Player player, @Nonnull Quest quest) {
    }
}
