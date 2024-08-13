package me.hapyl.fight.game.heroes.heavy_knight;

import me.hapyl.eterna.module.util.Buffer;
import me.hapyl.fight.game.Event;

import javax.annotation.Nonnull;

public abstract class BufferedOrder<E> {

    private final long delay;
    private final E[] correctOrder;

    private final Buffer<E> buffer;
    private long lastUse;

    @SafeVarargs
    public BufferedOrder(long delay, @Nonnull E... correctOrder) {
        this.delay = delay;
        this.correctOrder = correctOrder;
        this.buffer = new Buffer<>(correctOrder.length) {
            @Override
            public void unbuffered(@Nonnull E e) {

            }
        };
    }

    public boolean offer(@Nonnull E e) {
        if (lastUse > 0L && System.currentTimeMillis() - lastUse >= delay) {
            buffer.clear();
        }

        lastUse = System.currentTimeMillis();

        // Adding an already present value is not allowed
        if (buffer.contains(e)) {
            return false;
        }

        buffer.add(e);

        // Compare
        if (!buffer.compareAll(correctOrder)) {
            return false;
        }

        onCorrectOrder();
        return true;
    }

    public void clear() {
        buffer.clear();
    }

    @Event
    public abstract void onCorrectOrder();

}
