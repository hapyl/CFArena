package me.hapyl.fight.game.cosmetic.contrail;

import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.setting.Setting;
import me.hapyl.fight.game.cosmetic.Rarity;
import org.bukkit.Particle;

public class ParticleContrailCosmetic extends ContrailCosmetic {

    private final Particle particle;

    public ParticleContrailCosmetic(Particle particle, String name, String description, long cost, Rarity rarity) {
        super(name, description, rarity);
        this.particle = particle;
    }

    public Particle getParticle() {
        return particle;
    }

    @Override
    public void onMove(Display display) {
        display.particle0(display.getLocation(), particle, 1, 0.0d, 0.0d, 0.0d, 0.0f, Setting.SEE_OTHERS_CONTRAIL::isDisabled, null);
    }
}
