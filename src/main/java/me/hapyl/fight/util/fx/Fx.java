package me.hapyl.fight.util.fx;

import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class Fx {

    @Nullable private String message;

    private Fx() {
    }

    public Fx withMessage(String message) {
        this.message = message;
        return this;
    }

    public Fx particle(Particle particle) {
        return this;
    }

    public final void display(Player player) {
    }

}
