package me.hapyl.fight.game;

import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface EntityElement {

    @Nullable
    DamageOutput onDamageTaken(@Nonnull DamageInput input);

    @Nullable
    DamageOutput onDamageDealt(@Nonnull DamageInput input);

}
