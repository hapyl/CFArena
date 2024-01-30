package me.hapyl.fight;

import javax.annotation.Nonnull;

public class VersionInfo {

    private final String updateTopic;

    public VersionInfo(String updateTopic) {
        this.updateTopic = updateTopic;
    }

    @Nonnull
    public String getUpdateTopic() {
        return updateTopic;
    }
}
