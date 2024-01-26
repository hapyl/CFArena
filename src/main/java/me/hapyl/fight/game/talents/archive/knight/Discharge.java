package me.hapyl.fight.game.talents.archive.knight;

import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.HeroReference;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.knight.BlastKnight;
import me.hapyl.fight.game.heroes.archive.knight.BlastKnightData;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import javax.annotation.Nonnull;

public class Discharge extends Talent implements Listener, HeroReference<BlastKnight> {

    @DisplayField private final short minChargesToDischarge = 3;
    @DisplayField private final int dischargeDelayPerShieldCharge = 3;
    @DisplayField private final double damagePerShieldCharge = 3;
    @DisplayField private final double explosionRadius = 10.0d;
    @DisplayField private final int shieldCooldownPerCharge = 80;

    private final ItemStack fxItem = ItemBuilder.playerHeadUrl("d81fcffb53acbc7c00c53bc7121ca259371b5b76c001dc52139e1804c287e54").asIcon();

    public Discharge() {
        super("Quantum Discharge");

        setDescription("""
                Spend all &dQuantum Energy&7 to launch a &ddevice&7 that charges overtime.
                                
                Once charged, create &fNova Explosion&7 that deals &cAoE damage&7 and knocks enemies back.
                """);

        setType(Type.DAMAGE);
        setItem(Material.POPPED_CHORUS_FRUIT);
        setCooldownSec(20);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final BlastKnightData data = getHero().getPlayerData(player);
        final int shieldCharge = data.getShieldCharge();

        if (shieldCharge < minChargesToDischarge) {
            return Response.error("Not enough charges!");
        }

        player.setItem(EquipmentSlot.OFF_HAND, null);
        player.playWorldSound(Sound.ITEM_SHIELD_BREAK, 1.0f);

        final Location location = player.getLocation().add(0.0d, 2.5d, 0.0d);
        final double damage = damagePerShieldCharge * shieldCharge;

        final ArmorStand stand = Entities.ARMOR_STAND_MARKER.spawn(location, self -> {
            self.setSilent(true);
            self.setInvisible(true);
            self.setHelmet(fxItem);
        });

        data.resetShieldCharge();

        new TimedGameTask(dischargeDelayPerShieldCharge * shieldCharge) {
            @Override
            public void run(int tick) {
                if (!modulo(dischargeDelayPerShieldCharge)) {
                    stand.setHeadPose(new EulerAngle(ThreadRandom.nextDouble(), ThreadRandom.nextDouble(), ThreadRandom.nextDouble()));
                    player.playWorldSound(location, Sound.ENTITY_WITCH_HURT, 0.5f + (1.5f / maxTick * tick));
                }

                final Location location = stand.getLocation();
                final double y = Math.sin(Math.toRadians(tick * 8)) / 10d;

                location.add(0, y, 0);
                location.setYaw(location.getYaw() + 5);

                stand.teleport(location);
            }

            @Override
            public void onLastTick() {
                stand.remove();
                explode(location, player, damage);

                // Give shield back
                player.setItem(EquipmentSlot.OFF_HAND, getHero().shieldItem);
                player.setCooldown(Material.SHIELD, shieldCooldownPerCharge * shieldCharge);

                startCd(player);
            }
        }.runTaskTimer(0, 1);

        return Response.AWAIT;
    }

    public void explode(Location location, GamePlayer player, double damage) {
        Collect.nearbyEntities(location, explosionRadius).forEach(entity -> {
            if (player.isSelfOrTeammateOrHasEffectResistance(entity)) {
                return;
            }

            entity.damage(damage, player, EnumDamageCause.NOVA_EXPLOSION);
            entity.setVelocity(entity.getLocation().getDirection().normalize().multiply(-2.0d));
        });

        // Fx
        player.spawnWorldParticle(location, Particle.SMOKE_NORMAL, 50, 1.25d, 0.5d, 1.25d, 0.5f);
        player.spawnWorldParticle(location, Particle.FIREWORKS_SPARK, 50, 1.25d, 0.5d, 1.25d, 0.5f);
        player.spawnWorldParticle(location, Particle.SPELL_WITCH, 50, 1.25d, 0.5d, 1.25d, 1.0f);
        player.spawnWorldParticle(location, Particle.EXPLOSION_LARGE, 1, 0.0d, 0.5d, 0.0d, 0.0f);

        player.playWorldSound(location, Sound.ITEM_SHIELD_BREAK, 0.0f);
        player.playWorldSound(location, Sound.ENTITY_BLAZE_HURT, 0.0f);
        player.playWorldSound(location, Sound.ENTITY_WARDEN_DEATH, 0.0f);
    }

    @Nonnull
    @Override
    public BlastKnight getHero() {
        return Heroes.BLAST_KNIGHT.getHero(BlastKnight.class);
    }
}
