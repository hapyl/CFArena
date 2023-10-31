package me.hapyl.fight.game.heroes.archive.mage;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class DragonSkinSpell extends MageSpell {
    public DragonSkinSpell() {
        super("Dragon's Skin");

        setItem(Material.PHANTOM_MEMBRANE);

        setDescription(
                "Consume to gain &cincredible strength&7, but suffer %s reduction.",
                AttributeType.SPEED
        );
    }

    @Override
    protected void useSpell(@Nonnull GamePlayer player) {
        final int duration = getDuration();

        player.addPotionEffect(PotionEffectType.SLOW, duration, 2);
        player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE, duration, 3);
        player.addPotionEffect(PotionEffectType.JUMP, duration, 250);

        // Fx
        player.spawnParticle(player.getLocation().add(0.0d, 1.0d, 0.0d), Particle.CRIT_MAGIC, 40, 0.1, 0.1, 0.1, 1);
    }
}
