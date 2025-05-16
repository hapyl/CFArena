package me.hapyl.fight.game;

import javax.annotation.Nonnull;

public class ItemFactory<T> {
    
    protected final T product;
    
    public ItemFactory(@Nonnull T product) {
        this.product = product;
    }
}
