package me.hapyl.fight.game.cosmetic.contrail;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Tuple;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.setting.EnumSetting;
import org.bukkit.Particle;

import javax.annotation.Nonnull;

public class ParticleContrailCosmetic extends ContrailCosmetic {

    private final Particle particle;

    public ParticleContrailCosmetic(@Nonnull Key key, @Nonnull Particle particle, @Nonnull String name, @Nonnull String description, @Nonnull Rarity rarity) {
        super(key, name, description, rarity, Tuple.of("particle", "It will follow behind you."));

        this.particle = particle;
    }

    @Nonnull
    public Particle getParticle() {
        return particle;
    }

    @Override
    public void onMove(@Nonnull Display display) {
        display.particle0(display.getLocation(), particle, 1, 0.0d, 0.0d, 0.0d, 0.0f, EnumSetting.SEE_OTHERS_CONTRAIL::isDisabled, null);
    }
}
