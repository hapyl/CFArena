package me.hapyl.fight.quest;

import me.hapyl.eterna.module.player.quest.Quest;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;

import javax.annotation.Nonnull;

public class CFQuest extends Quest {

    private static final CFQuestFormatter FORMATTER;

    static {
        FORMATTER = new CFQuestFormatter();
    }

    public CFQuest(@Nonnull Key key) {
        super(CF.getPlugin(), key);

        setFormatter(FORMATTER);
    }
}
