package me.hapyl.fight.game.talents.storage.archer;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.gometry.Draw;
import me.hapyl.spigotutils.module.particle.ParticleBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.Set;

public class ShockDark extends Talent implements Listener {

    private final double shockExplosionRadius = 3.7d;
    private final double shockExplosionMaxDamage = 15.0d; //9.0
    private final int shockDartWindup = 18;

    private final Set<Arrow> arrows;

    public ShockDark() {
        super(
                "Shock Dart",
                "Shoots an arrow infused with &oshocking &7power. Upon hit, charges and explodes dealing damage based on distance.",
                Type.COMBAT
        );
        this.setItem(Material.LIGHT_BLUE_DYE);
        this.setCd(120);

        this.arrows = Sets.newHashSet();
    }

    @Override
    public void onStop() {
        arrows.forEach(Arrow::remove);
        arrows.clear();
    }

    @EventHandler()
    public void handleProjectileLand(ProjectileHitEvent ev) {
        if (!(ev.getEntity() instanceof final Arrow arrow)) {
            return;
        }

        if (!(ev.getEntity().getShooter() instanceof final Player shooter)) {
            return;
        }

        if (arrows.contains(arrow)) {
            executeShockExplosion(shooter, arrow.getLocation());
            arrows.remove(arrow);
        }
    }

    private void executeShockExplosion(Player player, Location location) {

        final ParticleBuilder blueColor = ParticleBuilder.redstoneDust(Color.fromRGB(89, 255, 233));
        final ParticleBuilder redColor = ParticleBuilder.redstoneDust(Color.RED);

        Geometry.drawSphere(location, 10, 4, new Draw(Particle.VILLAGER_HAPPY) {
            @Override
            public void draw(Location location) {
                blueColor.display(location);
            }
        });

        playAndCut(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2f, shockDartWindup);

        new GameTask() {
            @Override
            public void run() {
                Geometry.drawSphere(location, 10, shockExplosionRadius, new Draw(Particle.VILLAGER_HAPPY) {
                    @Override
                    public void draw(Location location) {
                        redColor.display(location);
                    }
                });
                PlayerLib.playSound(location, Sound.ENCHANT_THORNS_HIT, 1.2f);
                Utils.getEntitiesInRange(location, shockExplosionRadius)
                        .forEach(target -> {
                            final double distance = target.getLocation().distance(location);
                            final double damage = distance <= 1 ? shockExplosionMaxDamage : (shockExplosionMaxDamage - (distance * 2));
                            GamePlayer.damageEntity(target, damage, player, EnumDamageCause.SHOCK_DART);
                        });
            }
        }.runTaskLater(shockDartWindup);

    }

    private void playAndCut(Location location, Sound sound, float pitch, int cutAt) {
        final World world = location.getWorld();
        if (world == null) {
            return;
        }

        PlayerLib.playSound(location, sound, pitch);
        GameTask.runLater(() -> Bukkit.getOnlinePlayers().forEach(player -> player.stopSound(sound, SoundCategory.RECORDS)), cutAt);
    }

    @Override
    public Response execute(Player player) {
        final Arrow arrow = player.launchProjectile(Arrow.class);
        this.arrows.add(arrow);
        PlayerLib.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0f);
        return Response.OK;
    }
}
