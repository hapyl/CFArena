package me.hapyl.fight.game.talents.archive.archer;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.entity.Arrow;

import javax.annotation.Nonnull;

public class TripleShot extends Talent {

    private final Color arrowColor = Color.fromRGB(186, 177, 153);

    public TripleShot() {
        super(
                "Triple Shot",
                "Shoots three arrows in front of you. Two additional arrows deal &b50%&7 of normal damage."
        );

        setType(Type.DAMAGE);
        setItem(Material.ARROW);
        setCooldown(75);
        setPoint(0);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();

        if (location.getWorld() == null) {
            return Response.error("world is null?");
        }

        final Location offsetLocation = player.getLocation().add(0, 1.5, 0);

        final Arrow arrowMiddle = player.launchProjectile(Arrow.class);
        arrowMiddle.setColor(arrowColor);

        final Arrow arrowLeft = spawnArrow(player, offsetLocation);
        final Arrow arrowRight = spawnArrow(player, offsetLocation);

        arrowLeft.setVelocity(arrowMiddle.getVelocity().add(player.getVectorOffsetLeft(0.3d)));
        arrowRight.setVelocity(arrowMiddle.getVelocity().add(player.getVectorOffsetRight(0.3d)));

        final double damage = Heroes.ARCHER.getHero().getWeapon().getDamage();
        arrowMiddle.setDamage(damage);
        arrowLeft.setDamage(damage / 2);
        arrowRight.setDamage(damage / 2);

        // Fx
        PlayerLib.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.25f);
        PlayerLib.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 0.75f);

        return Response.OK;
    }

    private Arrow spawnArrow(GamePlayer player, Location location) {
        final World world = location.getWorld();

        if (world == null) {
            throw new IllegalArgumentException("Cannot shoot in an unloaded world!");
        }

        return world.spawn(location, Arrow.class, self -> {
            self.setColor(arrowColor);
            self.setCritical(false);
            self.setShooter(player.getPlayer());
        });
    }

}
