package me.hapyl.fight.game.artifact;

import me.hapyl.eterna.module.registry.SimpleRegistry;

public class ArtifactRegistry extends SimpleRegistry<Artifact> {

    public ArtifactRegistry() {
        registerArtifacts();
    }

    private void registerArtifacts() {
        register(new Artifact("test_artifact", "Test Artifact", """
                The best artifact in the world!
                """));
    }


}
