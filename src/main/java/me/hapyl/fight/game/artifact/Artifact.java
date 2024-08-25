package me.hapyl.fight.game.artifact;

import me.hapyl.fight.game.talents.Cooldown;
import me.hapyl.fight.registry.Key;
import me.hapyl.fight.registry.Keyed;
import me.hapyl.fight.util.Described;

import javax.annotation.Nonnull;

public class Artifact implements Keyed, Described, Cooldown {

    private final Key key;
    private final String name;
    private final String description;

    private ArtifactFamily family;
    private int cooldown;

    public Artifact(@Nonnull String id, @Nonnull String name, @Nonnull String description) {
        this.key = Key.ofString(id);
        this.name = name;
        this.description = description;
    }

    @Nonnull
    @Override
    public Key getKey() {
        return key;
    }

    @Nonnull
    public ArtifactFamily getFamily() {
        return family;
    }

    public void setFamily(@Nonnull ArtifactFamily family) {
        this.family = family;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }

    @Override
    public Artifact setCooldown(int cooldown) {
        this.cooldown = cooldown;
        return this;
    }
}
