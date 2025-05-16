package me.hapyl.fight.game.talents.engineer;

import me.hapyl.fight.util.ImmutableArray;

import javax.annotation.Nonnull;

public class ImmutableInt3Array implements ImmutableArray<Integer> {
    
    private final int[] array;
    
    public ImmutableInt3Array(int i1, int i2, int i3) {
        this.array = new int[] { i1, i2, i3 };
    }
    
    @Nonnull
    @Override
    public Integer get(int index) throws IndexOutOfBoundsException {
        return array[index];
    }
    
    @Override
    public int length() {
        return 3;
    }
    
}
