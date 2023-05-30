package me.hapyl.fight.game.effect.storage;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.damage.EntityData;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;

public class BleedEffect extends GameEffect {

    private final double damage = 2.0d;

    public BleedEffect() {
        super("Bleed");
        setPositive(false);
    }

    @Override
    public void onStart(LivingEntity entity) {
        Chat.sendMessage(entity, "&c&l∲ &7You are bleeding!");
    }

    @Override
    public void onStop(LivingEntity entity) {
        Chat.sendMessage(entity, "&c&l∲ &aThe bleeding has stopped!");
    }

    @Override
    public void onTick(LivingEntity entity, int tick) {
        if (tick == 0) {
            EntityData.damage(entity, damage, null, EnumDamageCause.BLEED);

            entity.getWorld()
                    .spawnParticle(
                            Particle.BLOCK_CRACK,
                            entity.getLocation(),
                            10,
                            0.5d,
                            0.5d,
                            0.5d,
                            0.0d,
                            Material.REDSTONE_BLOCK.createBlockData()
                    );
        }
    }
}
