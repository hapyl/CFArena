package me.hapyl.fight.game.talents.bounty_hunter;

import com.google.common.collect.Sets;
import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.Set;

public class ShortyShotgun extends Talent {

    @DisplayField(suffix = "blocks") private final double bleedThreshold = 1.0d;
    @DisplayField private final int bleedDuration = 100;
    @DisplayField private final short pellets = 12;
    @DisplayField private final double maxDamagePerPellet = 5.0d;
    @DisplayField private final double spread = 0.5d;
    @DisplayField(suffix = "blocks") private final double maxDistance = 3.0d;

    private final Set<GamePlayer> hasShotFirst = Sets.newHashSet();

    public ShortyShotgun(@Nonnull DatabaseKey key) {
        super(key, "Shorty");

        setDescription("""
                Shoot you double barrel to deal &cdamage&7 that &nfalls&7 &noff&7 with &bdistance&7.
                
                If hit &cenemy&7 is &bclose enough&7, they will &cBleed&7 and will be &bVulnerable&7 for &b{bleedDuration}&7.
                &8;;Additionally, if used while on a Grapple Hook, the damage is increased.
                
                &8;;This talent can be used twice consecutively before reloading.
                """
        );

        setItem(Material.CROSSBOW);
        setStartAmount(2);
        setCooldown(60);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        for (int i = 0; i < pellets; i++) {
            raycastPellet(player);
        }

        // Fx
        player.playWorldSound(Sound.ENTITY_GENERIC_EXPLODE, 1.75f);

        // Fix amount
        if (hasShotFirst.contains(player)) {
            hasShotFirst.remove(player);
            fixAmount(player);

            return Response.OK;
        }

        hasShotFirst.add(player);
        fixAmount(player);

        startCd(player, 5); // internal cooldown

        return Response.AWAIT;
    }

    private void fixAmount(GamePlayer player) {
        final ItemStack shotgunItem = player.getItem(HeroRegistry.BOUNTY_HUNTER.getTalentSlotByHandle(this));

        if (shotgunItem == null || shotgunItem.getType() != getMaterial()) {
            return;
        }

        if (hasShotFirst.contains(player)) {
            shotgunItem.setAmount(1);
        }
        else {
            shotgunItem.setAmount(2);
        }
    }

    private void raycastPellet(GamePlayer player) {
        final Location playerEyeLocation = player.getEyeLocation().subtract(0.0d, 0.2d, 0.0d);
        final Vector direction = playerEyeLocation.getDirection().normalize().add(getRandomVector());
        final GrappleHookTalent hookTalent = TalentRegistry.GRAPPLE;

        for (double d = 0; d < maxDistance; d += 0.25) {
            final Location location = playerEyeLocation.clone().add(direction.clone().multiply(d));
            final LivingGameEntity entity = Collect.nearestEntity(location, 1.0d, player);

            // Had to put fx here since breaking
            player.spawnWorldParticle(location, Particle.BLOCK, 1, 0, 0, 0, Material.COAL_BLOCK.createBlockData());

            if (entity != null) {
                // Check for bleed
                if (entity.getLocation().distance(player.getLocation()) <= bleedThreshold) {
                    entity.setLastDamager(player);
                    entity.addEffect(Effects.BLEED, bleedDuration, true);
                    entity.addEffect(Effects.VULNERABLE, bleedDuration, true);
                }

                double damage = maxDamagePerPellet - d;

                if (hookTalent.hasHook(player)) {
                    damage *= hookTalent.onHookMultiplier;
                }

                entity.damage(damage, player, EnumDamageCause.SHOTGUN);

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
