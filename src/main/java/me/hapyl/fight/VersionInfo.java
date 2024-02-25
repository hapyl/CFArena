package me.hapyl.fight;

import me.hapyl.fight.util.CFUtils;

import javax.annotation.Nonnull;

public class VersionInfo {

    private final UpdateTopic[] updateTopic;

    public VersionInfo(@Nonnull UpdateTopic... topics) {
        this.updateTopic = CFUtils.requireVarArgs(topics);
    }

    @Nonnull
    public UpdateTopic[] getUpdateTopic() {
        return updateTopic;
    }

}
