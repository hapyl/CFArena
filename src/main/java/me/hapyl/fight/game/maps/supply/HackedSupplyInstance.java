package me.hapyl.fight.game.maps.supply;

import me.hapyl.eterna.module.reflect.glowing.Glowing;
import me.hapyl.eterna.module.reflect.glowing.GlowingColor;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.techie.Saboteur;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class HackedSupplyInstance extends PlatformSupplyInstance {
    
    private final GamePlayer player;
    
    public HackedSupplyInstance(@Nonnull GamePlayer player, @Nonnull SupplyPlatform platform, @Nonnull Supply supply, @Nonnull Location location) {
        super(platform, supply, location);
        
        this.player = player;
        
        // Glow the entity for self and teammates
        this.player.getTeam()
                   .getPlayers()
                   .forEach(teammate -> {
                       Glowing.setGlowing(teammate.getEntity(), armorStand, GlowingColor.AQUA);
                   });
        
        // Fx
        this.player.spawnWorldParticle(location().add(0, 1, 0), Particle.ENCHANTED_HIT, 10, 0.3, 0.3, 0.3, 0.025f);
    }
    
    @Nonnull
    public GamePlayer player() {
        return player;
    }
    
    @Override
    public void onPickup(@Nonnull GamePlayer target) {
        if (player.isSelfOrTeammate(target)) {
            return;
        }
        
        final Saboteur saboteur = TalentRegistry.SABOTEUR;
        
        // Damage
        target.setLastDamager(player);
        target.damage(saboteur.hackedSupplyDamage, DamageCause.HACK);
        
        // Fx
        target.playWorldSound(Sound.ENTITY_ENDERMAN_HURT, 0.75f);
        target.playWorldSound(Sound.ENTITY_BLAZE_HURT, 0.75f);
        
        target.sendTitle("&bʜᴀᴄᴋᴇᴅ ʙʏ", "&3" + player.getName(), 0, 20, 5);
        
        target.getAttributes().addModifier(
                Saboteur.modifierSource, saboteur.impairDuration, target, modifier -> modifier
                        .of(AttributeType.ATTACK, ModifierType.ADDITIVE, saboteur.hackedSupplyAttackReduction)
                        .of(AttributeType.SPEED, ModifierType.FLAT, saboteur.hackedSupplySpeedReduction)
        );
        
        HeroRegistry.TECHIE.getPlayerData(player).bugRandomly(target);
        super.remove();
    }
}
