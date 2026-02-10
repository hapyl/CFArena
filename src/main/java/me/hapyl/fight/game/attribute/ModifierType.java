package me.hapyl.fight.game.attribute;

/**
 * Represents types of modifiers that can be applied to attributes.
 * These modifiers follow the formula:
 * {@code value * (1 + additiveBonus) * (1 + multiplicativeBonus) + flatBonus}
 * where:
 * <ul>
 *   <li>additiveBonus: Sum of all additive bonuses {@code n + n + n}</li>
 *   <li>multiplicativeBonus: Combined multiplicative bonuses {@code (1 + n) * (1 + n) * (1 + n) - 1}</li>
 *   <li>flatBonus: Sum of all flat bonuses {@code n + n + n}</li>
 * </ul>
 */
public enum ModifierType {
    
    /**
     * An additive bonus that is summed and applied as a percentage increase.
     */
    ADDITIVE,
    
    /**
     * A multiplicative bonus that compounds with other multiplicative bonuses.
     */
    MULTIPLICATIVE,
    
    /**
     * A flat bonus that is added directly to the final value.
     */
    FLAT
    
}
