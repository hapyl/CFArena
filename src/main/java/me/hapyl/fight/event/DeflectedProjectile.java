package me.hapyl.fight.event;

import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.entity.Projectile;

public class DeflectedProjectile {

    public final Projectile projectile;
    public final GamePlayer damager;
    public final double damage;

    public DeflectedProjectile(Projectile projectile, GamePlayer damager, double damage) {
        this.projectile = projectile;
        this.damager = damager;
        this.damage = damage;
    }

}
