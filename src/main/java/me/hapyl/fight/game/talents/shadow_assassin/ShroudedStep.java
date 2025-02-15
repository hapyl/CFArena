package me.hapyl.fight.game.talents.shadow_assassin;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.reflect.npc.ClickType;
import me.hapyl.eterna.module.reflect.npc.Human;
import me.hapyl.eterna.module.reflect.npc.HumanNPC;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class ShroudedStep extends Talent {

    @DisplayField private final short maxDistance = 100;
    @DisplayField private final int decoyDuration = 30;
    @DisplayField private final double decoyExplosionRadius = 1.5d;
    @DisplayField private final double decoyExplosionDamage = 10.0d;

    public ShroudedStep(@Nonnull Key key) {
        super(key, "Shrouded Step");

        setDescription("""
                While in Dark Cover, deploy a decoy footprints that travel in a straight line.
                
                Leave Dark Cover to create a decoy that explodes after being hit or after a short duration damaging nearby enemies.
                """
        );

        setCooldown(600);
        setItem(Material.NETHERITE_BOOTS);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        if (!player.isSneaking()) {
            return Response.error("You must be in &lDark Cover &cto use this!");
        }

        final Location location = player.getLocation();
        final ArmorStand entity = Entities.ARMOR_STAND.spawn(location, me -> {
            me.setVisible(false);
            me.setSilent(true);
            me.setInvulnerable(true);
            me.setSmall(true);
            me.getLocation().setYaw(location.getYaw());
        });

        new GameTask() {
            private int distance = maxDistance;

            private void explode(Human decoy) {
                if (!decoy.isAlive()) {
                    return;
                }

                final Location decoyLocation = decoy.getLocation().add(0.0d, 1.8d, 0.0d);

                decoy.remove();
                Collect.nearbyEntities(decoyLocation, decoyExplosionRadius).forEach(entity -> {
                    if (entity.equals(player)) {
                        return;
                    }

                    entity.damage(decoyExplosionDamage, player, DamageCause.DECOY);
                });

                PlayerLib.spawnParticle(decoyLocation, Particle.ENCHANTED_HIT, 50, 0.5d, 0.5d, 0.5d, 0.01f);
                PlayerLib.playSound(decoyLocation, Sound.ENTITY_SILVERFISH_DEATH, 0.0f);
            }

            @Override
            public void run() {
                final Location entityLocation = entity.getLocation();

                // Check for sneaking
                if (!player.isSneaking()) {
                    final Human decoy = new HumanNPC(entityLocation, "", player.getName()) {
                        @Override
                        public void onClick(@Nonnull Player player, @Nonnull ClickType clickType) {
                            explode(this);
                        }
                    };

                    decoy.setEquipment(player.getEquipment());
                    decoy.showAll();

                    GameTask.runLater(() -> explode(decoy), decoyDuration);

                    cancel();
                    return;
                }

                // Travel finished
                if (distance < 0 || entity.isDead() || player.isDead()) {
                    if (distance < 0) {
                        PlayerLib.spawnParticle(entityLocation, Particle.ENCHANTED_HIT, 10, 0, 0, 0, 0.5f);
                        entity.remove();
                    }
                    cancel();
                    return;
                }

                // Travel
                final Vector vector = entityLocation.getDirection();
                entity.setVelocity(new Vector(vector.getX(), -1, vector.getZ()).normalize().multiply(0.15f));
                HeroRegistry.SHADOW_ASSASSIN.displayFootprints(entityLocation);

                --distance;
            }
        }.runTaskTimer(0, 2);

        return Response.OK;
    }
}
