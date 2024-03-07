package me.hapyl.fight.game.talents.archive.archer;

import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.cosmetic.skin.archer.AbstractSkinArcher;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.particle.ParticleBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import javax.annotation.Nonnull;
import java.util.Set;

public class ShockDark extends Talent implements Listener {

    @DisplayField(suffix = "blocks") private final double explosionRadius = 4.0d;
    @DisplayField private final double explosionMaxDamage = 20.0d;
    @DisplayField private final int explosionWindup = 26;

    private final ParticleBuilder blueColor = ParticleBuilder.redstoneDust(Color.fromRGB(89, 255, 233));
    private final ParticleBuilder redColor = ParticleBuilder.redstoneDust(Color.RED);

    private final int sphereRings = 20;
    private final Color arrowColor = Color.fromRGB(115, 157, 199);

    private final Set<Arrow> shockArrows;

    public ShockDark() {
        super(
                "Shock Dart",
                "Shoots an arrow infused with &oshocking &7power. Upon hit, charges and explodes dealing damage based on distance."
        );

        setType(Type.DAMAGE);
        setItem(Material.LIGHT_BLUE_DYE);
        setCooldownSec(5);

        shockArrows = Sets.newHashSet();
    }

    @Override
    public void onStop() {
        shockArrows.forEach(Arrow::remove);
        shockArrows.clear();
    }

    @EventHandler()
    public void handleProjectileLand(ProjectileHitEvent ev) {
        if (!(ev.getEntity() instanceof final Arrow arrow)) {
            return;
        }

        if (!(ev.getEntity().getShooter() instanceof final Player shooter)) {
            return;
        }

        final GamePlayer gamePlayer = CF.getPlayer(shooter);

        if (gamePlayer == null) {
            return;
        }

        if (shockArrows.contains(arrow)) {
            executeShockExplosion(gamePlayer, arrow.getLocation());
            shockArrows.remove(arrow);
        }
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Arrow arrow = player.launchProjectile(Arrow.class, self -> {
            self.setColor(player.getSkinValue(AbstractSkinArcher.class, AbstractSkinArcher::getShockDartArrowColor, arrowColor));
        });

        shockArrows.add(arrow);

        // Fx
        player.playWorldSound(Sound.ENTITY_ARROW_SHOOT, 1.0f);
        return Response.OK;
    }

    private void executeShockExplosion(GamePlayer player, Location location) {
        final ParticleBuilder blueColor = player.getSkinValue(
                AbstractSkinArcher.class,
                AbstractSkinArcher::getShockDartBlueColor,
                this.blueColor
        );

        final ParticleBuilder redColor = player.getSkinValue(
                AbstractSkinArcher.class,
                AbstractSkinArcher::getShockDartRedColor,
                this.redColor
        );

        Geometry.drawSphere(location, sphereRings, explosionRadius, blueColor::display);
        playAndCut(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2f, explosionWindup);

        new GameTask() {
            @Override
            public void run() {
                Geometry.drawSphere(location, sphereRings, explosionRadius, redColor::display);

                Collect.nearbyEntities(location, explosionRadius).forEach(target -> {
                    if (player.isTeammate(player)) {
                        return;
                    }

                    final double distance = target.getLocation().distance(location);
                    final double damage = distance <= 1 ? explosionMaxDamage : (explosionMaxDamage - (distance * 2));

                    target.setLastDamager(player);
                    target.damage(damage, EnumDamageCause.SHOCK_DART);
                });

                // Fx
                player.playWorldSound(location, Sound.ENCHANT_THORNS_HIT, 1.2f);
            }
        }.runTaskLater(explosionWindup);

    }

    private void playAndCut(Location location, Sound sound, float pitch, int cutAt) {
        final World world = location.getWorld();
        if (world == null) {
            return;
        }

        PlayerLib.playSound(location, sound, pitch);
        GameTask.runLater(() -> Bukkit.getOnlinePlayers().forEach(player -> player.stopSound(sound, SoundCategory.RECORDS)), cutAt);
    }
}
