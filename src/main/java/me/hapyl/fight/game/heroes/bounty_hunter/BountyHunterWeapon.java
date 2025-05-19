package me.hapyl.fight.game.heroes.bounty_hunter;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.math.Geometry;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.*;

import javax.annotation.Nonnull;

public class BountyHunterWeapon extends Weapon {
    
    @DisplayField(percentage = true) protected final double defenseIgnore = 0.2;
    @DisplayField protected final short hitsForBleed = 5;
    
    protected final BloodBountyAbility ability;
    
    @DisplayField private final double maxDistance = 5.0;
    @DisplayField private final int bleedDuration = Tick.fromSeconds(5);
    
    private final Particle.DustTransition dustTransition = new Particle.DustTransition(Color.fromRGB(209, 38, 63), Color.fromRGB(135, 1, 21), 1);
    
    BountyHunterWeapon() {
        super(Material.IRON_SWORD, Key.ofString("bloodweep"));
        
        setName("Bloodweep");
        setDescription("""
                       A handy sword that appeared in her dream.
                       """);
        
        setDamage(6.0);
        
        setAbility(AbilityType.RIGHT_CLICK, ability = new BloodBountyAbility());
    }
    
    public void affect(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity) {
        entity.addEffect(EffectType.BLEED, bleedDuration, player);
        
        // Fx
        drawFx(player, entity.getLocation(), entity.getEyeHeight());
    }
    
    private void drawFx(GamePlayer player, Location location, double height) {
        final double radius = 0.8;
        
        drawLine(player, location, radius, height, radius);
        drawLine(player, location, -radius, height, radius);
        drawLine(player, location, radius, height, -radius);
        drawLine(player, location, -radius, height, -radius);
    }
    
    private void drawLine(GamePlayer player, Location location, double x, double y, double z) {
        final Location start = location.clone().add(x, y, z);
        final Location end = location.clone().subtract(x, 0, z);
        
        Geometry.drawLine(start, end, 0.25, loc -> player.spawnParticle(loc, Particle.DUST, 1, 0, 0, 0, 0, dustTransition));
    }
    
    public final class BloodBountyAbility extends Ability {
        
        BloodBountyAbility() {
            super(
                    "Blood Bounty", """
                                    Put a &cbounty&7 on the target &cenemy&7.
                                    
                                    While the bounty is active:
                                     &8├&7 You can &f&nonly&7 damage the target.
                                     &8├&7 Your hits ignore &2%.0f%%&7 of target's %s.
                                     &8├&7 Your &c%s&7 applies %s and clears the bounty.
                                    """.formatted(defenseIgnore * 100, AttributeType.DEFENSE, Chat.stNdTh(hitsForBleed), EffectType.BLEED)
            );
            
            setCooldownSec(30);
        }
        
        @Nonnull
        @Override
        public Response execute(@Nonnull GamePlayer player) {
            final BountyHunterData data = HeroRegistry.BOUNTY_HUNTER.getPlayerData(player);
            
            if (data.bounty != null) {
                return Response.error("Already has bounty!");
            }
            
            final LivingGameEntity target = Collect.targetEntityRayCast(player, maxDistance, 1.25, player::isNotSelfOrTeammateOrHasEffectResistance);
            
            if (target == null) {
                return Response.error("Not valid target!");
            }
            
            data.bounty = new BloodBounty(player, target);
            
            // Fx
            player.playSound(Sound.ITEM_FLINTANDSTEEL_USE, 0.0f);
            player.playSound(Sound.ENTITY_EVOKER_FANGS_ATTACK, 0.0f);
            
            drawFx(player, target.getLocation(), target.getEyeHeight());
            
            return Response.AWAIT; // The cooldown only starts after the bounty ends
        }
        
    }
    
}
