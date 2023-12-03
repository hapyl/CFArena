package me.hapyl.fight.gui.styled;

/**
 * Size of the GUI + 1 row.
 * <p>
 * The last row is used as "styling."
 */
public enum Size {

    ONE(2),
    TWO(3),
    THREE(4),
    FOUR(5),
    FIVE(6),
    NOT_STYLED(6);

    public final int size;

    Size(int size) {
        this.size = size;
    }
}
