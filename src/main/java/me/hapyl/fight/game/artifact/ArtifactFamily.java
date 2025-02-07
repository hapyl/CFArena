package me.hapyl.fight.game.artifact;

import me.hapyl.eterna.module.util.Described;
import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.AutoRegisteredListener;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

@AutoRegisteredListener
public class ArtifactFamily implements Described, Listener {

    private final String name;
    private final String description;

    public ArtifactFamily(@Nonnull String name, @Nonnull String description) {
        this.name = name;
        this.description = description;

        CF.registerEvents(this);
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
}
