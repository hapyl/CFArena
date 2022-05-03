package kz.hapyl.fight.game.effect;

import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class EffectParticleBlockMarker extends EffectParticle {

    private final Material block;

    public EffectParticleBlockMarker(int amount, Material block) {
        super(Particle.BLOCK_MARKER, amount);
        this.block = block;
        Validate.isTrue(block.isBlock(), "material is not a block");
    }

    @Override
    public void display(Location location, @Nullable Player ignored) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (ignored != null && player == ignored) {
                return;
            }
            final World world = location.getWorld();
            if (world == null) {
                return;
            }
            world.spawnParticle(
                    getParticle(),
                    location,
                    getAmount(),
                    getoX(),
                    getoY(),
                    getoZ(),
                    block.createBlockData()
            );
        });
    }
}
