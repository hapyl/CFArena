package me.hapyl.fight.game.heroes.ultimate;

import javax.annotation.Nonnull;

public final class UltimateInstanceBuilder extends UltimateInstance {
    private UltimateInvoker<Void> onCastStart;
    private UltimateInvoker<Integer> onCastTick;
    private UltimateInvoker<Void> onCastEnd;
    private UltimateInvoker<Void> onExecute;
    private UltimateInvoker<Integer> onTick;
    private UltimateInvoker<Void> onEnd;

    public UltimateInstanceBuilder() {
    }

    public UltimateInstanceBuilder onCastStart(@Nonnull Runnable runnable) {
        this.onCastStart = UltimateInvoker.ofRunnable(runnable);
        return this;
    }

    @Override
    public void onCastStart() {
        execute(onCastStart, null);
    }

    public UltimateInstanceBuilder onCastTick(@Nonnull UltimateInvoker<Integer> runnable) {
        this.onCastTick = runnable;
        return this;
    }

    @Override
    public void onCastTick(int tick) {
        execute(onCastTick, tick);
    }

    public UltimateInstanceBuilder onCastEnd(@Nonnull Runnable runnable) {
        this.onCastEnd = UltimateInvoker.ofRunnable(runnable);
        return this;
    }

    @Override
    public void onCastEnd() {
        execute(onCastEnd, null);
    }

    public UltimateInstanceBuilder onExecute(@Nonnull Runnable runnable) {
        this.onExecute = UltimateInvoker.ofRunnable(runnable);
        return this;
    }

    @Override
    public void onExecute() {
        execute(onExecute, null);
    }

    public UltimateInstanceBuilder onTick(@Nonnull UltimateInvoker<Integer> runnable) {
        this.onTick = runnable;
        return this;
    }

    @Override
    public void onTick(int tick) {
        execute(onTick, tick);
    }

    public UltimateInstanceBuilder onEnd(@Nonnull Runnable runnable) {
        this.onEnd = UltimateInvoker.ofRunnable(runnable);
        return this;
    }

    @Override
    public void onEnd() {
        execute(onEnd, null);
    }

    private <E> void execute(UltimateInvoker<E> state, E value) {
        if (state != null) {
            state.invoke(value);
        }
    }
}
