package me.hapyl.fight.database.entry;

import com.google.common.collect.Lists;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.artifact.Artifact;
import me.hapyl.fight.game.artifact.ArtifactRegistry;
import me.hapyl.fight.game.artifact.Type;
import me.hapyl.fight.registry.Registries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ArtifactEntry extends PlayerDatabaseEntry {
    public ArtifactEntry(@Nonnull PlayerDatabase playerDatabase) {
        super(playerDatabase, "artifacts");
    }

    public boolean isOwned(@Nonnull Artifact artifact) {
        return getOwned().contains(artifact);
    }

    @Nonnull
    public List<Artifact> getOwned() {
        final ArtifactRegistry registry = Registries.artifacts();
        final List<Artifact> artifacts = Lists.newArrayList();

        getValue("owned", new ArrayList<String>())
                .forEach(key -> {
                    final Artifact artifact = registry.get(key);

                    if (artifact != null) {
                        artifacts.add(artifact);
                    }
                });

        return artifacts;
    }

    public void setOwned(@Nonnull Artifact artifact, boolean owned) {
        fetchDocumentValue("owned", new ArrayList<>(), list -> {
            if (owned) {
                list.add(artifact.getKeyAsString());
            }
            else {
                list.remove(artifact.getKeyAsString());
            }
        });
    }

    @Nullable
    public Artifact getSelected(@Nonnull Type type) {
        return getValue("selected_" + type.getEntryPath(), null);
    }

    public void setSelected(@Nonnull Type type, @Nullable Artifact artifact) {
        setValue("selected_" + type.getEntryPath(), artifact != null ? artifact.getKeyAsString() : null);
    }
}
