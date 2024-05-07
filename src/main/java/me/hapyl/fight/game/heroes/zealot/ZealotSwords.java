package me.hapyl.fight.game.heroes.zealot;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Removable;
import me.hapyl.fight.game.task.player.PlayerGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.MaterialCooldown;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.entity.EntityUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Giant;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ZealotSwords extends PlayerGameTask implements Removable, MaterialCooldown {

    private final GamePlayer player;
    private final ZealotUltimate ultimate;
    private final Giant[] giants;

    private int tick;
    private boolean swing;

    public ZealotSwords(GamePlayer player, ZealotUltimate ultimate) {
        super(player);

        this.player = player;
        this.ultimate = ultimate;
        this.giants = new Giant[] {
                createGiant(true, new ItemStack(Material.GOLDEN_SWORD)),
                createGiant(false, new ItemStack(Material.DIAMOND_SWORD))
        };

        runTaskTimer(0, 1);
    }

    @Override
    public void run() {
        if (player.isDeadOrRespawning() || tick++ > ultimate.getDuration()) {
            cancel();
            return;
        }

        final Location location = getLocation();

        for (Giant giant : giants) {
            giant.setFireTicks(0);
            giant.teleport(location);
        }
    }

    @Override
    public void onTaskStopBecauseOfDeath() {
        remove();
    }

    @Override
    public void onTaskStop() {
        remove();
    }

    @Override
    public void remove() {
        for (Giant giant : giants) {
            giant.remove();

            player.spawnWorldParticle(giant.getEyeLocation(), Particle.POOF, 5, 0.25d, 0.5d, 0.25d, 0.05f);
        }
    }

    public void swing() {
        if (hasCooldown(player)) {
            return;
        }

        startCooldown(player);

        if (swing) {
            onSwingLeft();
        }
        else {
            onSwingRight();
        }

        // Giant fx
        giants[swing ? 0 : 1].swingMainHand();

        swing = !swing;
    }

    public void onSwingLeft() {
    }

    public void onSwingRight() {
    }

    @Nonnull
    @Override
    public Material getCooldownMaterial() {
        return ultimate.getMaterial();
    }

    @Override
    public int getCooldown() {
        return 0;
    }

    private void hitEnemies(Consumer<LivingGameEntity> consumer) {
        final Location location = player.getLocationInFrontFromEyes(3);

        Collect.nearbyEntities(location, 4.0d)
                .forEach(entity -> {
                    if (player.isSelfOrTeammate(entity)) {
                        return;
                    }

                    consumer.accept(entity);
                });

        // Fx
        player.spawnWorldParticle(location, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 5);
        player.playWorldSound(location, Sound.ENTITY_PLAYER_ATTACK_CRIT, 0.75f);
        player.playWorldSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.75f);
    }

    private Giant createGiant(boolean left, ItemStack itemStack) {
        return Entities.GIANT.spawn(getLocation(), self -> {
            self.setInvulnerable(true);
            self.setInvisible(true);
            self.setGravity(false);

            final EntityEquipment equipment = self.getEquipment();

            if (equipment != null) {
                if (left) {
                    equipment.setItemInOffHand(itemStack);
                }
                else {
                    equipment.setItemInMainHand(itemStack);
                }
            }

            EntityUtils.setCollision(self, EntityUtils.Collision.DENY);
        });
    }

    @Nonnull
    private Location getLocation() {
        final Location location = player.getLocation();
        location.subtract(0.0d, 7.0d, 0.0d);
        location.setPitch(0.0f);

        location.add(location.getDirection().setY(0.0d).normalize().multiply(-4.0d));
        return location;
    }
}
