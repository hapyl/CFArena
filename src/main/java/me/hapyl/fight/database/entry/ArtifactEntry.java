package me.hapyl.fight.database.entry;

import com.google.common.collect.Lists;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.artifact.Artifact;
import me.hapyl.fight.game.artifact.ArtifactRegistry;
import me.hapyl.fight.game.artifact.Type;
import me.hapyl.fight.registry.Registry;
import me.hapyl.fight.util.CFUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ArtifactEntry extends PlayerDatabaseEntry {
    public ArtifactEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
        setPath("artifacts");
    }

    public boolean isOwned(@Nonnull Artifact artifact) {
        return getOwned().contains(artifact);
    }

    @Nonnull
    public List<Artifact> getOwned() {
        final ArtifactRegistry registry = Registry.ARTIFACTS;
        final List<Artifact> artifacts = Lists.newArrayList();
        final List<String> ownedNames = getInDocument().get("owned", Lists.newArrayList());

        ownedNames.forEach(name -> {
            final Artifact artifact = registry.get(name);

            if (artifact != null) {
                artifacts.add(artifact);
            }
        });

        return artifacts;
    }

    public void setOwned(@Nonnull Artifact artifact, boolean owned) {
        fetchDocument(document -> {
            final String id = artifact.getId();
            final List<String> ownedList = document.get("owned", Lists.newArrayList());

            document.put("owned", CFUtils.computeCollection(ownedList, id, owned));
        });
    }

    @Nullable
    public Artifact getSelected(@Nonnull Type type) {
        return fetchFromDocument(document -> {
            return document.get("selected_" + type.getEntryPath(), null);
        });
    }

    public void setSelected(@Nonnull Type type, @Nullable Artifact artifact) {
        fetchDocument(document -> {
            document.put("selected_" + type.getEntryPath(), artifact != null ? artifact.getId() : null);
        });
    }
}
