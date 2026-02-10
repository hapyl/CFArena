package me.hapyl.fight.util;

import me.hapyl.eterna.module.util.RomanNumber;
import me.hapyl.eterna.module.util.Validate;
import org.jetbrains.annotations.Range;

import javax.annotation.Nonnull;

public final class RomanInt {
    
    private final int integer;
    private final String roman;
    
    RomanInt(int integer) {
        Validate.isTrue(integer > 0, "Integer must be positive");
        
        this.integer = integer;
        this.roman = RomanNumber.toRoman(integer);
    }
    
    @Nonnull
    public static RomanInt of(@Range(from = 0, to = Integer.MAX_VALUE) int integer) {
        return new RomanInt(integer);
    }
    
    public int toInt() {
        return integer;
    }
    
    @Override
    public String toString() {
        return roman;
    }
}
