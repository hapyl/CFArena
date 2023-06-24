package me.hapyl.fight.game.talents.archive.bounty_hunter;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.Set;

public class ShortyShotgun extends Talent {

    @DisplayField(suffix = "blocks") private final double bleedThreshold = 1.0d;
    @DisplayField private final int bleedDuration = 100;
    @DisplayField private final short pellets = 12;
    @DisplayField private final double maxDamagePerPellet = 7.0d;
    @DisplayField private final double spread = 0.5d;
    @DisplayField(suffix = "blocks") private final double maxDistance = 3.0d;

    private final Set<Player> hasShotFirst = Sets.newHashSet();

    public ShortyShotgun() {
        super("Shorty");

        setDescription("""
                Shoot you double barrel to deal damage that falls off with distance.
                                
                If hit enemy is close enough, they will &cbleed&7 and will be &bvulnerable&7 for &b{bleedDuration}&7.
                                
                &6;;This ability can be shot twice consecutively before reloading.
                """);

        setItem(Material.CROSSBOW);
        setStartAmount(2);
        setCooldown(50);
    }

    @Override
    public Response execute(Player player) {
        for (int i = 0; i < pellets; i++) {
            raycastPellet(player);
        }

        // Fx
        PlayerLib.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.75f);

        if (hasShotFirst.contains(player)) {
            hasShotFirst.remove(player);

            fixAmount(player);
            return Response.OK;
        }

        hasShotFirst.add(player);
        fixAmount(player);
        startCd(player, 5);

        return Response.AWAIT;
    }

    private void fixAmount(Player player) {
        final PlayerInventory inventory = player.getInventory();
        final int indexOfTalent = inventory.first(getMaterial());

        if (indexOfTalent == -1) {
            return;
        }

        final ItemStack item = inventory.getItem(indexOfTalent);

        if (item == null) {
            return;
        }

        if (hasShotFirst.contains(player)) {
            item.setAmount(1);
        }
        else {
            item.setAmount(2);
        }
    }

    private void raycastPellet(Player player) {
        final Location playerEyeLocation = player.getEyeLocation().subtract(0.0d, 0.2d, 0.0d);
        final Vector direction = playerEyeLocation.getDirection().normalize().add(getRandomVector());

        for (double d = 0; d < maxDistance; d += 0.25) {
            final Location location = playerEyeLocation.clone().add(direction.clone().multiply(d));
            final LivingEntity entity = Collect.nearestLivingEntity(location, 1.0d, player);

            // Had to put fx here since breaking
            player.getWorld().spawnParticle(Particle.BLOCK_CRACK, location, 1, Material.COAL_BLOCK.createBlockData());

            if (entity != null) {
                // Check for bleed
                if (entity.getLocation().distance(player.getLocation()) <= bleedThreshold) {
                    if (entity instanceof Player entityPlayer) {
                        GamePlayer.getPlayer(entityPlayer).addEffect(GameEffectType.BLEED, bleedDuration, true);
                        GamePlayer.getPlayer(entityPlayer).addEffect(GameEffectType.VULNERABLE, bleedDuration, true);
                    }
                }

                double damage = maxDamagePerPellet - d;

                if (Talents.GRAPPLE.getTalent(GrappleHookTalent.class).hasHook(player)) {
                    damage *= 1.5d;
                }

                GamePlayer.damageEntity(entity, damage, player, EnumDamageCause.SHOTGUN);

                // Knock back entity
                entity.setVelocity(location.getDirection().normalize().multiply(1.2d).setY(0.25d));
                return;
            }
        }
    }

    private Vector getRandomVector() {
        return new Vector(
                new Random().nextDouble(-spread, spread),
                new Random().nextDouble(-spread, spread),
                new Random().nextDouble(-spread, spread)
        );
    }

}
