package me.hapyl.fight.util.fx;

import com.google.common.collect.Lists;
import org.bukkit.Particle;

import java.util.List;

public abstract class Fx {

    private List<ParticleFx> particles;

    private Fx() {
        this.particles = Lists.newArrayList();
    }

    public Fx particle(Particle particle) {
        return this;
    }

}
