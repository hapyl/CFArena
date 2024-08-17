package me.hapyl.fight.game.talents.archer;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.cosmetic.skin.archer.AbstractSkinArcher;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;

import javax.annotation.Nonnull;

public class TripleShot extends Talent {

    private final Color arrowColor = Color.fromRGB(186, 177, 153);

    @DisplayField(suffix = "°") private final double spread = 5;

    public TripleShot() {
        super(
                "Triple Shot",
                "Shoots three arrows in front of you. Two additional arrows deal &b50%&7 of normal damage."
        );

        setType(TalentType.DAMAGE);
        setItem(Material.ARROW);
        setCooldown(90);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();

        if (location.getWorld() == null) {
            return Response.error("world is null?");
        }

        final Arrow arrowMiddle = spawnArrow(player);
        final Arrow arrowLeft = spawnArrow(player);
        final Arrow arrowRight = spawnArrow(player);

        final double piSpread = Math.PI * Math.toRadians(spread);

        arrowLeft.setVelocity(arrowMiddle.getVelocity().add(player.getVectorOffsetLeft(piSpread)));
        arrowRight.setVelocity(arrowMiddle.getVelocity().add(player.getVectorOffsetRight(piSpread)));

        final double damage = HeroRegistry.ARCHER.getWeapon().getDamage();

        arrowMiddle.setDamage(damage);
        arrowLeft.setDamage(damage / 2);
        arrowRight.setDamage(damage / 2);

        // Fx
        player.playWorldSound(Sound.ENTITY_ARROW_SHOOT, 1.25f);
        player.playWorldSound(Sound.ENTITY_ARROW_SHOOT, 0.75f);

        return Response.OK;
    }

    private Arrow spawnArrow(GamePlayer player) {
        return player.launchProjectile(Arrow.class, self -> {
            self.setColor(player.getSkinValue(AbstractSkinArcher.class, AbstractSkinArcher::getTripleShotArrowColor, arrowColor));
            self.setCritical(false);
            self.setShooter(player.getPlayer());
        });
    }

}
