package me.hapyl.fight.script;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.Event;
import me.hapyl.fight.registry.EnumId;
import me.hapyl.fight.registry.Identified;
import me.hapyl.fight.script.action.ScriptActionBuilder;

import javax.annotation.Nonnull;
import java.util.LinkedList;

public class Script implements  Identified {

    private final EnumId id;
    protected final LinkedList<ScriptAction> actions;

    public Script(@Nonnull String id) {
        this.id = EnumId.of(id);
        this.actions = Lists.newLinkedList();
    }

    @Event
    public void onStart() {
    }

    @Event
    public void onEnd() {
    }

    public ScriptActionBuilder builder() {
        return new ScriptActionBuilder(this);
    }

    @Nonnull
    public LinkedList<ScriptAction> copyActions() {
        return new LinkedList<>(actions);
    }

    @Override
    public final String toString() {
        return id.toString();
    }

    public void push(@Nonnull ScriptAction action) {
        actions.addLast(action);
    }

    @Nonnull
    @Override
    public EnumId getId() {
        return null;
    }
}
