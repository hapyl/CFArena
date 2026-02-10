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
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TripleShot extends Talent {
    
    private final Color arrowColor = Color.fromRGB(186, 177, 153);
    
    @DisplayField private final short arrowCount = 3;
    @DisplayField(suffix = "°") private final double spread = 5;
    @DisplayField(percentage = true) private final double extraArrowDamage = 0.5d;
    
    public TripleShot(@Nonnull Key key) {
        super(key, "Triple Shot");
        
        setDescription("""
                       Shoot three arrows in front of you.
                       
                       The additional arrows deal &b{extraArrowDamage}&7 of normal damage.
                       """
        );
        
        setType(TalentType.DAMAGE);
        setMaterial(Material.ARROW);
        setCooldown(90);
    }
    
    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        final double damage = HeroRegistry.ARCHER.getWeapon().getDamage();
        
        final Arrow arrowMiddle = spawnArrow(player);
        final Arrow arrowLeft = spawnArrow(player);
        final Arrow arrowRight = spawnArrow(player);
        
        final double piSpread = Math.PI * Math.toRadians(spread);
        
        arrowMiddle.setDamage(damage);
        arrowLeft.setDamage(damage * extraArrowDamage);
        arrowRight.setDamage(damage * extraArrowDamage);
        
        arrowLeft.setVelocity(arrowMiddle.getVelocity().add(player.getVectorOffsetLeft(piSpread)));
        arrowRight.setVelocity(arrowMiddle.getVelocity().add(player.getVectorOffsetRight(piSpread)));
        
        // Fx
        player.playWorldSound(Sound.ENTITY_ARROW_SHOOT, 1.25f);
        player.playWorldSound(Sound.ENTITY_ARROW_SHOOT, 0.75f);
        
        return Response.OK;
    }
    
    private Arrow spawnArrow(GamePlayer player) {
        return player.launchProjectile(
                Arrow.class, self -> {
                    self.setColor(player.getSkinValue(AbstractSkinArcher.class, AbstractSkinArcher::getTripleShotArrowColor, arrowColor));
                    self.setCritical(false);
                    self.setShooter(player.getEntity());
                }
        );
    }
    
}
