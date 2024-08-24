package me.hapyl.fight.vehicle;

import me.hapyl.fight.util.ImmutableList;

import javax.annotation.Nonnull;

public enum VehicleDirection {

    FORWARD,
    BACKWARDS,
    RIGHT,
    LEFT,
    UP,
    DOWN;

    private static final double MOVE_CONSTANT = 0.98f;

    @Nonnull
    public static ImmutableList<VehicleDirection> of(float x, float z, boolean isJumping, boolean isSneaking) {
        return ImmutableList.of(
                z >= MOVE_CONSTANT ? FORWARD : null,
                z <= -MOVE_CONSTANT ? BACKWARDS : null,
                x >= MOVE_CONSTANT ? LEFT : null,
                x <= -MOVE_CONSTANT ? RIGHT : null,
                isJumping ? UP : null,
                isSneaking ? DOWN : null
        );
    }

}
