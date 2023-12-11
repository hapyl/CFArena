package me.hapyl.fight.game.effect;

import me.hapyl.fight.game.entity.GamePlayer;
import org.apache.commons.lang.Validate;
import org.bukkit.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EffectParticleBlockMarker extends EffectParticle {

    private final Material block;

    public EffectParticleBlockMarker(int amount, Material block) {
        super(Particle.BLOCK_MARKER, amount);
        this.block = block;
        Validate.isTrue(block.isBlock(), "material is not a block");
    }

    @Override
    public void display(@Nonnull Location location, @Nullable GamePlayer ignored) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            final World world = location.getWorld();
            if (player == ignored || world == null) {
                return;
            }

            player.spawnParticle(getParticle(), location, getAmount(), getOffsetX(), getOffsetY(), getOffsetZ(), block.createBlockData());
        });
    }
}
