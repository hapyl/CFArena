package me.hapyl.fight.game.talents.archer;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.archer.ArcherMastery;
import me.hapyl.fight.game.skin.archer.AbstractSkinArcher;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

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
        final ArcherMastery mastery = HeroRegistry.ARCHER.getMastery();
        
        final int arrowCount = mastery.getTripleShotArrowCount(player, this.arrowCount);
        final double arrowSpeed = mastery.getTripleShotArrowSpeed(player, 1.0d);
        
        shoot(player, arrowCount, arrowSpeed);
        
        // Fx
        player.playWorldSound(Sound.ENTITY_ARROW_SHOOT, 1.25f);
        player.playWorldSound(Sound.ENTITY_ARROW_SHOOT, 0.75f);
        
        return Response.OK;
    }
    
    public void shoot(@Nonnull GamePlayer player, int amount, double arrowSpeed) {
        final double damage = HeroRegistry.ARCHER.getWeapon().getDamage();
        
        final Arrow middleArrow = spawnArrow(player);
        
        if (arrowSpeed != 1.0d) {
            middleArrow.setVelocity(middleArrow.getVelocity().multiply(arrowSpeed));
        }
        
        middleArrow.setDamage(damage);
        
        for (int i = 1; i < amount; i++) {
            final double spread = Math.PI * Math.toRadians(this.spread * ((int) Math.floor((double) (i + 1) / 2)));
            final Vector velocity = middleArrow.getVelocity();
            
            final Arrow arrow = spawnArrow(player);
            arrow.setDamage(damage * extraArrowDamage);
            arrow.setVelocity(velocity.add(i % 2 == 0 ? player.getVectorOffsetLeft(spread) : player.getVectorOffsetRight(spread)));
        }
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
