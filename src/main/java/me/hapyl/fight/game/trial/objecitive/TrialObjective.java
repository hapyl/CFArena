package me.hapyl.fight.game.trial.objecitive;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.util.Described;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.game.trial.Trial;
import me.hapyl.fight.util.Lifecycle;
import me.hapyl.fight.util.SingleEventHandler;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.List;

public class TrialObjective implements Lifecycle, Described, Ticking, SingleEventHandler {

    public final Trial trial;

    private final String name;
    private final String description;

    private TrialObjectivePath path;

    public TrialObjective(Trial trial, String name, String description) {
        this.trial = trial;
        this.name = name;
        this.description = description;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onStart() {
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onStop() {
        if (path != null) {
            path.onStop();
        }
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void tick() {
        if (path != null) {
            path.tick();
        }
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }

    @Nonnull
    public List<String> getDescriptionSplit(int limit) {
        return ItemBuilder.splitString(description, limit);
    }

    @Nonnull
    public String[] getScoreboardStrings() {
        return new String[] { description };
    }

    @Override
    public <T extends Event> void handle(@Nonnull T ev) {
    }

    @Nullable
    public TrialObjectivePath getPath() {
        return path;
    }

    protected void setPath(@Nonnull TrialObjectivePath path) {
        this.path = path;
    }

}
