package me.hapyl.fight.game.talents.archer;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.skin.archer.AbstractSkinArcher;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class TripleShot extends Talent {

    private final Color arrowColor = Color.fromRGB(186, 177, 153);

    @DisplayField private final short arrowCount = 3;
    @DisplayField(suffix = "°") private final double spread = 5;

    public TripleShot(@Nonnull Key key) {
        super(key, "Triple Shot");

        setDescription("""
                Shoots three arrows in front of you. Two additional arrows deal &b50%&7 of normal damage.
                """
        );

        setType(TalentType.DAMAGE);
        setItem(Material.ARROW);
        setCooldown(90);
    }

    public short arrowCount() {
        return arrowCount;
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();

        if (location.getWorld() == null) {
            return Response.error("world is null?");
        }

        shoot(player, arrowCount);

        // Fx
        player.playWorldSound(Sound.ENTITY_ARROW_SHOOT, 1.25f);
        player.playWorldSound(Sound.ENTITY_ARROW_SHOOT, 0.75f);

        return Response.OK;
    }

    public void shoot(@Nonnull GamePlayer player, int amount) {
        final double damage = HeroRegistry.ARCHER.getWeapon().getDamage();

        final Arrow middleArrow = spawnArrow(player);
        middleArrow.setDamage(damage);

        boolean left = true;
        for (int i = 1; i < amount; i++) {
            final double spread = Math.PI * Math.toRadians(this.spread * ((int) Math.floor((double) (i + 1) / 2)));
            final Vector velocity = middleArrow.getVelocity();

            final Arrow arrow = spawnArrow(player);
            arrow.setDamage(damage / 2);
            arrow.setVelocity(velocity.add(left ? player.getVectorOffsetLeft(spread) : player.getVectorOffsetRight(spread)));

            left = !left;
        }
    }

    private Arrow spawnArrow(GamePlayer player) {
        return player.launchProjectile(
                Arrow.class, self -> {
                    self.setColor(player.getSkinValue(AbstractSkinArcher.class, AbstractSkinArcher::getTripleShotArrowColor, arrowColor));
                    self.setCritical(false);
                    self.setShooter(player.getPlayer());
                }
        );
    }

}
